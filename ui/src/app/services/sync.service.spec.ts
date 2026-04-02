import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
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
      getBulkAnalytics: vi.fn().mockReturnValue(of({
        clicks: { 'code1': 50 },
        serverTimestamp: 123456789
      }))
    };

    dbMock = {
      getUrls: vi.fn().mockResolvedValue([
        { shortCode: 'code1', totalClicks: 10 }
      ]),
      updateAnalytics: vi.fn().mockResolvedValue(undefined)
    };

    TestBed.configureTestingModule({
      providers: [
        SyncService,
        { provide: ApiService, useValue: apiMock },
        { provide: DbService, useValue: dbMock }
      ]
    });
    service = TestBed.inject(SyncService);
  });

  it('should send lastServerTimestamp in subsequent requests', async () => {
    const visibleCodes = new Set(['code1']);
    const provider = () => visibleCodes;

    // First Sync (initial call)
    await service.performSync(provider);
    expect(apiMock.getBulkAnalytics).toHaveBeenCalledWith({ 'code1': 10 }, null);

    // Second Sync (should use timestamp from first response)
    await service.performSync(provider);
    expect(apiMock.getBulkAnalytics).toHaveBeenCalledWith({ 'code1': 10 }, 123456789);
  });

  it('should only update DB if clicks have changed', async () => {
    const visibleCodes = new Set(['code1']);
    const provider = () => visibleCodes;

    // Server returns same count as local
    apiMock.getBulkAnalytics.mockReturnValue(of({
      clicks: { 'code1': 10 },
      serverTimestamp: 999
    }));

    await service.performSync(provider);
    expect(dbMock.updateAnalytics).not.toHaveBeenCalled();
  });
});
