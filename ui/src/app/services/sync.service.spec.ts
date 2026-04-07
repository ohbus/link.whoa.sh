import { TestBed } from '@angular/core/testing';
import { SyncService } from './sync.service';
import { ApiService } from './api.service';
import { DbService } from './db.service';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('SyncService', () => {
  let service: SyncService;
  let apiMock: any;
  let dbMock: any;

  beforeEach(() => {
    apiMock = {
      getBulkAnalytics: vi.fn(),
    };
    dbMock = {
      getUrls: vi.fn().mockResolvedValue([]),
      updateAnalytics: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        SyncService,
        { provide: ApiService, useValue: apiMock },
        { provide: DbService, useValue: dbMock },
      ],
    });
    service = TestBed.inject(SyncService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not sync if already syncing', async () => {
    service.isSyncing.set(true);
    await service.performSync(() => new Set(['abc']));
    expect(apiMock.getBulkAnalytics).not.toHaveBeenCalled();
  });

  it('should not sync if viewport is empty', async () => {
    await service.performSync(() => new Set());
    expect(apiMock.getBulkAnalytics).not.toHaveBeenCalled();
  });

  it('should not sync if no visible links are registered', async () => {
    dbMock.getUrls.mockResolvedValue([{ shortCode: 'other' }]);
    await service.performSync(() => new Set(['abc']));
    expect(apiMock.getBulkAnalytics).not.toHaveBeenCalled();
    expect(service.isSyncing()).toBe(false);
  });

  it('should perform sync successfully', async () => {
    const visibleCodes = new Set(['abc']);
    dbMock.getUrls.mockResolvedValue([{ shortCode: 'abc', totalClicks: 10 }]);
    apiMock.getBulkAnalytics.mockReturnValue(of({
      clicks: { abc: 15 },
      serverTimestamp: 12345
    }));

    await service.performSync(() => visibleCodes);

    expect(dbMock.updateAnalytics).toHaveBeenCalledWith('abc', 15);
    expect(service.isSyncing()).toBe(false);
    expect((service as any).lastServerTimestamp).toBe(12345);
    expect(service.lastSuccessfulSyncTimestamp()).toBeTruthy();
  });

  it('should handle API error during sync', async () => {
    const visibleCodes = new Set(['abc']);
    dbMock.getUrls.mockResolvedValue([{ shortCode: 'abc', totalClicks: 10 }]);
    apiMock.getBulkAnalytics.mockReturnValue(throwError(() => new Error('API Error')));

    await service.performSync(() => visibleCodes);

    expect(service.isSyncing()).toBe(false);
  });

  it('should handle error in getUrls', async () => {
    const visibleCodes = new Set(['abc']);
    dbMock.getUrls.mockRejectedValue(new Error('DB Error'));

    await service.performSync(() => visibleCodes);

    expect(service.isSyncing()).toBe(false);
  });

  it('should start and stop scheduled sync', async () => {
    vi.useFakeTimers();
    const provider = () => new Set(['abc']);
    
    // Mock successful sync to ensure isSyncing is reset
    dbMock.getUrls.mockResolvedValue([{ shortCode: 'abc', totalClicks: 10 }]);
    apiMock.getBulkAnalytics.mockReturnValue(of({ clicks: {}, serverTimestamp: 1 }));

    service.startSync(provider, 1000);
    
    expect(service['scheduledSyncJobId']).toBeDefined();
    
    // The first call happens immediately. We need to let it finish.
    await Promise.resolve(); // Flush microtasks
    
    vi.advanceTimersByTime(1000);
    await Promise.resolve();
    
    expect(dbMock.getUrls).toHaveBeenCalledTimes(2);

    service.stopSync();
    vi.advanceTimersByTime(1000);
    expect(dbMock.getUrls).toHaveBeenCalledTimes(2); 
    vi.useRealTimers();
  });

  it('should skip sync if skipSync is true', () => {
    (window as any).SyncService_skipSync = true;
    const provider = () => new Set(['abc']);
    service.startSync(provider, 1000);
    
    expect(dbMock.getUrls).not.toHaveBeenCalled();
    
    (window as any).SyncService_skipSync = false;
  });
});
