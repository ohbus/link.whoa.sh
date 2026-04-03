import { test, expect } from '@playwright/test';

test.describe('Registry & Navigation', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should show empty state when no links exist', async ({ page }) => {
    await expect(page.getByTestId('registry-empty-state')).toBeVisible();
    await expect(page.getByTestId('registry-empty-state')).toContainText('Registry Empty');
  });

  test('should handle multi-page keyset pagination with data stability', async ({ page }) => {
    // 1. Create 12 links (Page 1 holds 10)
    for (let i = 1; i <= 12; i++) {
      await page.getByTestId('destination-url-input').fill(`https://link${i}.com`);
      const btn = page.getByTestId('execute-shorten-btn');
      
      // Ensure button is ready
      await expect(btn).toBeEnabled();
      await btn.click();
      
      // Wait for the specific success indicator for this iteration
      await expect(page.getByTestId('toast-notification')).toBeVisible();
      // Button returns to stable state
      await expect(btn).toContainText('Execute');
      // Wait for registry list to physically grow
      await expect(page.locator('tr')).toHaveCount(Math.min(i + 1, 11));
      
      // Clear toast for next run
      await page.evaluate(() => {
        const toast = document.querySelector('[data-testid="toast-notification"]');
        if (toast) toast.remove();
      });
    }

    // 2. Verify Page 1
    const rowsPage1 = page.locator('tr');
    await expect(rowsPage1).toHaveCount(11); // Header + 10 rows

    // 3. Navigate to Page 2
    await page.getByTestId('next-page-btn').click();
    const rowsPage2 = page.locator('tr');
    await expect(rowsPage2).toHaveCount(3); // Header + 2 rows

    // 4. Navigate Back
    await page.getByTestId('prev-page-btn').click();
    await expect(page.locator('tr')).toHaveCount(11);
  });

  test('should only sync visible links in background', async ({ page }) => {
    // 1. Create 15 links
    for (let i = 1; i <= 15; i++) {
      await page.getByTestId('destination-url-input').fill(`https://synctest${i}.com`);
      const btn = page.getByTestId('execute-shorten-btn');
      
      await expect(btn).toBeEnabled();
      await btn.click();
      
      // Wait for stability before next fill
      await expect(btn).toContainText('Execute');
      await expect(page.getByTestId('toast-notification')).toBeVisible();
      
      await page.evaluate(() => {
        const toast = document.querySelector('[data-testid="toast-notification"]');
        if (toast) toast.remove();
      });
    }

    // Intercept bulk sync
    const syncRequestPromise = page.waitForRequest(request => 
      request.url().includes('/analytics/bulk')
    );

    // Trigger scroll rest by scrolling slightly
    await page.mouse.wheel(0, 10);
    
    const request = await syncRequestPromise;
    const postData = JSON.parse(request.postData() || '{}');
    
    // We expect at most 10 codes (page size) in the sync request
    const codesInSync = Object.keys(postData.currentCounts);
    expect(codesInSync.length).toBeLessThanOrEqual(10);
  });
});
