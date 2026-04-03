import { indexedDB as freshIndexedDB } from 'fake-indexeddb';
import { TestBed } from '@angular/core/testing';
import { DbService } from './db.service';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';

describe('DbService', () => {
  let service: DbService;

  beforeEach(async () => {
    // 1. Completely isolate the IndexedDB environment for this test
    // @ts-ignore
    global.indexedDB = freshIndexedDB;

    DbService.skipSeeding = true;
    TestBed.configureTestingModule({
      providers: [DbService],
    });
    service = TestBed.inject(DbService);

    // Dexie.clear() returns a promise.
    await service.db.urls.clear();
    await service.db.analytics.clear();
  }, 30000); // 30s timeout for DB init in CI

  afterEach(async () => {
    if (service && service.db) {
      await service.db.close();
    }
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a URL correctly', async () => {
    await service.addUrl('abc', 'https://example.com', 'http://127.0.0.1:8844/abc', 123456789);

    const urls = await service.getUrls();
    expect(urls.length).toBe(1);
    expect(urls[0].shortCode).toBe('abc');
    expect(urls[0].originalUrl).toBe('https://example.com');
    expect(urls[0].createdAt).toBe(123456789);
  });

  it('should bulk add URLs correctly', async () => {
    const links = [
      {
        shortCode: 'code1',
        originalUrl: 'https://url1.com',
        shortUrl: 'http://127.0.0.1:8844/code1',
        clicks: 5,
      },
      {
        shortCode: 'code2',
        originalUrl: 'https://url2.com',
        shortUrl: 'http://127.0.0.1:8844/code2',
        clicks: 10,
      },
    ];

    await service.bulkAddUrls(links);

    const urls = await service.getUrls();
    expect(urls.length).toBe(2);
    const codes = urls.map((u) => u.shortCode);
    expect(codes).toContain('code1');
    expect(codes).toContain('code2');
  });

  it('should update analytics and add snapshot', async () => {
    await service.addUrl('abc', 'https://example.com', 'http://127.0.0.1:8844/abc');

    await service.updateAnalytics('abc', 50);

    const urls = await service.getUrls();
    const url = urls.find((u) => u.shortCode === 'abc');
    expect(url?.totalClicks).toBe(50);

    const history = await service.getAnalyticsHistory('abc');
    expect(history.length).toBe(1);
    expect(history[0].clicks).toBe(50);
  });

  it('should seed data if empty', async () => {
    await (service as any).seedDataIfEmpty();

    const urls = await service.getUrls();
    expect(urls.length).toBeGreaterThan(0);

    const initialCount = urls.length;
    await (service as any).seedDataIfEmpty();
    const finalUrls = await service.getUrls();
    expect(finalUrls.length).toBe(initialCount);
  });

  it('should update existing URLs in bulkAddUrls', async () => {
    await service.addUrl('code1', 'old', 'old');

    const links = [
      {
        shortCode: 'code1',
        originalUrl: 'new',
        shortUrl: 'new',
        clicks: 100,
        createdAt: '2026-01-01T00:00:00Z',
      },
    ];

    await service.bulkAddUrls(links);

    const urls = await service.getUrls();
    expect(urls[0].totalClicks).toBe(100);
  });

  it('should return live urls', async () => {
    await service.addUrl('abc', 'url', 'url');
    const urls = await service.liveUrls();
    expect(urls.length).toBe(1);
  });
});
