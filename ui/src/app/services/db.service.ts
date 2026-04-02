import { Injectable } from '@angular/core';
import Dexie, { Table } from 'dexie';

export interface LocalUrl {
  shortCode: string; // Primary Key
  originalUrl: string;
  shortUrl: string;
  createdAt: number;
  totalClicks: number;
  lastPolledAt: number;
}

export interface AnalyticsSnapshot {
  id?: number; // Auto-increment
  shortCode: string; 
  timestamp: number;
  clicks: number;
}

export class AppDB extends Dexie {
  urls!: Table<LocalUrl, string>;
  analytics!: Table<AnalyticsSnapshot, number>;

  constructor() {
    super('WhoaDatabase');
    this.version(1).stores({
      urls: '&shortCode, createdAt, lastPolledAt',
      analytics: '++id, shortCode, timestamp'
    });
  }
}

@Injectable({
  providedIn: 'root'
})
export class DbService {
  db = new AppDB();

  async addUrl(shortCode: string, originalUrl: string, shortUrl: string) {
    const now = Date.now();
    await this.db.urls.add({
      shortCode,
      originalUrl,
      shortUrl,
      createdAt: now,
      totalClicks: 0,
      lastPolledAt: now
    });
  }

  async updateAnalytics(shortCode: string, clicks: number) {
    const now = Date.now();
    
    // Update main table
    await this.db.urls.update(shortCode, {
      totalClicks: clicks,
      lastPolledAt: now
    });

    // Save snapshot for charts
    await this.db.analytics.add({
      shortCode,
      timestamp: now,
      clicks
    });
  }

  async getUrls() {
    return this.db.urls.orderBy('createdAt').reverse().toArray();
  }

  async getAnalyticsHistory(shortCode: string) {
    return this.db.analytics.where('shortCode').equals(shortCode).sortBy('timestamp');
  }

  // Observable stream of URLs for reactive UI
  liveUrls() {
    // Modern Dexie uses liveQuery which we'll wrap in a Signal in the component
    return this.db.urls.orderBy('createdAt').reverse().toArray();
  }
}
