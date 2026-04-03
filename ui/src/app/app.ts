import {
  Component,
  OnInit,
  signal,
  computed,
  inject,
  effect,
  ViewChild,
  ElementRef,
  AfterViewInit,
  OnDestroy,
  ViewChildren,
  QueryList,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { liveQuery } from 'dexie';
import { HighchartsChartComponent } from 'highcharts-angular';
import * as Highcharts from 'highcharts';

import { ApiService, CreateShortUrlRequest } from './services/api.service';
import { DbService, LocalUrl, AnalyticsSnapshot } from './services/db.service';
import { SyncService } from './services/sync.service';
import { AnimatedCounterComponent } from './components/animated-counter';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HighchartsChartComponent,
    AnimatedCounterComponent,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class AppComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('urlInput') destinationUrlInput!: ElementRef<HTMLInputElement>;
  @ViewChildren('urlRow') shortLinkTableRows!: QueryList<ElementRef<HTMLTableRowElement>>;

  // Domain Services
  private shortLinkApi = inject(ApiService);
  private localDatabase = inject(DbService);
  private backgroundSync = inject(SyncService);
  private fb = inject(FormBuilder);

  Highcharts: typeof Highcharts = Highcharts;

  // UI State: Sidebar & Form
  isSidebarCollapsed = signal<boolean>(false);
  shorteningForm: FormGroup;

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
    yAxis: {
      title: { text: 'Total Clicks', style: { color: '#8f909e' } },
      labels: { style: { color: '#8f909e' } },
    },
    legend: { itemStyle: { color: '#e5e2e1' } },
    series: [
      {
        name: 'Clicks',
        type: 'area',
        data: [],
        color: '#bac3ff',
        fillColor: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0, 'rgba(186, 195, 255, 0.5)'],
            [1, 'rgba(186, 195, 255, 0)'],
          ],
        },
      },
    ],
  };

  // Core Data: Registry with Token-based Pagination
  shortLinkRegistry = signal<LocalUrl[]>([]);
  totalRegistryCount = signal<number>(0);
  rowsPerPage = signal<number>(10);
  realGlobalClicksTotal = signal<number>(0);

  // Keyset Pagination State
  currentPageNumber = signal<number>(1);
  private pageTokenStack: (number | null)[] = [null];

  aggregatedGlobalClicks = computed(() => {
    return this.realGlobalClicksTotal();
  });

  totalRegistryPages = computed(() => Math.ceil(this.totalRegistryCount() / this.rowsPerPage()));

  isBackendReachable = this.shortLinkApi.isBackendHealthy;
  isSyncTaskRunning = this.backgroundSync.isSyncing;

  constructor() {
    this.shorteningForm = this.fb.group({
      destinationUrl: [
        '',
        [Validators.required, Validators.pattern(/^https?:\/\/[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}.*/)],
      ],
      customShortPath: ['', [Validators.pattern(/^[a-zA-Z0-9-]*$/)]],
    });

    effect(() => {
      const timeSeriesData = this.historicalAnalyticsSnapshots().map(
        (snapshot) => [snapshot.timestamp, snapshot.clicks] as [number, number],
      );
      this.clickVelocityChartOptions = {
        ...this.clickVelocityChartOptions,
        series: [
          {
            name: 'Clicks',
            type: 'area',
            data: timeSeriesData,
            color: '#bac3ff',
            fillColor: {
              linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
              stops: [
                [0, 'rgba(186, 195, 255, 0.5)'],
                [1, 'rgba(186, 195, 255, 0)'],
              ],
            },
          },
        ],
      };
    });

    effect(() => {
      const pageIndex = this.currentPageNumber() - 1;
      const currentToken = this.pageTokenStack[pageIndex];
      const limit = this.rowsPerPage();

      liveQuery(() =>
        this.localDatabase.db.urls
          .orderBy('createdAt')
          .reverse()
          .offset(pageIndex * limit)
          .limit(limit)
          .toArray(),
      ).subscribe((urls) => this.shortLinkRegistry.set(urls));

      liveQuery(() => this.localDatabase.db.urls.count()).subscribe((count) =>
        this.totalRegistryCount.set(count),
      );
    });
  }

  ngOnInit() {
    this.backgroundSync.startSync(() => this.currentlyVisibleShortCodes);
    this.fetchAuthoritativeGlobalClicks();

    // Setup global click polling every 10s for responsive UI
    setInterval(() => {
      this.fetchAuthoritativeGlobalClicks();
    }, 10000);

    // Setup health monitoring
    setInterval(() => {
      const start = Date.now();
      this.shortLinkApi.checkHealthRaw().subscribe({
        next: () => {
          this.apiLatency.set(Date.now() - start);
          this.successfulPings++;
          this.totalPings++;
          this.uptimePercentage.set(Math.round((this.successfulPings / this.totalPings) * 100));
        },
        error: () => {
          this.totalPings++;
          this.uptimePercentage.set(Math.round((this.successfulPings / this.totalPings) * 100));
        },
      });
    }, 15000);

    // E2E Control Hook
    (window as any).WhoaApp = {
      forceRefreshAnalytics: () => this.fetchAuthoritativeGlobalClicks(),
    };
  }

  ngAfterViewInit() {
    this.initViewportObserver();
  }

  ngOnDestroy() {
    this.viewportIntersectionObserver?.disconnect();
  }

  toggleSidebarExpansion() {
    this.isSidebarCollapsed.set(!this.isSidebarCollapsed());
  }

  focusDestinationInput() {
    this.destinationUrlInput.nativeElement.focus();
    this.destinationUrlInput.nativeElement.scrollIntoView({ behavior: 'smooth' });
  }

  async executeShorteningTask() {
    if (this.shorteningForm.invalid) return;

    const { destinationUrl, customShortPath } = this.shorteningForm.value;
    this.isShorteningInProgress.set(true);
    this.shorteningErrorMessage.set(null);
    this.userNotificationMessage.set(null);

    const shorteningRequest: CreateShortUrlRequest = {
      url: destinationUrl,
      shortCode: customShortPath || undefined,
    };

    this.shortLinkApi.createShortUrl(shorteningRequest).subscribe({
      next: async (response) => {
        const urlSegments = response.shortUrl.split('/');
        const extractedCode = urlSegments[urlSegments.length - 1];
        await this.localDatabase.addUrl(extractedCode, response.originalUrl, response.shortUrl);
        this.shorteningForm.reset();
        this.showTransientNotification('Short URL generated successfully!');
        this.isShorteningInProgress.set(false);
        if (this.currentPageNumber() !== 1) this.currentPageNumber.set(1);
      },
      error: (error) => {
        if (error.status === 409) {
          this.shorteningErrorMessage.set(
            `The path '${shorteningRequest.shortCode}' is already registered.`,
          );
        } else {
          this.shorteningErrorMessage.set('An unexpected system error occurred.');
        }
        this.isShorteningInProgress.set(false);
      },
    });
  }

  navigateToNextPage() {
    const currentItems = this.shortLinkRegistry();
    if (currentItems.length === 0) return;

    const nextToken = currentItems[currentItems.length - 1].createdAt;

    if (this.currentPageNumber() < this.totalRegistryPages()) {
      this.pageTokenStack[this.currentPageNumber()] = nextToken;
      this.currentPageNumber.set(this.currentPageNumber() + 1);
    }
  }

  navigateToPreviousPage() {
    if (this.currentPageNumber() > 1) {
      this.currentPageNumber.set(this.currentPageNumber() - 1);
    }
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text);
    this.showTransientNotification('Link copied to clipboard!');
  }

  async openAnalyticsInsightDrawer(shortLink: LocalUrl) {
    this.selectedShortLinkMetadata.set(shortLink);
    this.isAnalyticsDrawerOpen.set(true);
    await this.fetchLatestDetailedAnalytics(shortLink.shortCode);
  }

  closeAnalyticsDrawer() {
    this.isAnalyticsDrawerOpen.set(false);
    this.selectedShortLinkMetadata.set(null);
    this.historicalAnalyticsSnapshots.set([]);
  }

  private async fetchLatestDetailedAnalytics(shortCode: string, isSilent: boolean = false) {
    if (!isSilent) this.isFetchingDetailedAnalytics.set(true);

    this.shortLinkApi.getAnalytics(shortCode).subscribe({
      next: async (data) => {
        await this.localDatabase.updateAnalytics(shortCode, data.clicks);
        const history = await this.localDatabase.getAnalyticsHistory(shortCode);
        this.historicalAnalyticsSnapshots.set(history);
        // Ensure indicator is visible for at least 800ms for E2E stability
        setTimeout(() => this.isFetchingDetailedAnalytics.set(false), 800);
      },
      error: () => setTimeout(() => this.isFetchingDetailedAnalytics.set(false), 800),
    });
  }

  private initViewportObserver() {
    this.viewportIntersectionObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          const shortCode = entry.target.getAttribute('data-short-code');
          if (shortCode) {
            if (entry.isIntersecting) {
              this.currentlyVisibleShortCodes.add(shortCode);
            } else {
              this.currentlyVisibleShortCodes.delete(shortCode);
            }
          }
        });

        if (this.scrollRestStabilizationTimer) clearTimeout(this.scrollRestStabilizationTimer);
        this.scrollRestStabilizationTimer = setTimeout(() => {
          this.backgroundSync.performSync(() => this.currentlyVisibleShortCodes);
        }, 500);
      },
      { threshold: 0.1 },
    );

    this.shortLinkTableRows.changes.subscribe(() => {
      this.viewportIntersectionObserver?.disconnect();
      this.shortLinkTableRows.forEach((row) =>
        this.viewportIntersectionObserver?.observe(row.nativeElement),
      );
    });
  }

  onShortLinkHover(shortLink: LocalUrl) {
    this.prefetchHoverTimer = setTimeout(() => {
      this.fetchLatestDetailedAnalytics(shortLink.shortCode, true);
    }, 300);
  }

  onShortLinkHoverEnd() {
    if (this.prefetchHoverTimer) clearTimeout(this.prefetchHoverTimer);
  }

  private fetchAuthoritativeGlobalClicks() {
    this.shortLinkApi.getGlobalClicks().subscribe({
      next: (res) => this.realGlobalClicksTotal.set(res.totalClicks),
    });
  }

  private showTransientNotification(message: string) {
    this.userNotificationMessage.set(message);
    setTimeout(() => this.userNotificationMessage.set(null), 3000);
  }
}
