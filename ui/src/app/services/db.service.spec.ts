import 'fake-indexeddb/auto';
import { TestBed } from '@angular/core/testing';
import { DbService } from './db.service';
import { describe, it, expect, beforeEach } from 'vitest';

describe('DbService', () => {
  let service: DbService;

  beforeEach(async () => {
    DbService.skipSeeding = true;
    TestBed.configureTestingModule({
      providers: [DbService]
    });
    service = TestBed.inject(DbService);
    
    // Explicitly wait for seeder to finish if it started, then clear
    // Since seeder is async and constructor doesn't await it, we just clear here.
    await service.db.urls.clear();
    await service.db.analytics.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a URL correctly', async () => {
    await service.addUrl('abc', 'https://example.com', 'http://localhost:8844/abc', 123456789);
    
    const urls = await service.getUrls();
    expect(urls.length).toBe(1);
    expect(urls[0].shortCode).toBe('abc');
    expect(urls[0].originalUrl).toBe('https://example.com');
    expect(urls[0].createdAt).toBe(123456789);
  });

  it('should bulk add URLs correctly', async () => {
    const links = [
      { shortCode: 'code1', originalUrl: 'https://url1.com', shortUrl: 'http://localhost:8844/code1', clicks: 5 },
      { shortCode: 'code2', originalUrl: 'https://url2.com', shortUrl: 'http://localhost:8844/code2', clicks: 10 }
    ];

    await service.bulkAddUrls(links);
    
    const urls = await service.getUrls();
    expect(urls.length).toBe(2);
    const codes = urls.map(u => u.shortCode);
    expect(codes).toContain('code1');
    expect(codes).toContain('code2');
  });

  it('should update analytics and add snapshot', async () => {
    await service.addUrl('abc', 'https://example.com', 'http://localhost:8844/abc');
    
    await service.updateAnalytics('abc', 50);
    
    const urls = await service.getUrls();
    const url = urls.find(u => u.shortCode === 'abc');
    expect(url?.totalClicks).toBe(50);
    
    const history = await service.getAnalyticsHistory('abc');
    expect(history.length).toBe(1);
    expect(history[0].clicks).toBe(50);
  });
});
