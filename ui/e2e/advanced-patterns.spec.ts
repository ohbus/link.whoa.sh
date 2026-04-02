import { test, expect } from '@playwright/test';

test.describe('Advanced Data Patterns & Monkey Testing', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
  });

  test('should debounce network calls during rapid monkey scrolling', async ({ page }) => {
    // 1. Seed links to enable scrolling
    for (let i = 1; i <= 20; i++) {
      await page.getByTestId('destination-url-input').fill(`https://monkey-${i}.com`);
      const btn = page.getByTestId('execute-shorten-btn');
      await expect(btn).toBeEnabled();
      await btn.click();
      // Wait for registry to update
      await expect(page.getByTestId(/link-row-monkey-/).first()).toBeVisible();
    }

    let syncRequestCount = 0;
    await page.route('**/api/v1/urls/analytics/bulk', async route => {
      syncRequestCount++;
      await route.continue();
    });

    for (let i = 0; i < 5; i++) {
      await page.mouse.wheel(0, 500);
      await page.waitForTimeout(50);
      await page.mouse.wheel(0, -500);
      await page.waitForTimeout(50);
    }

    await page.waitForTimeout(1000); 
    expect(syncRequestCount).toBeLessThanOrEqual(2);
  });

  test('should fire correct delta requests with map and timestamp', async ({ page }) => {
    const code = 'delta-test';
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

    await page.mouse.wheel(0, 10);
    
    const request = await syncRequestPromise;
    const postData = JSON.parse(request.postData() || '{}');

    expect(postData.currentCounts).toBeDefined();
    expect(postData.currentCounts[code]).toBe(0);
  });

  test('should show correct counts after backend update during delta sync', async ({ page, context }) => {
    const code = 'live-update';
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();

    await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('0');

    await context.request.get(`http://127.0.0.1:8844/${code}`);

    await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('1', { timeout: 15000 });
  });

  test('should handle immediate drawer click with loading indicator', async ({ page }) => {
    const code = 'drawer-test';
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();

    await page.route(`**/api/v1/urls/${code}/analytics`, async route => {
      await page.waitForTimeout(1000);
      await route.continue();
    });

    await page.getByTestId(`link-row-${code}`).click();
    await expect(page.getByTestId('drawer-sync-status')).toBeVisible();
    await expect(page.getByTestId('drawer-sync-status')).toBeHidden({ timeout: 10000 });
    await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  });

  test('should gracefully handle offline transition and recovery', async ({ page, context }) => {
    await expect(page.getByTestId('system-status')).toContainText('Backend Active');

    await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
    await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 15000 });
    await expect(page.getByTestId('execute-shorten-btn')).toBeDisabled();

    await context.unroute('**/actuator/health');
    await expect(page.getByTestId('system-status')).toContainText('Backend Active', { timeout: 15000 });
    await expect(page.getByTestId('execute-shorten-btn')).toBeEnabled();
  });
});
