import { Injectable, signal } from '@angular/core';
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
      if (urls.length === 0) return;

      const codesToPoll = urls
        .filter(url => Date.now() - url.lastPolledAt > 15000)
        .map(url => url.shortCode);

      if (codesToPoll.length > 0) {
        this.api.getBulkAnalytics(codesToPoll).pipe(
          catchError(() => EMPTY)
        ).subscribe(async response => {
          for (const [code, clicks] of Object.entries(response.clicks)) {
            await this.db.updateAnalytics(code, clicks);
          }
        });
      }
      
      this.lastSyncTime.set(new Date());
    } finally {
      this.isSyncing.set(false);
    }
  }
}
