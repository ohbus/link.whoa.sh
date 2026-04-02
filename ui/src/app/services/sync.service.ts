import { Injectable, signal, effect } from '@angular/core';
import { ApiService } from './api.service';
import { DbService } from './db.service';
import { catchError, EMPTY } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SyncService {
  private syncIntervalId: any;
  
  // State
  isSyncing = signal<boolean>(false);
  lastSyncTime = signal<Date | null>(null);

  constructor(
    private api: ApiService,
    private db: DbService
  ) {}

  startSync(intervalMs: number = 60000) {
    this.stopSync();
    // Immediate first sync
    this.performSync();
    
    this.syncIntervalId = setInterval(() => {
      this.performSync();
    }, intervalMs);
  }

  stopSync() {
    if (this.syncIntervalId) {
      clearInterval(this.syncIntervalId);
    }
  }

  private async performSync() {
    if (this.isSyncing()) return;
    this.isSyncing.set(true);

    try {
      const urls = await this.db.getUrls();
      
      // We will batch process or do them sequentially to not hammer the API
      for (const url of urls) {
        // Simple decay logic: only update if it hasn't been updated in the last 15 seconds
        if (Date.now() - url.lastPolledAt > 15000) {
          this.api.getAnalytics(url.shortCode).pipe(
            catchError(() => EMPTY) // Ignore 404s or network errors during background sync
          ).subscribe(async response => {
            await this.db.updateAnalytics(url.shortCode, response.clicks);
          });
        }
      }
      this.lastSyncTime.set(new Date());
    } finally {
      this.isSyncing.set(false);
    }
  }
}
