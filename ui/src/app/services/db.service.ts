import 'fake-indexeddb/auto';
import { Injectable, isDevMode } from '@angular/core';
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
  static skipSeeding = false;

  constructor() {
    if (isDevMode() && !DbService.skipSeeding) {
      this.seedDataIfEmpty();
    }
  }

  private async seedDataIfEmpty() {
    const count = await this.db.urls.count();
    if (count > 0) return;

    console.log("Local development mode: Seeding IndexedDB with sample data...");

    const baseUrls = [
        "https://github.com/ohbus/link.whoa.sh",
        "https://spring.io/projects/spring-boot",
        "https://kotlinlang.org/docs/home.html",
        "https://angular.dev/overview",
        "https://www.postgresql.org/docs/",
        "https://docker.com",
        "https://news.ycombinator.com",
        "https://reddit.com"
    ];

    const now = Date.now();

    for (let i = 0; i < baseUrls.length; i++) {
        const shortCode = `dev00${i + 1}`;
        const totalClicks = Math.floor(Math.random() * 200) + 50;
        
        await this.db.urls.add({
            shortCode,
            originalUrl: baseUrls[i],
            shortUrl: `http://localhost:8844/${shortCode}`,
            createdAt: now - (Math.random() * 10000000000), 
            totalClicks: totalClicks,
            lastPolledAt: now
        });

        let currentClicks = 0;
        for (let day = 7; day >= 0; day--) {
            currentClicks += Math.floor(Math.random() * (totalClicks / 7));
            await this.db.analytics.add({
                shortCode,
                timestamp: now - (day * 86400000),
                clicks: day === 0 ? totalClicks : currentClicks
            });
        }
    }
  }

  async addUrl(shortCode: string, originalUrl: string, shortUrl: string, createdAt: number = Date.now()) {
    const now = Date.now();
    await this.db.urls.put({
      shortCode,
      originalUrl,
      shortUrl,
      createdAt,
      totalClicks: 0,
      lastPolledAt: now
    });
  }

  async bulkAddUrls(links: {shortCode: string, originalUrl: string, shortUrl: string, clicks: number, createdAt?: string}[]) {
    const now = Date.now();
    for (const link of links) {
      const existing = await this.db.urls.get(link.shortCode);
      const linkCreatedAt = link.createdAt ? new Date(link.createdAt).getTime() : now;
      
      if (!existing) {
        await this.db.urls.add({
          shortCode: link.shortCode,
          originalUrl: link.originalUrl,
          shortUrl: link.shortUrl,
          createdAt: linkCreatedAt,
          totalClicks: link.clicks,
          lastPolledAt: now
        });
      } else {
        await this.db.urls.update(link.shortCode, { 
          totalClicks: link.clicks,
          lastPolledAt: now 
        });
      }
    }
  }

  async updateAnalytics(shortCode: string, clicks: number) {
    const now = Date.now();
    await this.db.urls.update(shortCode, {
      totalClicks: clicks,
      lastPolledAt: now
    });

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

  liveUrls() {
    return this.db.urls.orderBy('createdAt').reverse().toArray();
  }
}
