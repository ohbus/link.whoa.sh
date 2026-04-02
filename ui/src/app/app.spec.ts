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
  value: MockIntersectionObserver
});

import 'fake-indexeddb/auto';
import { ComponentFixture, TestBed } from '@angular/core/testing';
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
      getPagedUrls: vi.fn().mockReturnValue(of({ links: [], nextCursor: null, hasMore: false }))
    };

    dbMock = {
      db: {
        urls: {
          orderBy: () => ({ reverse: () => ({ offset: () => ({ limit: () => ({ toArray: () => Promise.resolve([]) }) }) }) }),
          count: () => Promise.resolve(0),
          toArray: () => Promise.resolve([])
        }
      },
      addUrl: vi.fn().mockResolvedValue(undefined),
      bulkAddUrls: vi.fn().mockResolvedValue(undefined),
      updateAnalytics: vi.fn().mockResolvedValue(undefined),
      getAnalyticsHistory: vi.fn().mockResolvedValue([])
    };

    syncMock = {
      isSyncing: signal(false),
      startSync: vi.fn(),
      performSync: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: DbService, useValue: dbMock },
        { provide: SyncService, useValue: syncMock },
        provideHighcharts()
      ]
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
    
    component.destinationUrl.set('https://test.com');
    await component.executeShorteningTask();
    
    expect(dbMock.addUrl).toHaveBeenCalledWith('abc', 'https://test.com', 'http://loc/abc');
    expect(component.destinationUrl()).toBe('');
    expect(component.userNotificationMessage()).toContain('successfully');
  });

  it('should handle shortening error 409', async () => {
    apiMock.createShortUrl.mockReturnValue(throwError(() => ({ status: 409 })));
    
    component.destinationUrl.set('https://test.com');
    component.customShortPath.set('taken');
    await component.executeShorteningTask();
    
    expect(component.shorteningErrorMessage()).toContain('already registered');
  });

  it('should open analytics drawer', async () => {
    const mockUrl = { shortCode: 'abc', originalUrl: 'https://test.com', totalClicks: 10 } as any;
    
    await component.openAnalyticsInsightDrawer(mockUrl);
    
    expect(component.isAnalyticsDrawerOpen()).toBe(true);
    expect(component.selectedShortLinkMetadata()).toEqual(mockUrl);
    expect(apiMock.getAnalytics).toHaveBeenCalledWith('abc');
  });

  it('should close analytics drawer', () => {
    component.isAnalyticsDrawerOpen.set(true);
    component.closeAnalyticsDrawer();
    expect(component.isAnalyticsDrawerOpen()).toBe(false);
    expect(component.selectedShortLinkMetadata()).toBeNull();
  });
});
