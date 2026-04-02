import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export interface CreateShortUrlRequest {
  url: string;
  shortCode?: string;
}

export interface CreateShortUrlResponse {
  originalUrl: string;
  shortUrl: string;
}

export interface UrlAnalyticsResponse {
  originalUrl: string;
  shortUrl: string;
  clicks: number;
  createdAt?: string; // ISO string from backend
}

export interface BulkAnalyticsResponse {
  clicks: { [key: string]: number };
  serverTimestamp: number;
}

export interface PagedUrlsResponse {
  links: UrlAnalyticsResponse[];
  nextCursor: number | null;
  hasMore: boolean;
}

export interface GlobalClicksResponse {
  totalClicks: number;
  serverTimestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = '/api/v1/urls';
  
  // Expose backend health status
  isBackendHealthy = signal<boolean>(true);

  constructor(private http: HttpClient) {}

  createShortUrl(request: CreateShortUrlRequest) {
    return this.http.post<CreateShortUrlResponse>(this.baseUrl, request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 0 || error.status >= 500) {
          this.isBackendHealthy.set(false);
        }
        return throwError(() => error);
      })
    );
  }

  getAnalytics(shortCode: string) {
    return this.http.get<UrlAnalyticsResponse>(`${this.baseUrl}/${shortCode}/analytics`).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 0 || error.status >= 500) {
          this.isBackendHealthy.set(false);
        }
        return throwError(() => error);
      })
    );
  }

  getBulkAnalytics(currentCounts: { [key: string]: number }, lastSyncedAt: number | null) {
    return this.http.post<BulkAnalyticsResponse>(`${this.baseUrl}/analytics/bulk`, { currentCounts, lastSyncedAt }).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 0 || error.status >= 500) {
          this.isBackendHealthy.set(false);
        }
        return throwError(() => error);
      })
    );
  }

  getPagedUrls(cursor: number | null, limit: number = 10) {
    let params = new HttpParams().set('limit', limit.toString());
    if (cursor) {
      params = params.set('cursor', cursor.toString());
    }
    
    return this.http.get<PagedUrlsResponse>(this.baseUrl, { params }).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 0 || error.status >= 500) {
          this.isBackendHealthy.set(false);
        }
        return throwError(() => error);
      })
    );
  }

  getGlobalClicks() {
    return this.http.get<GlobalClicksResponse>(`${this.baseUrl}/analytics/global`).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 0 || error.status >= 500) {
          this.isBackendHealthy.set(false);
        }
        return throwError(() => error);
      })
    );
  }

  checkHealth() {
    this.checkHealthRaw().subscribe(res => {
      this.isBackendHealthy.set(res.status === 'UP');
    });
  }

  checkHealthRaw() {
    return this.http.get<{status: string}>('/actuator/health').pipe(
      catchError((error) => {
        this.isBackendHealthy.set(false);
        return throwError(() => error);
      })
    );
  }
}
