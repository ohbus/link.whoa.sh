import { test, expect } from '@playwright/test';

test.describe('Advanced Data Patterns & Monkey Testing', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should debounce network calls during rapid monkey scrolling', async ({ page }) => {
    // 1. Seed links to enable scrolling
    for (let i = 1; i <= 15; i++) {
      await page.getByTestId('destination-url-input').fill(`https://monkey${i}.com`);
      await page.getByTestId('custom-code-summary').click();
      await page.getByTestId('custom-path-input').fill(`mky${i}`);
      
      const btn = page.getByTestId('execute-shorten-btn');
      await expect(btn).toBeEnabled();
      await btn.click();
      
      // Wait for registry to update
      await expect(page.getByTestId(`link-row-mky${i}`)).toBeVisible();
      // Collapse summary for next iteration
      await page.getByTestId('custom-code-summary').click();
    }

    let syncRequestCount = 0;
    await page.route('**/api/v1/urls/analytics/bulk', async route => {
      syncRequestCount++;
      await route.continue();
    });

    // 2. Perform "Monkey Scrolling"
    for (let i = 0; i < 10; i++) {
      await page.mouse.wheel(0, 1000);
      await page.waitForTimeout(50);
      await page.mouse.wheel(0, -1000);
      await page.waitForTimeout(50);
    }

    // Rest period to allow the debounced call to fire
    await page.waitForTimeout(1500); 
    
    // We expect at most 2 calls (initial + one after rest)
    expect(syncRequestCount).toBeLessThanOrEqual(2);
  });

  test('should fire correct delta requests with map and timestamp', async ({ page }) => {
    const code = 'delta';
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();

    const syncRequestPromise = page.waitForRequest(request => 
      request.url().includes('/analytics/bulk') && request.method() === 'POST'
    );

    // Trigger a rest-event
    await page.mouse.wheel(0, 10);
    
    const request = await syncRequestPromise;
    const postData = JSON.parse(request.postData() || '{}');

    expect(postData.currentCounts).toBeDefined();
    expect(postData.currentCounts[code]).toBe(0);
  });

  test('should show correct counts after backend update during delta sync', async ({ page, context }) => {
    const code = 'live';
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();

    await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('0');

    // Simulate a hit - don't follow redirects to avoid SSL certificate issues with example.com
    await context.request.get(`http://127.0.0.1:8844/${code}`, { maxRedirects: 0 });

    // Enable sync
    await page.evaluate(() => { (window as any).SyncService_skipSync = false; });

    await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('1', { timeout: 25000 });
  });

  test('should handle immediate drawer click with loading indicator', async ({ page }) => {
    const code = 'draw';
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();
    await expect(btn).toContainText('Execute');

    await page.route(`**/api/v1/urls/${code}/analytics`, async route => {
      await page.waitForTimeout(1000);
      await route.continue();
    });

    const row = page.getByTestId(`link-row-${code}`);
    await expect(row).toBeVisible();
    await row.click();
    
    await expect(page.getByTestId('drawer-sync-status')).toBeVisible({ timeout: 15000 });
    await expect(page.getByTestId('drawer-sync-status')).toBeHidden({ timeout: 15000 });
    await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  });

  test('should gracefully handle offline transition and recovery', async ({ page, context }) => {
    await expect(page.getByTestId('system-status')).toContainText('Backend Active');

    // Mock failure
    await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
    
    // The UI heartbeats every few seconds, we wait for the state transition
    await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 30000 });
    await expect(page.getByTestId('execute-shorten-btn')).toBeDisabled();

    // Recover
    await context.unroute('**/actuator/health');
    await expect(page.getByTestId('system-status')).toContainText('Backend Active', { timeout: 60000 });
    await expect(page.getByTestId('execute-shorten-btn')).toBeEnabled();
  });
});
