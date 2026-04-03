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

  it('should check health correctly', () => {
    service.checkHealth();

    const req = httpMock.expectOne('/actuator/health');
    req.flush({ status: 'UP' });

    expect(service.isBackendHealthy()).toBe(true);
  });

  it('should handle health check failure', () => {
    // Suppress error log for expected failure if possible, or just handle
    service.checkHealth();

    const req = httpMock.expectOne('/actuator/health');
    req.flush('Offline', { status: 503, statusText: 'Service Unavailable' });

    expect(service.isBackendHealthy()).toBe(false);
  });
});
