import { Component, OnInit, signal, computed, inject, effect, ViewChild, ElementRef, AfterViewInit, OnDestroy, ViewChildren, QueryList } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { liveQuery } from 'dexie';
import { HighchartsChartDirective } from 'highcharts-angular';
import * as Highcharts from 'highcharts';

import { ApiService, CreateShortUrlRequest } from './services/api.service';
import { DbService, LocalUrl, AnalyticsSnapshot } from './services/db.service';
import { SyncService } from './services/sync.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HighchartsChartDirective],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('urlInput') urlInput!: ElementRef<HTMLInputElement>;
  @ViewChildren('urlRow') urlRows!: QueryList<ElementRef<HTMLTableRowElement>>;

  // Services
  private api = inject(ApiService);
  private db = inject(DbService);
  private sync = inject(SyncService);

  Highcharts: typeof Highcharts = Highcharts;

  // State
  isSidebarCollapsed = signal<boolean>(false);
  longUrl = signal<string>('');
  customShortCode = signal<string>('');
  
  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Visibility Tracking
  private visibleCodes = new Set<string>();
  private intersectionObserver?: IntersectionObserver;

  // Drawer state
  isDrawerOpen = signal<boolean>(false);
  selectedUrl = signal<LocalUrl | null>(null);
  selectedUrlAnalytics = signal<AnalyticsSnapshot[]>([]);
  isLoadingDetails = signal<boolean>(false);

  private hoverTimeout: any;

  // Chart configuration
  chartOptions: Highcharts.Options = {
    title: { text: 'Clicks Over Time', style: { color: '#e5e2e1' } },
    chart: { backgroundColor: 'transparent', type: 'area' },
    xAxis: { type: 'datetime', labels: { style: { color: '#8f909e' } } },
    yAxis: { title: { text: 'Total Clicks', style: { color: '#8f909e' } }, labels: { style: { color: '#8f909e' } } },
    legend: { itemStyle: { color: '#e5e2e1' } },
    series: [{ name: 'Clicks', type: 'area', data: [], color: '#bac3ff', fillColor: { linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 }, stops: [[0, 'rgba(186, 195, 255, 0.5)'], [1, 'rgba(186, 195, 255, 0)']] } }]
  };

  // Pagination State
  pageIndex = signal<number>(0);
  pageSize = signal<number>(10);
  totalUrlCount = signal<number>(0);

  // Dexie live query bound to a signal
  urls = signal<LocalUrl[]>([]);
  
  globalClicks = computed(() => {
    return this.urls().reduce((sum, url) => sum + url.totalClicks, 0);
  });

  totalPages = computed(() => Math.ceil(this.totalUrlCount() / this.pageSize()));

  isBackendHealthy = this.api.isBackendHealthy;
  isSyncing = this.sync.isSyncing;

  constructor() {
    effect(() => {
      const data = this.selectedUrlAnalytics().map(snap => [snap.timestamp, snap.clicks] as [number, number]);
      this.chartOptions = {
        ...this.chartOptions,
        series: [{ name: 'Clicks', type: 'area', data: data, color: '#bac3ff', fillColor: { linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 }, stops: [[0, 'rgba(186, 195, 255, 0.5)'], [1, 'rgba(186, 195, 255, 0)']] } }]
      };
    });

    // Re-subscribe to URLs when pageIndex or pageSize changes
    effect(() => {
      const index = this.pageIndex();
      const size = this.pageSize();
      
      const urlsObservable = liveQuery(() => 
        this.db.db.urls
          .orderBy('createdAt')
          .reverse()
          .offset(index * size)
          .limit(size)
          .toArray()
      );
      
      urlsObservable.subscribe(data => {
        this.urls.set(data);
      });
    });
  }

  ngOnInit() {
    // Start background sync: only poll visible codes
    this.sync.startSync(() => this.visibleCodes, 30000);
    
    // Check initial health
    this.api.checkHealth();
    setInterval(() => this.api.checkHealth(), 60000);

    // Subscribe to total count for pagination
    const countObservable = liveQuery(() => this.db.db.urls.count());
    countObservable.subscribe(count => {
      this.totalUrlCount.set(count);
    });
  }

  ngAfterViewInit() {
    this.setupIntersectionObserver();
    this.urlRows.changes.subscribe(() => {
      this.refreshObservers();
    });
    this.refreshObservers();
  }

  ngOnDestroy() {
    this.intersectionObserver?.disconnect();
  }

  private setupIntersectionObserver() {
    this.intersectionObserver = new IntersectionObserver((entries) => {
      let changed = false;
      entries.forEach(entry => {
        const code = entry.target.getAttribute('data-code');
        if (!code) return;

        if (entry.isIntersecting) {
          if (!this.visibleCodes.has(code)) {
            this.visibleCodes.add(code);
            changed = true;
          }
        } else {
          if (this.visibleCodes.has(code)) {
            this.visibleCodes.delete(code);
            changed = true;
          }
        }
      });

      if (changed) {
        // Trigger an immediate "delta" sync when visibility changes
        this.sync.performSync(() => this.visibleCodes);
      }
    }, { threshold: 0.1, rootMargin: '50px' });
  }

  private refreshObservers() {
    // We don't disconnect entirely, we just observe new ones
    this.urlRows.forEach(row => {
      this.intersectionObserver?.observe(row.nativeElement);
    });
  }

  nextPage() {
    if (this.pageIndex() < this.totalPages() - 1) {
      this.pageIndex.set(this.pageIndex() + 1);
      this.visibleCodes.clear();
    }
  }

  previousPage() {
    if (this.pageIndex() > 0) {
      this.pageIndex.set(this.pageIndex() - 1);
      this.visibleCodes.clear();
    }
  }

  toggleSidebar() {
    this.isSidebarCollapsed.set(!this.isSidebarCollapsed());
  }

  scrollToCreate() {
    this.urlInput.nativeElement.scrollIntoView({ behavior: 'smooth' });
    this.urlInput.nativeElement.focus();
  }

  async shortenUrl() {
    if (!this.longUrl()) return;

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const request: CreateShortUrlRequest = {
      url: this.longUrl(),
      shortCode: this.customShortCode() ? this.customShortCode() : undefined
    };

    this.api.createShortUrl(request).subscribe({
      next: async (res) => {
        const parts = res.shortUrl.split('/');
        const code = parts[parts.length - 1];
        await this.db.addUrl(code, res.originalUrl, res.shortUrl);
        this.longUrl.set('');
        this.customShortCode.set('');
        this.successMessage.set('Short URL created successfully!');
        setTimeout(() => this.successMessage.set(null), 3000);
        this.isSubmitting.set(false);
      },
      error: (err) => {
        if (err.status === 409) {
          this.errorMessage.set(`Short code '${request.shortCode}' is already taken.`);
        } else {
          this.errorMessage.set('An unexpected error occurred.');
        }
        this.isSubmitting.set(false);
      }
    });
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      this.successMessage.set('Link copied to clipboard!');
      setTimeout(() => this.successMessage.set(null), 3000);
    });
  }

  async openAnalyticsDrawer(url: LocalUrl) {
    this.selectedUrl.set(url);
    this.isDrawerOpen.set(true);
    const history = await this.db.getAnalyticsHistory(url.shortCode);
    this.selectedUrlAnalytics.set(history);
    await this.loadDetailedAnalytics(url.shortCode);
  }

  onMouseEnter(url: LocalUrl) {
    this.hoverTimeout = setTimeout(() => {
      this.loadDetailedAnalytics(url.shortCode, true);
    }, 300);
  }

  onMouseLeave() {
    if (this.hoverTimeout) {
      clearTimeout(this.hoverTimeout);
    }
  }

  private async loadDetailedAnalytics(shortCode: string, isBackground = false) {
    if (!isBackground) this.isLoadingDetails.set(true);
    
    this.api.getAnalytics(shortCode).subscribe({
      next: async (res) => {
        await this.db.updateAnalytics(shortCode, res.clicks);
        if (this.selectedUrl()?.shortCode === shortCode) {
          const history = await this.db.getAnalyticsHistory(shortCode);
          this.selectedUrlAnalytics.set(history);
        }
        this.isLoadingDetails.set(false);
      },
      error: () => {
        this.isLoadingDetails.set(false);
      }
    });
  }

  closeDrawer() {
    this.isDrawerOpen.set(false);
    this.selectedUrl.set(null);
    this.selectedUrlAnalytics.set([]);
  }
}
