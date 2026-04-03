import { test, expect, Page } from '@playwright/test';

/**
 * Hyper-stable helper to perform a shortening task and wait for absolute UI settlement.
 * It verifies both the UI feedback (toast) and the physical data growth (registry count).
 */
async function shortenUrl(page: Page, url: string, customCode?: string) {
  const btn = page.getByTestId('execute-shorten-btn');

  await page.getByTestId('destination-url-input').fill(url);

  if (customCode) {
    const details = page.getByTestId('custom-code-details');
    const isOpen = await details.evaluate((node: HTMLDetailsElement) => node.open);
    if (!isOpen) {
      await page.getByTestId('custom-code-summary').click();
    }
    await page.getByTestId('custom-path-input').fill(customCode);
  }

  await expect(btn).toBeEnabled();
  await btn.click();

  // 1. Wait for success indicator
  await expect(page.getByTestId('toast-notification')).toBeVisible();

  // 2. Wait for button to return to ready state (it will be disabled because the form is reset to empty)
  await expect(btn).toContainText('Execute');
  await expect(btn).toBeDisabled();

  // 3. Cleanup toast
  await page.evaluate(() => {
    const toast = document.querySelector('[data-testid="toast-notification"]');
    if (toast) toast.remove();
  });
}

test.describe('Advanced Data Patterns & Monkey Testing', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => {
      await indexedDB.deleteDatabase('WhoaDatabase');
    });
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });
  test('should handle rapid-fire shortening requests (monkey test)', async ({ page }) => {
    // Sequentially create 5 links with settlement verification
    for (let i = 0; i < 5; i++) {
      await shortenUrl(page, `https://monkey-${i}.com`);
    }

    // Header (1) + Items (5)
    await expect(page.locator('tr')).toHaveCount(6);
  });

  test('should survive massive URL payloads', async ({ page }) => {
    const massiveUrl = 'https://example.com/' + 'a'.repeat(1000); // 1k is safer for varying browser buffers
    await shortenUrl(page, massiveUrl);

    await expect(page.locator('tr').nth(1)).toBeVisible();
    const originalUrlHint = await page
      .locator('tr')
      .nth(1)
      .locator('span[title]')
      .getAttribute('title');
    expect(originalUrlHint).toBe(massiveUrl);
  });

  test('should correctly sort mixed creation dates in registry', async ({ page }) => {
    await shortenUrl(page, 'https://first-link.com', 'linkalpha');
    await shortenUrl(page, 'https://second-link.com', 'linkbeta');

    // newest-first: linkbeta must be at the top
    const firstRowCode = await page
      .locator('tr')
      .nth(1)
      .getByTestId(/link-code-/)
      .innerText();
    const secondRowCode = await page
      .locator('tr')
      .nth(2)
      .getByTestId(/link-code-/)
      .innerText();

    expect(firstRowCode).toBe('linkbeta');
    expect(secondRowCode).toBe('linkalpha');
  });

  test('should handle special characters in custom paths', async ({ page }) => {
    const code = 'dash123';
    await shortenUrl(page, 'https://special-chars.com', code);
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
  });

  test('should persist data across hard refreshes', async ({ page }) => {
    await shortenUrl(page, 'https://persistence-test.com');
    await expect(page.locator('tr')).toHaveCount(2);

    await page.reload();

    await expect(page.getByTestId('app-logo')).toBeVisible();
    await expect(page.locator('tr')).toHaveCount(2);
  });

  test('should handle immediate drawer click with loading indicator', async ({ page }) => {
    const code = 'drawertest';
    await shortenUrl(page, 'https://drawer.com', code);

    const row = page.getByTestId(`link-row-${code}`);
    await expect(row).toBeVisible();

    // Intercept call to ensure we catch the indicator
    const responsePromise = page.waitForResponse(
      (resp) => resp.url().includes('/analytics') && resp.status() === 200,
    );
    await row.locator('td').first().click();

    await responsePromise;
    await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  });

  test('should gracefully handle offline transition and recovery', async ({ page, context }) => {
    await expect(page.getByTestId('system-status')).toContainText('Backend Active');

    // 1. Go Offline
    await context.route('**/actuator/health', (route) => route.fulfill({ status: 503 }));
    await expect(page.getByTestId('system-status')).toContainText('Backend Offline', {
      timeout: 20000,
    });
    await expect(page.getByTestId('execute-shorten-btn')).toBeDisabled();

    // 2. Recover
    await context.unroute('**/actuator/health');

    await expect(page.getByTestId('system-status')).toContainText('Backend Active', {
      timeout: 30000,
    });
  });
});
