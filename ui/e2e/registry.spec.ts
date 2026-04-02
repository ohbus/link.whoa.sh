import { test, expect } from '@playwright/test';

test.describe('Registry & Navigation', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
  });

  test('should show empty state when no links exist', async ({ page }) => {
    await expect(page.getByTestId('registry-empty-state')).toBeVisible();
    await expect(page.getByTestId('registry-empty-state')).toContainText('Registry Empty');
  });

  test('should handle multi-page keyset pagination with data stability', async ({ page }) => {
    // 1. Create 12 links (Page 1 holds 10)
    for (let i = 1; i <= 12; i++) {
      await page.getByTestId('destination-url-input').fill(`https://link-${i}.com`);
      await page.getByTestId('execute-shorten-btn').click();
      await page.waitForTimeout(100); // Small delay for sequential timestamps
    }

    // 2. Verify Page 1
    await expect(page.getByTestId('pagination-controls')).toBeVisible();
    await expect(page.getByTestId('current-page-label')).toContainText('PAGE 1');
    
    // 3. Navigate to Page 2
    await page.getByTestId('next-page-btn').click();
    await expect(page.getByTestId('current-page-label')).toContainText('PAGE 2');
    
    // 4. Verify Keyset Stability: Create new link on Page 2 (should reset to Page 1)
    await page.getByTestId('destination-url-input').fill('https://new-link.com');
    await page.getByTestId('execute-shorten-btn').click();
    await expect(page.getByTestId('current-page-label')).toContainText('PAGE 1');
  });

  test('should only sync visible links in background', async ({ page }) => {
    // 1. Create 15 links
    for (let i = 1; i <= 15; i++) {
      await page.getByTestId('destination-url-input').fill(`https://sync-test-${i}.com`);
      await page.getByTestId('execute-shorten-btn').click();
    }

    // 2. Scroll to bottom
    await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
    
    // 3. Wait for scroll-rest stabilization (500ms)
    await page.waitForTimeout(1000);
    
    // Check if the system-status pulses (meaning a sync request was sent)
    await expect(page.getByTestId('system-status')).toBeVisible();
  });
});
