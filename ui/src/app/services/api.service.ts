import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
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
}

export interface BulkAnalyticsResponse {
  clicks: { [key: string]: number };
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

  getBulkAnalytics(shortCodes: string[]) {
    return this.http.post<BulkAnalyticsResponse>(`${this.baseUrl}/analytics/bulk`, { shortCodes }).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 0 || error.status >= 500) {
          this.isBackendHealthy.set(false);
        }
        return throwError(() => error);
      })
    );
  }

  checkHealth() {
    return this.http.get<{status: string}>('/actuator/health').pipe(
      catchError((error) => {
        this.isBackendHealthy.set(false);
        return throwError(() => error);
      })
    ).subscribe(res => {
      this.isBackendHealthy.set(res.status === 'UP');
    });
  }
}
