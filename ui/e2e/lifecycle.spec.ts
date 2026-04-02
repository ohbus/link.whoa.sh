import { test, expect } from '@playwright/test';

test.describe('Whoa Link Shortener Full Lifecycle', () => {
  
  test.beforeEach(async ({ page, request }) => {
    // 1. Reset Backend State
    const resetResponse = await request.post('http://127.0.0.1:8844/api/testing/reset');
    expect(resetResponse.ok()).toBeTruthy();

    // 2. Clear Browser State
    await page.goto('/#/');
    await page.evaluate(async () => {
      await indexedDB.deleteDatabase('WhoaDatabase');
      // @ts-ignore
      window.SyncService_skipSync = true;
    });
    
    // Navigate again to ensure the flag is set BEFORE the app starts
    await page.goto('/#/');
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should shorten a URL and verify its presence in the registry', async ({ page }) => {
    const targetUrl = 'https://playwright.dev';
    const customCode = 'pw' + Math.floor(Math.random() * 1000000);

    await page.getByTestId('destination-url-input').fill(targetUrl);
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(customCode);

    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();

    await expect(page.getByTestId('toast-notification')).toBeVisible();
    const row = page.getByTestId(`link-row-${customCode}`);
    await expect(row).toBeVisible();
  });

  test('should track analytics when a short link is visited', async ({ page, context }) => {
    const targetUrl = 'https://github.com';
    const customCode = 'v' + Math.floor(Math.random() * 1000000);

    await page.getByTestId('destination-url-input').fill(targetUrl);
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(customCode);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();
    
    const row = page.getByTestId(`link-row-${customCode}`);
    await expect(row).toBeVisible();

    const redirectUrl = `http://127.0.0.1:8844/${customCode}`;
    const visitPage = await context.newPage();
    await visitPage.goto(redirectUrl);
    await visitPage.close();

    // Manually trigger sync since background sync is disabled
    await page.evaluate(async () => {
      // @ts-ignore
      const app = document.querySelector('app-root');
      // Actually we can just wait for the component to poll or trigger it
      // For now, let's just re-enable it for a second
      // @ts-ignore
      window.SyncService_skipSync = false;
    });

    await expect(page.getByTestId(`link-clicks-${customCode}`)).toContainText('1', { timeout: 20000 });
  });

  test('should show detailed analytics drawer with time-series chart', async ({ page }) => {
    const targetUrl = 'https://kotlinlang.org';
    const code = 'ktest';

    await page.getByTestId('destination-url-input').fill(targetUrl);
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();

    const row = page.getByTestId(`link-row-${code}`);
    await row.scrollIntoViewIfNeeded();
    await expect(row).toBeVisible();
    
    await row.click();

    const drawer = page.getByTestId('analytics-drawer');
    await expect(drawer).toBeVisible({ timeout: 10000 });
    await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
    await expect(page.getByTestId('drawer-chart')).toBeVisible();
  });

  test('should handle custom code collisions gracefully', async ({ page }) => {
    const code = 'coll';
    
    await page.getByTestId('destination-url-input').fill('https://first.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();

    await page.getByTestId('destination-url-input').fill('https://second.com');
    await page.getByTestId('custom-path-input').fill(code);
    await btn.click();

    await expect(page.getByTestId('shortening-error')).toBeVisible();
    await expect(page.getByTestId('shortening-error')).toContainText(`The path '${code}' is already registered`);
  });
});
