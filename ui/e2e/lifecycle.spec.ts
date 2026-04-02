import { test, expect } from '@playwright/test';

test.describe('Whoa Link Shortener Full Lifecycle', () => {
  
  test.beforeEach(async ({ page, request }) => {
    // 1. Reset Backend State (PostgreSQL + Caffeine)
    const resetResponse = await request.post('http://127.0.0.1:8844/api/testing/reset');
    expect(resetResponse.ok()).toBeTruthy();

    // 2. Clear Browser State (IndexedDB)
    await page.goto('/#/');
    await page.evaluate(async () => {
      await indexedDB.deleteDatabase('WhoaDatabase');
    });
    
    // 3. Navigate back to ensure a fresh session
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should shorten a URL and verify its presence in the registry', async ({ page }) => {
    const targetUrl = 'https://playwright.dev';
    const customCode = 'pw-test-' + Date.now();

    await page.getByTestId('destination-url-input').fill(targetUrl);
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(customCode);

    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();

    await expect(page.getByTestId('toast-notification')).toBeVisible();
    await expect(page.getByTestId('toast-notification')).toContainText('Short URL generated successfully');

    const row = page.getByTestId(`link-row-${customCode}`);
    await expect(row).toBeVisible();
    await expect(row.getByTestId(`link-code-${customCode}`)).toContainText(customCode);
  });

  test('should track analytics when a short link is visited', async ({ page, context }) => {
    const targetUrl = 'https://github.com/ohbus/link.whoa.sh';
    const customCode = 'link-whoa-' + Date.now();

    await page.getByTestId('destination-url-input').fill(targetUrl);
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(customCode);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();
    await expect(page.getByTestId(`link-row-${customCode}`)).toBeVisible();

    const redirectUrl = `http://127.0.0.1:8844/${customCode}`;
    const visitPage = await context.newPage();
    await visitPage.goto(redirectUrl);
    await visitPage.close();

    await expect(page.getByTestId(`link-clicks-${customCode}`)).toContainText('1', { timeout: 15000 });
    await expect(page.getByTestId('global-clicks-value')).not.toContainText('0');
  });

  test('should show detailed analytics drawer with time-series chart', async ({ page }) => {
    const targetUrl = 'https://kotlinlang.org';
    const code = 'kt-test';

    await page.getByTestId('destination-url-input').fill(targetUrl);
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();

    await page.getByTestId(`link-row-${code}`).click();

    await expect(page.getByTestId('analytics-drawer')).toBeVisible();
    await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
    await expect(page.getByTestId('drawer-original-url')).toContainText(targetUrl);
    await expect(page.getByTestId('drawer-chart')).toBeVisible();

    await page.getByTestId('close-drawer-btn').click();
    await expect(page.getByTestId('analytics-drawer')).toBeHidden();
  });

  test('should handle custom code collisions gracefully', async ({ page }) => {
    const code = 'collision-test';
    
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
