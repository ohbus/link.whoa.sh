import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { SyncService } from './sync.service';
import { ApiService } from './api.service';
import { DbService } from './db.service';

describe('SyncService', () => {
  let service: SyncService;
  let apiMock: any;
  let dbMock: any;

  beforeEach(() => {
    apiMock = {
      getBulkAnalytics: vi.fn().mockReturnValue(
        of({
          clicks: { code1: 50 },
          serverTimestamp: 123456789,
        }),
      ),
    };

    dbMock = {
      getUrls: vi.fn().mockResolvedValue([{ shortCode: 'code1', totalClicks: 10 }]),
      updateAnalytics: vi.fn().mockResolvedValue(undefined),
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

  it('should send lastServerTimestamp in subsequent requests', async () => {
    const visibleCodes = new Set(['code1']);
    const provider = () => visibleCodes;

    // First Sync (initial call)
    await service.performSync(provider);
    expect(apiMock.getBulkAnalytics).toHaveBeenCalledWith({ code1: 10 }, null);

    // Second Sync (should use timestamp from first response)
    await service.performSync(provider);
    expect(apiMock.getBulkAnalytics).toHaveBeenCalledWith({ code1: 10 }, 123456789);
  });

  it('should only update DB if clicks have changed', async () => {
    const visibleCodes = new Set(['code1']);
    const provider = () => visibleCodes;

    // Server returns same count as local
    apiMock.getBulkAnalytics.mockReturnValue(
      of({
        clicks: { code1: 10 },
        serverTimestamp: 999,
      }),
    );

    await service.performSync(provider);
    expect(dbMock.updateAnalytics).not.toHaveBeenCalled();
  });

  it('should schedule sync jobs correctly', () => {
    vi.useFakeTimers();
    const provider = vi.fn().mockReturnValue(new Set());
    
    service.startSync(provider, 1000);
    expect(provider).toHaveBeenCalledTimes(1); // Initial call
    
    vi.advanceTimersByTime(1000);
    expect(provider).toHaveBeenCalledTimes(2);
    
    service.stopSync();
    vi.advanceTimersByTime(1000);
    expect(provider).toHaveBeenCalledTimes(2);
    
    vi.useRealTimers();
  });

  it('should respect skipSync flag', () => {
    vi.useFakeTimers();
    (window as any).SyncService_skipSync = true;
    const provider = vi.fn().mockReturnValue(new Set());
    
    service.startSync(provider, 1000);
    expect(provider).not.toHaveBeenCalled();
    
    vi.advanceTimersByTime(1000);
    expect(provider).not.toHaveBeenCalled();
    
    (window as any).SyncService_skipSync = false;
    vi.useRealTimers();
  });

  it('should handle empty viewport in performSync', async () => {
    const provider = () => new Set<string>();
    await service.performSync(provider);
    expect(apiMock.getBulkAnalytics).not.toHaveBeenCalled();
  });

  it('should handle no matching links in performSync', async () => {
    const provider = () => new Set(['unknown']);
    await service.performSync(provider);
    expect(apiMock.getBulkAnalytics).not.toHaveBeenCalled();
  });

  it('should handle API error in performSync', async () => {
    const provider = () => new Set(['code1']);
    apiMock.getBulkAnalytics.mockReturnValue(throwError(() => new Error('API Fail')));
    
    await service.performSync(provider);
    expect(service.isSyncing()).toBe(false);
  });

  it('should handle generic error in performSync', async () => {
    const provider = () => new Set(['code1']);
    dbMock.getUrls.mockRejectedValue(new Error('DB Fail'));
    
    await service.performSync(provider);
    expect(service.isSyncing()).toBe(false);
  });
});
