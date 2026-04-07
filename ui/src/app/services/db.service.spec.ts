import { TestBed } from '@angular/core/testing';
import { DbService } from './db.service';
import { describe, it, expect, beforeEach } from 'vitest';

describe('DbService', () => {
  let service: DbService;

  beforeEach(async () => {
    // Disable seeding by default for tests to have clean state
    DbService.skipSeeding = true;

    TestBed.configureTestingModule({
      providers: [DbService],
    });
    service = TestBed.inject(DbService);
    await service.db.urls.clear();
    await service.db.analytics.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should seed data manually for testing', async () => {
    await (service as any).seedDataIfEmpty();
    const count = await service.db.urls.count();
    expect(count).toBeGreaterThan(0);

    // Should return early if already seeded
    await (service as any).seedDataIfEmpty();
    const countAfter = await service.db.urls.count();
    expect(countAfter).toBe(count);
  });

  it('should add a URL', async () => {
    const shortCode = 'testAdd';
    await service.addUrl(shortCode, 'https://example.com', 'http://localhost/testAdd');

    const url = await service.db.urls.get(shortCode);
    expect(url).toBeTruthy();
    expect(url?.originalUrl).toBe('https://example.com');
  });

  it('should bulk add URLs', async () => {
    const links = [
      {
        shortCode: 'b1',
        originalUrl: 'u1',
        shortUrl: 's1',
        clicks: 10,
        createdAt: new Date().toISOString(),
      },
      { shortCode: 'b2', originalUrl: 'u2', shortUrl: 's2', clicks: 20 }, // No createdAt
    ];

    await service.bulkAddUrls(links);

    const u1 = await service.db.urls.get('b1');
    expect(u1?.totalClicks).toBe(10);
    expect(u1?.createdAt).toBeDefined();

    const u2 = await service.db.urls.get('b2');
    expect(u2?.totalClicks).toBe(20);
    expect(u2?.createdAt).toBeDefined();

    // Update existing
    await service.bulkAddUrls([{ shortCode: 'b1', originalUrl: 'u1', shortUrl: 's1', clicks: 15 }]);
    const u1Updated = await service.db.urls.get('b1');
    expect(u1Updated?.totalClicks).toBe(15);
  });

  it('should update analytics', async () => {
    const shortCode = 'updateTest';
    await service.addUrl(shortCode, 'u', 's');

    await service.updateAnalytics(shortCode, 50);

    const url = await service.db.urls.get(shortCode);
    expect(url?.totalClicks).toBe(50);

    const history = await service.getAnalyticsHistory(shortCode);
    expect(history.length).toBe(1);
    expect(history[0].clicks).toBe(50);
  });

  it('should get all URLs', async () => {
    await service.addUrl('a', 'u', 's');
    const urls = await service.getUrls();
    expect(urls.length).toBeGreaterThan(0);
  });

  it('should provide liveUrls (alias for getUrls)', async () => {
    await service.addUrl('a', 'u', 's');
    const urls = await service.liveUrls();
    expect(urls.length).toBeGreaterThan(0);
  });
});
