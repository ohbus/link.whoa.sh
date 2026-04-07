import 'zone.js';
import 'zone.js/testing';

// Mock IntersectionObserver BEFORE any other imports that might use it
class MockIntersectionObserver {
  observe = vi.fn();
  disconnect = vi.fn();
  unobserve = vi.fn();
}

Object.defineProperty(window, 'IntersectionObserver', {
  writable: true,
  configurable: true,
  value: MockIntersectionObserver,
});

import 'fake-indexeddb/auto';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { signal } from '@angular/core';
import { AppComponent } from './app';
import { ApiService } from './services/api.service';
import { DbService } from './services/db.service';
import { SyncService } from './services/sync.service';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { provideHighcharts } from 'highcharts-angular';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let apiMock: any;
  let dbMock: any;
  let syncMock: any;

  beforeEach(async () => {
    apiMock = {
      isBackendHealthy: signal(true),
      checkHealth: vi.fn(),
      checkHealthRaw: vi.fn().mockReturnValue(of({ status: 'UP' })),
      getGlobalClicks: vi.fn().mockReturnValue(of({ totalClicks: 100 })),
      createShortUrl: vi.fn(),
      getAnalytics: vi.fn().mockReturnValue(of({ clicks: 10 })),
      getPagedUrls: vi.fn().mockReturnValue(of({ links: [], nextCursor: null, hasMore: false })),
    };

    dbMock = {
      db: {
        urls: {
          orderBy: () => ({
            reverse: () => ({
              offset: () => ({ limit: () => ({ toArray: () => Promise.resolve([]) }) }),
            }),
          }),
          count: () => Promise.resolve(0),
          toArray: () => Promise.resolve([]),
        },
      },
      addUrl: vi.fn().mockResolvedValue(undefined),
      bulkAddUrls: vi.fn().mockResolvedValue(undefined),
      updateAnalytics: vi.fn().mockResolvedValue(undefined),
      getAnalyticsHistory: vi.fn().mockResolvedValue([]),
    };

    syncMock = {
      isSyncing: signal(false),
      startSync: vi.fn(),
      performSync: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: DbService, useValue: dbMock },
        { provide: SyncService, useValue: syncMock },
        provideHighcharts(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle sidebar', () => {
    const initial = component.isSidebarCollapsed();
    component.toggleSidebarExpansion();
    expect(component.isSidebarCollapsed()).toBe(!initial);
  });

  it('should handle successful shortening', async () => {
    const mockResponse = { originalUrl: 'https://test.com', shortUrl: 'http://loc/abc' };
    apiMock.createShortUrl.mockReturnValue(of(mockResponse));

    component.shorteningForm.patchValue({ destinationUrl: 'https://test.com' });
    await component.executeShorteningTask();

    expect(dbMock.addUrl).toHaveBeenCalledWith('abc', 'https://test.com', 'http://loc/abc');
    expect(component.shorteningForm.get('destinationUrl')?.value).toBe(null); // After reset
    expect(component.userNotificationMessage()).toContain('successfully');
  });

  it('should handle shortening error 409', async () => {
    apiMock.createShortUrl.mockReturnValue(throwError(() => ({ status: 409 })));

    component.shorteningForm.patchValue({
      destinationUrl: 'https://test.com',
      customShortPath: 'taken',
    });
    await component.executeShorteningTask();

    expect(component.shorteningErrorMessage()).toContain('already registered');
  });

  it('should handle shortening generic error', async () => {
    apiMock.createShortUrl.mockReturnValue(throwError(() => ({ status: 500 })));

    component.shorteningForm.patchValue({
      destinationUrl: 'https://test.com',
    });
    await component.executeShorteningTask();

    expect(component.shorteningErrorMessage()).toContain('unexpected system error');
  });

  it('should not execute shortening if form is invalid', async () => {
    component.shorteningForm.patchValue({ destinationUrl: 'invalid-url' });
    await component.executeShorteningTask();
    expect(apiMock.createShortUrl).not.toHaveBeenCalled();
  });

  it('should open analytics drawer', async () => {
    const mockUrl = { shortCode: 'abc', originalUrl: 'https://test.com', totalClicks: 10 } as any;

    await component.openAnalyticsInsightDrawer(mockUrl);

    expect(component.isAnalyticsDrawerOpen()).toBe(true);
    expect(component.selectedShortLinkMetadata()).toEqual(mockUrl);
    expect(apiMock.getAnalytics).toHaveBeenCalledWith('abc');
  });

  it('should close analytics drawer', async () => {
    component.isAnalyticsDrawerOpen.set(true);
    component.closeAnalyticsDrawer();
    expect(component.isAnalyticsDrawerOpen()).toBe(false);
    expect(component.selectedShortLinkMetadata()).toBeNull();
  });

  it('should focus destination input', () => {
    // JSDOM doesn't implement scrollIntoView
    component.destinationUrlInput.nativeElement.scrollIntoView = vi.fn();
    const focusSpy = vi.spyOn(component.destinationUrlInput.nativeElement, 'focus');
    const scrollSpy = vi.spyOn(component.destinationUrlInput.nativeElement, 'scrollIntoView');

    component.focusDestinationInput();
    expect(focusSpy).toHaveBeenCalled();
    expect(scrollSpy).toHaveBeenCalled();
  });

  it('should copy to clipboard and show notification', async () => {
    vi.useFakeTimers();
    const mockClipboard = {
      writeText: vi.fn().mockResolvedValue(undefined),
    };
    // @ts-ignore
    navigator.clipboard = mockClipboard;

    component.copyToClipboard('some-text');
    expect(mockClipboard.writeText).toHaveBeenCalledWith('some-text');
    expect(component.userNotificationMessage()).toBe('Link copied to clipboard!');

    vi.advanceTimersByTime(3000);
    expect(component.userNotificationMessage()).toBeNull();
    vi.useRealTimers();
  });

  it('should navigate between pages', () => {
    component.totalRegistryCount.set(25); // 3 pages if limit is 10
    component.shortLinkRegistry.set([{ createdAt: 100 } as any]);

    component.navigateToNextPage();
    expect(component.currentPageNumber()).toBe(2);

    component.navigateToNextPage();
    expect(component.currentPageNumber()).toBe(3);

    // Should not go past last page
    component.navigateToNextPage();
    expect(component.currentPageNumber()).toBe(3);

    component.navigateToPreviousPage();
    expect(component.currentPageNumber()).toBe(2);

    component.navigateToPreviousPage();
    expect(component.currentPageNumber()).toBe(1);

    // Should not go past first page
    component.navigateToPreviousPage();
    expect(component.currentPageNumber()).toBe(1);
  });

  it('should handle hovering over short links', () => {
    vi.useFakeTimers();
    const shortLink = { shortCode: 'abc' } as any;
    const analyticsSpy = vi.spyOn(apiMock, 'getAnalytics');

    component.onShortLinkHover(shortLink);
    vi.advanceTimersByTime(300);

    expect(analyticsSpy).toHaveBeenCalledWith('abc');

    // Test hover end
    component.onShortLinkHover(shortLink);
    component.onShortLinkHoverEnd();
    vi.advanceTimersByTime(300);
    expect(analyticsSpy).toHaveBeenCalledTimes(1); // Not called again

    // Test hover end when no timer
    component.onShortLinkHoverEnd(); // Should not throw

    vi.useRealTimers();
  });

  it('should poll global clicks and health', () => {
    vi.useFakeTimers();
    const globalClicksSpy = vi.spyOn(apiMock, 'getGlobalClicks');
    const healthSpy = vi.spyOn(apiMock, 'checkHealthRaw');

    // Re-run ngOnInit to ensure intervals use fake timers
    component.ngOnInit();

    vi.advanceTimersByTime(10000);
    expect(globalClicksSpy).toHaveBeenCalled();

    // Successful health check
    vi.advanceTimersByTime(5000); // Total 15000
    expect(healthSpy).toHaveBeenCalled();
    expect(component.apiLatency()).toBeGreaterThanOrEqual(0);

    // Failed health check
    healthSpy.mockReturnValue(throwError(() => new Error('Health check failed')));
    vi.advanceTimersByTime(15000);
    expect(healthSpy).toHaveBeenCalledTimes(2);

    vi.useRealTimers();
  });

  it('should cleanup observer on destroy', () => {
    const disconnectSpy = vi.fn();
    // @ts-ignore
    component.viewportIntersectionObserver = { disconnect: disconnectSpy };

    component.ngOnDestroy();
    expect(disconnectSpy).toHaveBeenCalled();
  });

  it('should handle navigation edge cases', () => {
    // Empty registry
    component.shortLinkRegistry.set([]);
    component.navigateToNextPage();
    expect(component.currentPageNumber()).toBe(1);
  });

  it('should handle analytics fetch error', async () => {
    vi.useFakeTimers();
    apiMock.getAnalytics.mockReturnValue(throwError(() => new Error('Fail')));

    // @ts-ignore - calling private method
    await component.fetchLatestDetailedAnalytics('abc');

    vi.advanceTimersByTime(800);
    expect(component.isFetchingDetailedAnalytics()).toBe(false);
    vi.useRealTimers();
  });

  it('should expose global WhoaApp hooks', () => {
    const whoaApp = (window as any).WhoaApp;
    expect(whoaApp).toBeDefined();

    // Test forceRefreshAnalytics
    const globalClicksSpy = vi.spyOn(apiMock, 'getGlobalClicks');
    whoaApp.forceRefreshAnalytics();
    expect(globalClicksSpy).toHaveBeenCalled();

    // Test forceHealthCheck
    const healthSpy = vi.spyOn(apiMock, 'checkHealthRaw');
    whoaApp.forceHealthCheck();
    expect(healthSpy).toHaveBeenCalled();

    // Test getRegistryCount
    component.totalRegistryCount.set(42);
    expect(whoaApp.getRegistryCount()).toBe(42);
  });

  it('should initialize viewport observer and handle changes', () => {
    vi.useFakeTimers();
    let observerCallback: any;
    class MockIntersectionObserver {
      constructor(cb: any) {
        observerCallback = cb;
      }
      observe = vi.fn();
      disconnect = vi.fn();
      unobserve = vi.fn();
    }
    // @ts-ignore
    window.IntersectionObserver = MockIntersectionObserver;

    // Mock ViewChildren QueryList
    const mockRows = [{ nativeElement: document.createElement('tr') }];
    // @ts-ignore
    component.shortLinkTableRows = {
      changes: of(mockRows),
      forEach: (cb: any) => mockRows.forEach(cb),
    };

    // @ts-ignore
    component.initViewportObserver();

    expect(observerCallback).toBeDefined();

    // Trigger callback with multiple entries
    const entries = [
      {
        target: { getAttribute: () => 'abc' },
        isIntersecting: true,
      },
      {
        target: { getAttribute: () => 'def' },
        isIntersecting: false,
      },
      {
        target: { getAttribute: () => null }, // Branch coverage for if(shortCode)
        isIntersecting: true,
      }
    ];
    observerCallback(entries);

    // Verify sync was scheduled
    vi.advanceTimersByTime(500);
    expect(syncMock.performSync).toHaveBeenCalled();
    vi.useRealTimers();
  });
});
