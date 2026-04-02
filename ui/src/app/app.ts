import { Component, OnInit, signal, computed, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { liveQuery } from 'dexie';
import { HighchartsChartDirective, provideHighcharts } from 'highcharts-angular';
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
export class AppComponent implements OnInit {
  // Services
  private api = inject(ApiService);
  private db = inject(DbService);
  private sync = inject(SyncService);

  Highcharts: typeof Highcharts = Highcharts;

  // State
  longUrl = signal<string>('');
  customShortCode = signal<string>('');
  
  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Drawer state
  isDrawerOpen = signal<boolean>(false);
  selectedUrl = signal<LocalUrl | null>(null);
  selectedUrlAnalytics = signal<AnalyticsSnapshot[]>([]);

  // Chart configuration
  chartOptions: Highcharts.Options = {
    title: { text: 'Clicks Over Time', style: { color: '#e5e2e1' } },
    chart: { backgroundColor: 'transparent', type: 'area' },
    xAxis: { type: 'datetime', labels: { style: { color: '#8f909e' } } },
    yAxis: { title: { text: 'Total Clicks', style: { color: '#8f909e' } }, labels: { style: { color: '#8f909e' } } },
    legend: { itemStyle: { color: '#e5e2e1' } },
    series: [{ name: 'Clicks', type: 'area', data: [], color: '#bac3ff', fillColor: { linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 }, stops: [[0, 'rgba(186, 195, 255, 0.5)'], [1, 'rgba(186, 195, 255, 0)']] } }]
  };

  // Dexie live query bound to a signal
  urls = signal<LocalUrl[]>([]);
  
  globalClicks = computed(() => {
    return this.urls().reduce((sum, url) => sum + url.totalClicks, 0);
  });

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
  }

  ngOnInit() {
    // Start background sync every 1 minute
    this.sync.startSync(60000);
    
    // Check initial health
    this.api.checkHealth();
    setInterval(() => this.api.checkHealth(), 60000);

    // Subscribe to Dexie LiveQuery
    const urlsObservable = liveQuery(() => this.db.db.urls.orderBy('createdAt').reverse().toArray());
    urlsObservable.subscribe(data => {
      this.urls.set(data);
    });
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
        // Parse the returned shortUrl (e.g. "http://link.whoa.sh/custom") to extract the code
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
          this.errorMessage.set(`Short code '${request.shortCode}' is already taken. Try another?`);
        } else if (err.status === 400) {
          this.errorMessage.set('Invalid URL format.');
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
    const history = await this.db.getAnalyticsHistory(url.shortCode);
    this.selectedUrlAnalytics.set(history);
    this.isDrawerOpen.set(true);
  }

  closeDrawer() {
    this.isDrawerOpen.set(false);
  }
}
