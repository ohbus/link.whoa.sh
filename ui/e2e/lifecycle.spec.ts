import { test, expect } from '@playwright/test';

test.describe('Whoa Link Shortener Full Lifecycle', () => {
  
  test.beforeEach(async ({ page, request }) => {
    // 1. Reset Backend State (PostgreSQL + Caffeine)
    const resetResponse = await request.post('/api/testing/reset');
    expect(resetResponse.ok()).toBeTruthy();

    // 2. Clear Browser State (IndexedDB)
    await page.goto('/#/');
    await page.evaluate(async () => {
      // @ts-ignore - Dexie might not be globally exposed, but we can target the DB name directly
      await indexedDB.deleteDatabase('WhoaDatabase');
    });
    
    // 3. Navigate back to ensure a fresh session
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should shorten a URL and verify its presence in the registry', async ({ page }) => {
    const targetUrl = 'https://playwright.dev';
    const customCode = 'pw-test-' + Date.now();

    // Fill the shortening form
    await page.getByTestId('destination-url-input').fill(targetUrl);
    
    // Expand custom code section
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(customCode);

    // Execute shortening
    await page.getByTestId('execute-shorten-btn').click();

    // Verify Success Notification
    await expect(page.getByTestId('toast-notification')).toBeVisible();
    await expect(page.getByTestId('toast-notification')).toContainText('Short URL generated successfully');

    // Verify presence in registry table
    const row = page.getByTestId(`link-row-${customCode}`);
    await expect(row).toBeVisible();
    await expect(row.getByTestId(`link-code-${customCode}`)).toContainText(customCode);
  });

  test('should track analytics when a short link is visited', async ({ page, context }) => {
    const targetUrl = 'https://github.com/ohbus/link.whoa.sh';
    const customCode = 'link-whoa-' + Date.now();

    // 1. Create the link
    await page.getByTestId('destination-url-input').fill(targetUrl);
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(customCode);
    await page.getByTestId('execute-shorten-btn').click();
    await expect(page.getByTestId(`link-row-${customCode}`)).toBeVisible();

    // 2. Perform a real redirect in a new tab
    const [newPage] = await Promise.all([
      context.waitForEvent('page'),
      page.getByTestId(`copy-link-${customCode}`).click() // Not actually clicking copy, just using the row trigger
    ]);
    
    // Actually, let's navigate to the redirect URL manually to be sure
    const redirectUrl = `http://127.0.0.1:8844/${customCode}`;
    const visitPage = await context.newPage();
    await visitPage.goto(redirectUrl);
    await visitPage.close();

    // 3. Verify analytics sync in the UI
    // Wait for the background SyncService to pulse (delta sync)
    await expect(page.getByTestId(`link-clicks-${customCode}`)).toContainText('1', { timeout: 15000 });

    // 4. Verify Global Clicks increment
    await expect(page.getByTestId('global-clicks-value')).not.toContainText('0');
  });

  test('should show detailed analytics drawer with time-series chart', async ({ page }) => {
    const targetUrl = 'https://kotlinlang.org';
    const code = 'kt-test';

    // Create link
    await page.getByTestId('destination-url-input').fill(targetUrl);
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();

    // Open drawer
    await page.getByTestId(`link-row-${code}`).click();

    // Verify Drawer content
    await expect(page.getByTestId('analytics-drawer')).toBeVisible();
    await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
    await expect(page.getByTestId('drawer-original-url')).toContainText(targetUrl);
    
    // Verify Chart visibility
    await expect(page.getByTestId('drawer-chart')).toBeVisible();

    // Close drawer
    await page.getByTestId('close-drawer-btn').click();
    await expect(page.getByTestId('analytics-drawer')).toBeHidden();
  });

  test('should handle custom code collisions gracefully', async ({ page }) => {
    const code = 'collision-test';
    
    // 1. Create first link
    await page.getByTestId('destination-url-input').fill('https://first.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();

    // 2. Try to create second link with same code
    await page.getByTestId('destination-url-input').fill('https://second.com');
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();

    // 3. Verify Error Messaging
    await expect(page.getByTestId('shortening-error')).toBeVisible();
    await expect(page.getByTestId('shortening-error')).toContainText(`The path '${code}' is already registered`);
  });
});
