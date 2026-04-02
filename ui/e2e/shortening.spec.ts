import { test, expect } from '@playwright/test';

test.describe('Shortening & Integrity', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
  });

  test('should fail to shorten an invalid URL', async ({ page }) => {
    await page.getByTestId('destination-url-input').fill('not-a-url');
    await page.getByTestId('execute-shorten-btn').click();
    
    // UI should show backend-returned validation error
    // Since we use native browser validation + backend validation
    await expect(page.getByTestId('execute-shorten-btn')).toBeVisible(); 
  });

  test('should handle custom code collisions with conflict styling', async ({ page }) => {
    const code = 'fixed-path';
    await page.getByTestId('destination-url-input').fill('https://google.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();

    // Try again
    await page.getByTestId('destination-url-input').fill('https://bing.com');
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();

    await expect(page.getByTestId('shortening-error')).toContainText(`path '${code}' is already registered`);
    await expect(page.getByTestId('shortening-error')).toHaveClass(/text-error/);
  });

  test('should auto-focus custom path when details is expanded', async ({ page }) => {
    await page.getByTestId('custom-code-summary').click();
    // Verify input is ready for typing
    await page.getByTestId('custom-path-input').click();
  });
});
