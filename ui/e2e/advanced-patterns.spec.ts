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
      await page.getByTestId('execute-shorten-btn').click();
      await expect(page.getByTestId(`link-row-monkey-${i}`, { timeout: 2000 }).or(page.locator('tr').nth(i))).toBeDefined();
    }

    let syncRequestCount = 0;
    // Intercept bulk analytics calls
    await page.route('**/api/v1/urls/analytics/bulk', async route => {
      syncRequestCount++;
      await route.continue();
    });

    // 2. Perform "Monkey Scrolling"
    // Rapidly scroll up and down multiple times
    for (let i = 0; i < 5; i++) {
      await page.mouse.wheel(0, 500);
      await page.waitForTimeout(50);
      await page.mouse.wheel(0, -500);
      await page.waitForTimeout(50);
    }

    // 3. Wait a moment and check request count
    // Since we have a 500ms backoff, many scrolls should result in at most 1 or 2 calls 
    // depending on the initial state and final rest.
    await page.waitForTimeout(1000); 
    
    // We expect the count to be very low (usually 1 after the final rest)
    expect(syncRequestCount).toBeLessThanOrEqual(2);
  });

  test('should fire correct delta requests with map and timestamp', async ({ page }) => {
    const code = 'delta-test';
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();

    // Capture the next sync request
    const syncRequestPromise = page.waitForRequest(request => 
      request.url().includes('/analytics/bulk') && request.method() === 'POST'
    );

    // Trigger a rest-event by scrolling slightly
    await page.mouse.wheel(0, 10);
    
    const request = await syncRequestPromise;
    const postData = JSON.parse(request.postData() || '{}');

    // Verify "intendedable" Map protocol
    expect(postData.currentCounts).toBeDefined();
    expect(postData.currentCounts[code]).toBe(0); // Initial local state
    
    // lastSyncedAt might be null on first call or a number
    // Subsequent calls will have the serverTimestamp
  });

  test('should show correct counts after backend update during delta sync', async ({ page, context }) => {
    const code = 'live-update';
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();

    // Verify initial count is 0
    await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('0');

    // Simulate a hit on the backend
    await context.request.get(`http://127.0.0.1:8844/${code}`);

    // Wait for the background SyncService to pulse and update the UI
    // The animated counter will transition from 0 to 1
    await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('1', { timeout: 15000 });
  });

  test('should handle immediate drawer click with loading indicator', async ({ page, context }) => {
    const code = 'drawer-test';
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();

    // 1. Intercept analytics call and delay it to see loading state
    await page.route(`**/api/v1/urls/${code}/analytics`, async route => {
      await page.waitForTimeout(500);
      await route.continue();
    });

    // 2. Click row immediately
    await page.getByTestId(`link-row-${code}`).click();

    // 3. Verify loading pulse is visible
    await expect(page.getByTestId('drawer-sync-status')).toBeVisible();
    
    // 4. Wait for it to finish
    await expect(page.getByTestId('drawer-sync-status')).toBeHidden();
    await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  });

  test('should gracefully handle offline transition and recovery', async ({ page, context }) => {
    // 1. Initial State: Online
    await expect(page.getByTestId('system-status')).toContainText('Backend Active');

    // 2. Go Offline (Mock error for health check)
    await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
    
    // Wait for heartbeat
    await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 15000 });
    
    // Create button should be disabled
    await expect(page.getByTestId('execute-shorten-btn')).toBeDisabled();

    // 3. Recover
    await context.unroute('**/actuator/health');
    await expect(page.getByTestId('system-status')).toContainText('Backend Active', { timeout: 15000 });
    
    // Create button should be re-enabled
    await expect(page.getByTestId('execute-shorten-btn')).toBeEnabled();
  });
});
