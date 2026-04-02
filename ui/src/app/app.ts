import { Component, OnInit, signal, computed, inject, effect, ViewChild, ElementRef, AfterViewInit, OnDestroy, ViewChildren, QueryList } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { liveQuery } from 'dexie';
import { HighchartsChartComponent } from 'highcharts-angular';
import * as Highcharts from 'highcharts';

import { ApiService, CreateShortUrlRequest } from './services/api.service';
import { DbService, LocalUrl, AnalyticsSnapshot } from './services/db.service';
import { SyncService } from './services/sync.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HighchartsChartComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('urlInput') destinationUrlInput!: ElementRef<HTMLInputElement>;
  @ViewChildren('urlRow') shortLinkTableRows!: QueryList<ElementRef<HTMLTableRowElement>>;

  // Domain Services
  private shortLinkApi = inject(ApiService);
  private localDatabase = inject(DbService);
  private backgroundSync = inject(SyncService);

  Highcharts: typeof Highcharts = Highcharts;

  // UI State: Sidebar & Form
  isSidebarCollapsed = signal<boolean>(false);
  destinationUrl = signal<string>('');
  customShortPath = signal<string>('');
  
  isShorteningInProgress = signal<boolean>(false);
  shorteningErrorMessage = signal<string | null>(null);
  userNotificationMessage = signal<string | null>(null);

  // UI State: Viewport Tracking
  private currentlyVisibleShortCodes = new Set<string>();
  private viewportIntersectionObserver?: IntersectionObserver;
  private scrollRestStabilizationTimer: any;

  // UI State: Analytics Drawer
  isAnalyticsDrawerOpen = signal<boolean>(false);
  selectedShortLinkMetadata = signal<LocalUrl | null>(null);
  historicalAnalyticsSnapshots = signal<AnalyticsSnapshot[]>([]);
  isFetchingDetailedAnalytics = signal<boolean>(false);

  private prefetchHoverTimer: any;

  // Data Visualization Configuration
  clickVelocityChartOptions: Highcharts.Options = {
    title: { text: 'Clicks Over Time', style: { color: '#e5e2e1' } },
    chart: { backgroundColor: 'transparent', type: 'area' },
    xAxis: { type: 'datetime', labels: { style: { color: '#8f909e' } } },
    yAxis: { title: { text: 'Total Clicks', style: { color: '#8f909e' } }, labels: { style: { color: '#8f909e' } } },
    legend: { itemStyle: { color: '#e5e2e1' } },
    series: [{ name: 'Clicks', type: 'area', data: [], color: '#bac3ff', fillColor: { linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 }, stops: [[0, 'rgba(186, 195, 255, 0.5)'], [1, 'rgba(186, 195, 255, 0)']] } }]
  };

  // Core Data: Paginated Registry
  currentPageIndex = signal<number>(0);
  rowsPerPage = signal<number>(10);
  totalRegistryCount = signal<number>(0);
  shortLinkRegistry = signal<LocalUrl[]>([]);
  
  aggregatedGlobalClicks = computed(() => {
    return this.shortLinkRegistry().reduce((total, link) => total + link.totalClicks, 0);
  });

  totalRegistryPages = computed(() => Math.ceil(this.totalRegistryCount() / this.rowsPerPage()));

  isBackendReachable = this.shortLinkApi.isBackendHealthy;
  isSyncTaskRunning = this.backgroundSync.isSyncing;

  constructor() {
    // Reactive Chart Updates
    effect(() => {
      const timeSeriesData = this.historicalAnalyticsSnapshots().map(snapshot => [snapshot.timestamp, snapshot.clicks] as [number, number]);
      this.clickVelocityChartOptions = {
        ...this.clickVelocityChartOptions,
        series: [{ name: 'Clicks', type: 'area', data: timeSeriesData, color: '#bac3ff', fillColor: { linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 }, stops: [[0, 'rgba(186, 195, 255, 0.5)'], [1, 'rgba(186, 195, 255, 0)']] } }]
      };
    });

    // Reactive Pagination Subscriptions
    effect(() => {
      const index = this.currentPageIndex();
      const limit = this.rowsPerPage();
      
      const pagedUrlsObservable = liveQuery(() => 
        this.localDatabase.db.urls
          .orderBy('createdAt')
          .reverse()
          .offset(index * limit)
          .limit(limit)
          .toArray()
      );
      
      pagedUrlsObservable.subscribe(results => {
        this.shortLinkRegistry.set(results);
      });
    });
  }

  ngOnInit() {
    // Initialize background sync restricted to visible viewport
    this.backgroundSync.startSync(() => this.currentlyVisibleShortCodes, 30000);
    
    // Heartbeat for backend connectivity
    this.shortLinkApi.checkHealth();
    setInterval(() => this.shortLinkApi.checkHealth(), 60000);

    // Track total links for pagination meta-data
    const totalCountObservable = liveQuery(() => this.localDatabase.db.urls.count());
    totalCountObservable.subscribe(count => {
      this.totalRegistryCount.set(count);
    });
  }

  ngAfterViewInit() {
    this.initializeViewportTracking();
    this.shortLinkTableRows.changes.subscribe(() => {
      this.rebindViewportObservers();
    });
    this.rebindViewportObservers();
  }

  ngOnDestroy() {
    this.viewportIntersectionObserver?.disconnect();
    if (this.scrollRestStabilizationTimer) {
      clearTimeout(this.scrollRestStabilizationTimer);
    }
  }

  private initializeViewportTracking() {
    this.viewportIntersectionObserver = new IntersectionObserver((entries) => {
      let visibilitySetChanged = false;
      entries.forEach(entry => {
        const shortCode = entry.target.getAttribute('data-short-code');
        if (!shortCode) return;

        if (entry.isIntersecting) {
          if (!this.currentlyVisibleShortCodes.has(shortCode)) {
            this.currentlyVisibleShortCodes.add(shortCode);
            visibilitySetChanged = true;
          }
        } else {
          if (this.currentlyVisibleShortCodes.has(shortCode)) {
            this.currentlyVisibleShortCodes.delete(shortCode);
            visibilitySetChanged = true;
          }
        }
      });

      if (visibilitySetChanged) {
        // Debounce sync until scroll resting to protect backend throughput
        if (this.scrollRestStabilizationTimer) {
          clearTimeout(this.scrollRestStabilizationTimer);
        }

        this.scrollRestStabilizationTimer = setTimeout(() => {
          this.backgroundSync.performSync(() => this.currentlyVisibleShortCodes);
        }, 500);
      }
    }, { threshold: 0.1, rootMargin: '50px' });
  }

  private rebindViewportObservers() {
    this.shortLinkTableRows.forEach(row => {
      this.viewportIntersectionObserver?.observe(row.nativeElement);
    });
  }

  navigateToNextPage() {
    if (this.currentPageIndex() < this.totalRegistryPages() - 1) {
      this.currentPageIndex.set(this.currentPageIndex() + 1);
      this.currentlyVisibleShortCodes.clear();
    }
  }

  navigateToPreviousPage() {
    if (this.currentPageIndex() > 0) {
      this.currentPageIndex.set(this.currentPageIndex() - 1);
      this.currentlyVisibleShortCodes.clear();
    }
  }

  toggleSidebarExpansion() {
    this.isSidebarCollapsed.set(!this.isSidebarCollapsed());
  }

  focusDestinationInput() {
    this.destinationUrlInput.nativeElement.scrollIntoView({ behavior: 'smooth' });
    this.destinationUrlInput.nativeElement.focus();
  }

  async executeShorteningTask() {
    const urlToShorten = this.destinationUrl();
    if (!urlToShorten) return;

    this.isShorteningInProgress.set(true);
    this.shorteningErrorMessage.set(null);
    this.userNotificationMessage.set(null);

    const shorteningRequest: CreateShortUrlRequest = {
      url: urlToShorten,
      shortCode: this.customShortPath() || undefined
    };

    this.shortLinkApi.createShortUrl(shorteningRequest).subscribe({
      next: async (response) => {
        const urlSegments = response.shortUrl.split('/');
        const extractedCode = urlSegments[urlSegments.length - 1];
        
        await this.localDatabase.addUrl(extractedCode, response.originalUrl, response.shortUrl);
        
        this.destinationUrl.set('');
        this.customShortPath.set('');
        this.showTransientNotification('Short URL generated successfully!');
        this.isShorteningInProgress.set(false);
      },
      error: (error) => {
        if (error.status === 409) {
          this.shorteningErrorMessage.set(`The path '${shorteningRequest.shortCode}' is already registered.`);
        } else {
          this.shorteningErrorMessage.set('An unexpected system error occurred.');
        }
        this.isShorteningInProgress.set(false);
      }
    });
  }

  copyToClipboard(content: string) {
    navigator.clipboard.writeText(content).then(() => {
      this.showTransientNotification('Link copied to system clipboard.');
    });
  }

  async openAnalyticsInsightDrawer(shortLink: LocalUrl) {
    this.selectedShortLinkMetadata.set(shortLink);
    this.isAnalyticsDrawerOpen.set(true);
    
    // Load local history immediately for snappy response
    const cachedHistory = await this.localDatabase.getAnalyticsHistory(shortLink.shortCode);
    this.historicalAnalyticsSnapshots.set(cachedHistory);
    
    // Refresh with live server data
    await this.fetchLatestDetailedAnalytics(shortLink.shortCode);
  }

  onShortLinkHover(shortLink: LocalUrl) {
    this.prefetchHoverTimer = setTimeout(() => {
      this.fetchLatestDetailedAnalytics(shortLink.shortCode, true);
    }, 300);
  }

  onShortLinkHoverEnd() {
    if (this.prefetchHoverTimer) {
      clearTimeout(this.prefetchHoverTimer);
    }
  }

  private async fetchLatestDetailedAnalytics(shortCode: string, isSilentBackgroundFetch = false) {
    if (!isSilentBackgroundFetch) this.isFetchingDetailedAnalytics.set(true);
    
    this.shortLinkApi.getAnalytics(shortCode).subscribe({
      next: async (serverAnalytics) => {
        await this.localDatabase.updateAnalytics(shortCode, serverAnalytics.clicks);
        
        // Refresh chart if this specific link is still selected in the drawer
        if (this.selectedShortLinkMetadata()?.shortCode === shortCode) {
          const updatedHistory = await this.localDatabase.getAnalyticsHistory(shortCode);
          this.historicalAnalyticsSnapshots.set(updatedHistory);
        }
        this.isFetchingDetailedAnalytics.set(false);
      },
      error: () => {
        this.isFetchingDetailedAnalytics.set(false);
      }
    });
  }

  closeAnalyticsDrawer() {
    this.isAnalyticsDrawerOpen.set(false);
    this.selectedShortLinkMetadata.set(null);
    this.historicalAnalyticsSnapshots.set([]);
  }

  private showTransientNotification(message: string) {
    this.userNotificationMessage.set(message);
    setTimeout(() => this.userNotificationMessage.set(null), 3000);
  }
}
