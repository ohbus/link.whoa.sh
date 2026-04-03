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
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

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
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });

  it('should handle error 500 when creating short URL', () => {
    const request: CreateShortUrlRequest = { url: 'https://example.com' };

    service.createShortUrl(request).subscribe({
      error: (err) => {
        expect(err.status).toBe(500);
        expect(service.isBackendHealthy()).toBe(false);
      },
    });

    const req = httpMock.expectOne('/api/v1/urls');
    req.flush('Internal Server Error', { status: 500, statusText: 'Server Error' });
  });

  it('should get analytics for a short code', () => {
    const mockResponse: UrlAnalyticsResponse = {
      originalUrl: 'https://example.com',
      shortUrl: 'http://localhost:8844/abc',
      clicks: 100,
    };

    service.getAnalytics('abc').subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/v1/urls/abc/analytics');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get bulk analytics', () => {
    const currentCounts = { code1: 10 };
    const mockResponse = { clicks: { code1: 15 }, serverTimestamp: 12345 };

    service.getBulkAnalytics(currentCounts, null).subscribe((res) => {
      expect(res.clicks['code1']).toBe(15);
    });

    const req = httpMock.expectOne('/api/v1/urls/analytics/bulk');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should get global clicks', () => {
    const mockResponse: GlobalClicksResponse = { totalClicks: 1000, serverTimestamp: 12345 };

    service.getGlobalClicks().subscribe((res) => {
      expect(res.totalClicks).toBe(1000);
    });

    const req = httpMock.expectOne('/api/v1/urls/analytics/global');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get paged URLs', () => {
    const mockResponse: PagedUrlsResponse = { links: [], nextCursor: null, hasMore: false };

    service.getPagedUrls(null, 5).subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/v1/urls?limit=5');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get paged URLs with cursor', () => {
    const mockResponse: PagedUrlsResponse = { links: [], nextCursor: 123, hasMore: true };

    service.getPagedUrls(123, 10).subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/v1/urls?limit=10&cursor=123');
    req.flush(mockResponse);
  });

  it('should handle error in getAnalytics', () => {
    service.getAnalytics('abc').subscribe({
      error: (err) => {
        expect(err.status).toBe(500);
        expect(service.isBackendHealthy()).toBe(false);
      },
    });

    const req = httpMock.expectOne('/api/v1/urls/abc/analytics');
    req.flush('Error', { status: 500, statusText: 'Error' });
  });

  it('should handle error in getBulkAnalytics', () => {
    service.getBulkAnalytics({}, null).subscribe({
      error: (err) => {
        expect(err.status).toBe(500);
        expect(service.isBackendHealthy()).toBe(false);
      },
    });

    const req = httpMock.expectOne('/api/v1/urls/analytics/bulk');
    req.flush('Error', { status: 500, statusText: 'Error' });
  });

  it('should handle error in getPagedUrls', () => {
    service.getPagedUrls(null).subscribe({
      error: (err) => {
        expect(err.status).toBe(500);
        expect(service.isBackendHealthy()).toBe(false);
      },
    });

    const req = httpMock.expectOne('/api/v1/urls?limit=10');
    req.flush('Error', { status: 500, statusText: 'Error' });
  });

  it('should handle error in getGlobalClicks', () => {
    service.getGlobalClicks().subscribe({
      error: (err) => {
        expect(err.status).toBe(500);
        expect(service.isBackendHealthy()).toBe(false);
      },
    });

    const req = httpMock.expectOne('/api/v1/urls/analytics/global');
    req.flush('Error', { status: 500, statusText: 'Error' });
  });

  it('should check health raw correctly', () => {
    service.checkHealthRaw().subscribe((res) => {
      expect(res.status).toBe('UP');
      expect(service.isBackendHealthy()).toBe(true);
    });

    const req = httpMock.expectOne('/actuator/health');
    req.flush({ status: 'UP' });
  });

  it('should handle error in checkHealthRaw', () => {
    service.checkHealthRaw().subscribe({
      error: (err) => {
        expect(service.isBackendHealthy()).toBe(false);
      },
    });

    const req = httpMock.expectOne('/actuator/health');
    req.flush('Down', { status: 503, statusText: 'Down' });
  });
});
