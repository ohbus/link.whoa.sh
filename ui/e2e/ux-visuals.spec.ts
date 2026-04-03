import { test, expect } from '@playwright/test';

test.describe('UX & Visual Interactions', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => {
      await indexedDB.deleteDatabase('WhoaDatabase');
    });
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should toggle sidebar and persist state layout', async ({ page }) => {
    const sidebar = page.getByTestId('sidebar');
    const mainCanvas = page.locator('main');

    // Initial state: Expanded
    await expect(sidebar).toHaveClass(/w-64/);
    await expect(mainCanvas).toHaveClass(/md:ml-64/);

    // Toggle: Collapse
    await page.getByTestId('sidebar-toggle').click();
    await expect(sidebar).toHaveClass(/w-20/);
    await expect(mainCanvas).toHaveClass(/md:ml-20/);

    // Toggle: Expand
    await page.getByTestId('sidebar-toggle').click();
    await expect(sidebar).toHaveClass(/w-64/);
  });

  test('should copy short link to clipboard', async ({ page, context }) => {
    // 1. Grant clipboard permissions
    await context.grantPermissions(['clipboard-read', 'clipboard-write']);

    // 2. Create a link
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('execute-shorten-btn').click();

    // Wait for stability
    await expect(page.getByTestId('execute-shorten-btn')).toContainText('Execute');

    const firstRowCode = await page
      .locator('tr')
      .nth(1)
      .getByTestId(/link-code-/)
      .innerText();

    // 3. Click copy in registry
    await page.getByTestId(`copy-link-${firstRowCode}`).click();

    // 4. Verify toast
    await expect(page.getByTestId('toast-notification')).toContainText('copied');

    // 5. Verify clipboard content
    const clipboardText = await page.evaluate(() => navigator.clipboard.readText());
    expect(clipboardText).toContain(firstRowCode);
  });

  test('should open analytics and verify chart structure', async ({ page }) => {
    // Create link
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('execute-shorten-btn').click();
    await expect(page.getByTestId('execute-shorten-btn')).toContainText('Execute');

    const firstRowCode = await page
      .locator('tr')
      .nth(1)
      .getByTestId(/link-code-/)
      .innerText();
    const cell = page.getByTestId(`link-code-${firstRowCode}`);
    await cell.click();

    // Verify Drawer
    await expect(page.getByTestId('analytics-drawer')).toBeVisible({ timeout: 15000 });

    // Verify Highcharts is rendering SVG
    const chartSvg = page.locator('.highcharts-container svg');
    await expect(chartSvg).toBeVisible();

    // Close using backdrop
    await page.getByTestId('drawer-backdrop').click({ position: { x: 10, y: 10 } });
    await expect(page.getByTestId('analytics-drawer')).toBeHidden();
  });
});
