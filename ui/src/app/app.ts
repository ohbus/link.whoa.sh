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

  // Performance Metrics (Dynamic)
  apiLatency = signal<number>(0);
  uptimePercentage = signal<number>(100);
  private successfulPings = 0;
  private totalPings = 0;

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

  // Core Data: Registry with Token-based Pagination
  shortLinkRegistry = signal<LocalUrl[]>([]);
  totalRegistryCount = signal<number>(0);
  rowsPerPage = signal<number>(10);
  realGlobalClicksTotal = signal<number>(0);
  
  // Keyset Pagination State
  currentPageNumber = signal<number>(1);
  private pageTokenStack: (number | null)[] = [null]; // null represents the first page (latest items)

  aggregatedGlobalClicks = computed(() => {
    return this.realGlobalClicksTotal();
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

    // Reactive Token-Based Subscription
    effect(() => {
      const pageIndex = this.currentPageNumber() - 1;
      const currentToken = this.pageTokenStack[pageIndex];
      const limit = this.rowsPerPage();
      
      const pagedUrlsObservable = liveQuery(() => {
        if (currentToken !== null) {
          return this.localDatabase.db.urls
            .where('createdAt').below(currentToken)
            .reverse()
            .limit(limit)
            .toArray();
        }
        return this.localDatabase.db.urls.orderBy('createdAt').reverse().limit(limit).toArray();
      });
      
      pagedUrlsObservable.subscribe(results => {
        this.shortLinkRegistry.set(results);
      });
    });
  }

  ngOnInit() {
    // Start background sync restricted to visible viewport
    this.backgroundSync.startSync(() => this.currentlyVisibleShortCodes, 30000);
    
    // Heartbeat for backend connectivity & metrics
    this.performHealthCheck();
    setInterval(() => this.performHealthCheck(), 60000);

    // Track total links for pagination meta-data
    const totalCountObservable = liveQuery(() => this.localDatabase.db.urls.count());
    totalCountObservable.subscribe(count => {
      this.totalRegistryCount.set(count);
    });

    // Track total clicks across the whole registry (True Global)
    const globalClicksObservable = liveQuery(() => 
      this.localDatabase.db.urls.toArray().then(links => 
        links.reduce((sum, link) => sum + link.totalClicks, 0)
      )
    );
    globalClicksObservable.subscribe(total => {
      this.realGlobalClicksTotal.set(total);
    });
  }

  private performHealthCheck() {
    const startTime = performance.now();
    this.totalPings++;

    this.shortLinkApi.checkHealthRaw().subscribe({
      next: (res) => {
        const endTime = performance.now();
        const latency = Math.round(endTime - startTime);
        this.apiLatency.set(latency);
        this.isBackendReachable.set(res.status === 'UP');
        if (res.status === 'UP') this.successfulPings++;
        this.updateUptime();
      },
      error: () => {
        this.isBackendReachable.set(false);
        this.apiLatency.set(0);
        this.updateUptime();
      }
    });
  }

  private updateUptime() {
    const uptime = (this.successfulPings / this.totalPings) * 100;
    this.uptimePercentage.set(Math.round(uptime * 10) / 10);
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
        if (this.scrollRestStabilizationTimer) clearTimeout(this.scrollRestStabilizationTimer);
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
    const currentItems = this.shortLinkRegistry();
    if (currentItems.length === 0) return;
    const nextToken = currentItems[currentItems.length - 1].createdAt;
    if (this.currentPageNumber() < this.totalRegistryPages()) {
      this.pageTokenStack[this.currentPageNumber()] = nextToken;
      this.currentPageNumber.set(this.currentPageNumber() + 1);
      this.currentlyVisibleShortCodes.clear();
    }
  }

  navigateToPreviousPage() {
    if (this.currentPageNumber() > 1) {
      this.currentPageNumber.set(this.currentPageNumber() - 1);
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
        if (this.currentPageNumber() !== 1) this.currentPageNumber.set(1);
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
    const cachedHistory = await this.localDatabase.getAnalyticsHistory(shortLink.shortCode);
    this.historicalAnalyticsSnapshots.set(cachedHistory);
    await this.fetchLatestDetailedAnalytics(shortLink.shortCode);
  }

  onShortLinkHover(shortLink: LocalUrl) {
    this.prefetchHoverTimer = setTimeout(() => {
      this.fetchLatestDetailedAnalytics(shortLink.shortCode, true);
    }, 300);
  }

  onShortLinkHoverEnd() {
    if (this.prefetchHoverTimer) clearTimeout(this.prefetchHoverTimer);
  }

  private async fetchLatestDetailedAnalytics(shortCode: string, isSilentBackgroundFetch = false) {
    if (!isSilentBackgroundFetch) this.isFetchingDetailedAnalytics.set(true);
    this.shortLinkApi.getAnalytics(shortCode).subscribe({
      next: async (serverAnalytics) => {
        await this.localDatabase.updateAnalytics(shortCode, serverAnalytics.clicks);
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
