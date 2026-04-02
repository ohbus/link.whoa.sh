import { Injectable, signal } from '@angular/core';
import { ApiService } from './api.service';
import { DbService, LocalUrl } from './db.service';
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

  startSync(visibleCodesGetter: () => Set<string>, intervalMs: number = 60000) {
    this.stopSync();
    // Immediate first sync
    this.performSync(visibleCodesGetter);
    
    this.syncIntervalId = setInterval(() => {
      this.performSync(visibleCodesGetter);
    }, intervalMs);
  }

  stopSync() {
    if (this.syncIntervalId) {
      clearInterval(this.syncIntervalId);
    }
  }

  /**
   * Performs a delta sync for visible items.
   */
  async performSync(visibleCodesGetter: () => Set<string>) {
    if (this.isSyncing()) return;
    
    const visibleCodes = visibleCodesGetter();
    if (visibleCodes.size === 0) return;

    this.isSyncing.set(true);

    try {
      const allUrls = await this.db.getUrls();
      const visibleUrls = allUrls.filter(u => visibleCodes.has(urlToCode(u)));
      
      if (visibleUrls.length === 0) return;

      // Create the intent-based Map: shortCode -> currentTotal
      const currentCounts: { [key: string]: number } = {};
      visibleUrls.forEach(u => {
        currentCounts[urlToCode(u)] = u.totalClicks;
      });

      this.api.getBulkAnalytics(currentCounts).pipe(
        catchError(() => EMPTY)
      ).subscribe(async response => {
        for (const [code, clicks] of Object.entries(response.clicks)) {
          // Only update if authoritative count differs (Delta)
          if (currentCounts[code] !== clicks) {
            await this.db.updateAnalytics(code, clicks);
          }
        }
      });
      
      this.lastSyncTime.set(new Date());
    } finally {
      this.isSyncing.set(false);
    }
  }
}

function urlToCode(url: LocalUrl): string {
  return url.shortCode;
}
