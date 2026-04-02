import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService, PagedUrlsResponse } from './api.service';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch paged URLs with cursor', () => {
    const mockResponse: PagedUrlsResponse = {
      links: [],
      nextCursor: 12345,
      hasMore: true
    };

    service.getPagedUrls(1000).subscribe(res => {
      expect(res.nextCursor).toBe(12345);
    });

    const req = httpMock.expectOne(request => 
      request.url === '/api/v1/urls' && 
      request.params.get('cursor') === '1000' &&
      request.params.get('limit') === '10'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
