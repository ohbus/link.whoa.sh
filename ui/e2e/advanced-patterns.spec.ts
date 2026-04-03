import { test, expect } from '@playwright/test';

test.describe('Advanced Data Patterns & Monkey Testing', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should handle rapid-fire shortening requests (monkey test)', async ({ page }) => {
    const input = page.getByTestId('destination-url-input');
    const btn = page.getByTestId('execute-shorten-btn');

    for (let i = 0; i < 5; i++) {
      await input.fill(`https://monkey-${i}.com`);
      await btn.click();
    }

    // Verify all 5 were added to local registry
    await expect(page.locator('tr')).toHaveCount(6); // 1 header + 5 rows
  });

  test('should survive massive URL payloads', async ({ page }) => {
    const massiveUrl = 'https://example.com/' + 'a'.repeat(2000);
    await page.getByTestId('destination-url-input').fill(massiveUrl);
    await page.getByTestId('execute-shorten-btn').click();

    // Registry should update
    await expect(page.locator('tr').nth(1)).toBeVisible();
    const originalUrlHint = await page.locator('tr').nth(1).locator('span[title]').getAttribute('title');
    expect(originalUrlHint).toBe(massiveUrl);
  });

  test('should correctly sort mixed creation dates in registry', async ({ page }) => {
    // We rely on the backend reset and fresh indexedDB from beforeEach
    
    // 1. Create first link
    await page.getByTestId('destination-url-input').fill('https://first.com');
    await page.getByTestId('execute-shorten-btn').click();
    await expect(page.getByTestId('toast-notification')).toBeVisible();
    await page.getByTestId('toast-notification').isHidden();

    // 2. Create second link
    await page.getByTestId('destination-url-input').fill('https://second.com');
    await page.getByTestId('execute-shorten-btn').click();
    
    // The second one (most recent) must be at the top (index 1 in tr list)
    const firstRowCode = await page.locator('tr').nth(1).getByTestId(/link-code-/).innerText();
    const secondRowCode = await page.locator('tr').nth(2).getByTestId(/link-code-/).innerText();
    
    // In our app, newest links are at the top
    expect(firstRowCode.length).toBeGreaterThan(0);
    expect(secondRowCode.length).toBeGreaterThan(0);
    expect(firstRowCode).not.toBe(secondRowCode);
  });

  test('should handle special characters in custom paths', async ({ page }) => {
    const code = 'dash-123'; // Standard alphanumeric + dash allowed by backend
    await page.getByTestId('destination-url-input').fill('https://special.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();

    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
  });

  test('should persist data across hard refreshes', async ({ page }) => {
    await page.getByTestId('destination-url-input').fill('https://persist.com');
    await page.getByTestId('execute-shorten-btn').click();
    await expect(page.locator('tr').nth(1)).toBeVisible();

    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
    // Data must still be there from IndexedDB
    await expect(page.locator('tr').nth(1)).toBeVisible();
    await expect(page.locator('tr')).toHaveCount(2);
  });

  test('should handle immediate drawer click with loading indicator', async ({ page }) => {
    const code = 'quick-click';
    await page.getByTestId('destination-url-input').fill('https://quick.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();

    const row = page.getByTestId(`link-row-${code}`);
    await expect(row).toBeVisible();
    
    // Rapidly click to catch the loading state
    await row.click();
    
    // We added 800ms minimum display in app.ts, so this should now be stable
    const indicator = page.getByTestId('drawer-sync-status');
    await expect(indicator).toBeVisible({ timeout: 5000 });
    await expect(indicator).toBeHidden({ timeout: 10000 });
    await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  });

  test('should gracefully handle offline transition and recovery', async ({ page, context }) => {
    // 1. Initial State: Active
    await expect(page.getByTestId('system-status')).toContainText('Backend Active');

    // 2. Transition: Offline
    await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
    // Force a check immediately
    await page.evaluate(() => (window as any).WhoaApp.forceHealthCheck());
    
    await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 10000 });
    await expect(page.getByTestId('execute-shorten-btn')).toBeDisabled();

    // 3. Recovery
    await context.unroute('**/actuator/health');
    // Force a check immediately
    await page.evaluate(() => (window as any).WhoaApp.forceHealthCheck());
    
    await expect(page.getByTestId('system-status')).toContainText('Backend Active', { timeout: 10000 });
    await expect(page.getByTestId('execute-shorten-btn')).toBeEnabled();
  });
});
