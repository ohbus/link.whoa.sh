import { Injectable, signal } from '@angular/core';
import { ApiService } from './api.service';
import { DbService } from './db.service';
import { catchError, EMPTY } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SyncService {
  private scheduledSyncJobId: any;
  
  // State
  isSyncing = signal<boolean>(false);
  lastSuccessfulSyncTimestamp = signal<Date | null>(null);

  constructor(
    private shortLinkApi: ApiService,
    private localDatabase: DbService
  ) {}

  startSync(visibleCodesProvider: () => Set<string>, syncIntervalMs: number = 60000) {
    this.stopSync();
    
    // Initial execution
    this.performSync(visibleCodesProvider);
    
    this.scheduledSyncJobId = setInterval(() => {
      this.performSync(visibleCodesProvider);
    }, syncIntervalMs);
  }

  stopSync() {
    if (this.scheduledSyncJobId) {
      clearInterval(this.scheduledSyncJobId);
    }
  }

  /**
   * Synchronizes click counts for items currently in the user's viewport.
   */
  async performSync(visibleCodesProvider: () => Set<string>) {
    if (this.isSyncing()) return;
    
    const codesInViewport = visibleCodesProvider();
    if (codesInViewport.size === 0) return;

    this.isSyncing.set(true);

    try {
      const allRegisteredLinks = await this.localDatabase.getUrls();
      const visibleLinks = allRegisteredLinks.filter(link => codesInViewport.has(link.shortCode));
      
      if (visibleLinks.length === 0) return;

      // Construct request map: [shortCode] -> [localClickCount]
      const localStateMap: { [shortCode: string]: number } = {};
      visibleLinks.forEach(link => {
        localStateMap[link.shortCode] = link.totalClicks;
      });

      this.shortLinkApi.getBulkAnalytics(localStateMap).pipe(
        catchError(() => EMPTY)
      ).subscribe(async serverResponse => {
        for (const [shortCode, serverClickCount] of Object.entries(serverResponse.clicks)) {
          // Perform delta update only if server data has diverged
          if (localStateMap[shortCode] !== serverClickCount) {
            await this.localDatabase.updateAnalytics(shortCode, serverClickCount);
          }
        }
      });
      
      this.lastSuccessfulSyncTimestamp.set(new Date());
    } finally {
      this.isSyncing.set(false);
    }
  }
}
