import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import {
  ApiService,
  PagedUrlsResponse,
  CreateShortUrlRequest,
  CreateShortUrlResponse,
  UrlAnalyticsResponse,
  GlobalClicksResponse,
} from './api.service';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService],
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a short URL', () => {
    const request: CreateShortUrlRequest = { url: 'https://example.com', shortCode: 'custom' };
    const mockResponse: CreateShortUrlResponse = {
      originalUrl: 'https://example.com',
      shortUrl: 'http://localhost:8844/custom',
    };

    service.createShortUrl(request).subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/v1/urls');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should handle errors in createShortUrl', () => {
    const request: CreateShortUrlRequest = { url: 'https://example.com' };

    // Test 500
    service.createShortUrl(request).subscribe({
      error: (err) => expect(service.isBackendHealthy()).toBe(false),
    });
    httpMock.expectOne('/api/v1/urls').flush('Error', { status: 500, statusText: 'Error' });

    // Test 0
    service.isBackendHealthy.set(true);
    service.createShortUrl(request).subscribe({
      error: (err) => expect(service.isBackendHealthy()).toBe(false),
    });
    httpMock.expectOne('/api/v1/urls').error(new ProgressEvent('Error'));

    // Test 400
    service.isBackendHealthy.set(true);
    service.createShortUrl(request).subscribe({
      error: (err) => expect(service.isBackendHealthy()).toBe(true),
    });
    httpMock.expectOne('/api/v1/urls').flush('Error', { status: 400, statusText: 'Bad Request' });
  });

  it('should handle errors in getAnalytics', () => {
    service.getAnalytics('abc').subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/urls/abc/analytics').error(new ProgressEvent('Error'));
    expect(service.isBackendHealthy()).toBe(false);

    service.isBackendHealthy.set(true);
    service.getAnalytics('abc').subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/urls/abc/analytics').flush('Error', { status: 404, statusText: 'Not Found' });
    expect(service.isBackendHealthy()).toBe(true);
  });

  it('should handle errors in getBulkAnalytics', () => {
    service.getBulkAnalytics({}, null).subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/urls/analytics/bulk').error(new ProgressEvent('Error'));
    expect(service.isBackendHealthy()).toBe(false);

    service.isBackendHealthy.set(true);
    service.getBulkAnalytics({}, null).subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/urls/analytics/bulk').flush('Error', { status: 400, statusText: 'Error' });
    expect(service.isBackendHealthy()).toBe(true);
  });

  it('should handle errors in getPagedUrls', () => {
    service.getPagedUrls(null).subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/urls?limit=10').error(new ProgressEvent('Error'));
    expect(service.isBackendHealthy()).toBe(false);

    service.isBackendHealthy.set(true);
    service.getPagedUrls(null).subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/urls?limit=10').flush('Error', { status: 401, statusText: 'Unauthorized' });
    expect(service.isBackendHealthy()).toBe(true);
  });

  it('should handle errors in getGlobalClicks', () => {
    service.getGlobalClicks().subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/urls/analytics/global').error(new ProgressEvent('Error'));
    expect(service.isBackendHealthy()).toBe(false);

    service.isBackendHealthy.set(true);
    service.getGlobalClicks().subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/urls/analytics/global').flush('Error', { status: 403, statusText: 'Forbidden' });
    expect(service.isBackendHealthy()).toBe(true);
  });

  it('should check health correctly', () => {
    service.checkHealth();
    httpMock.expectOne('/actuator/health').flush({ status: 'UP' });
    expect(service.isBackendHealthy()).toBe(true);

    service.checkHealth();
    httpMock.expectOne('/actuator/health').flush({ status: 'DOWN' });
    expect(service.isBackendHealthy()).toBe(false);
  });

  it('should check health raw correctly', () => {
    service.checkHealthRaw().subscribe((res) => {
      expect(res.status).toBe('UP');
    });
    httpMock.expectOne('/actuator/health').flush({ status: 'UP' });
    
    service.checkHealthRaw().subscribe({ error: () => {} });
    httpMock.expectOne('/actuator/health').flush('Down', { status: 500, statusText: 'Down' });
    expect(service.isBackendHealthy()).toBe(false);
  });

  it('should handle paged URLs with cursor', () => {
    service.getPagedUrls(123).subscribe();
    const req = httpMock.expectOne('/api/v1/urls?limit=10&cursor=123');
    req.flush({ links: [], nextCursor: null, hasMore: false });
  });

  it('should get analytics success', () => {
    service.getAnalytics('abc').subscribe();
    httpMock.expectOne('/api/v1/urls/abc/analytics').flush({});
  });

  it('should get bulk analytics success', () => {
    service.getBulkAnalytics({a: 1}, 123).subscribe();
    httpMock.expectOne('/api/v1/urls/analytics/bulk').flush({ clicks: {}, serverTimestamp: 0 });
  });

  it('should get global clicks success', () => {
    service.getGlobalClicks().subscribe();
    httpMock.expectOne('/api/v1/urls/analytics/global').flush({ totalClicks: 0, serverTimestamp: 0 });
  });
});
