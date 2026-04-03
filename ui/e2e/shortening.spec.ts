import { test, expect } from '@playwright/test';

test.describe('Shortening & Integrity', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should fail to shorten an invalid URL', async ({ page }) => {
    const input = page.getByTestId('destination-url-input');
    await input.fill('not-a-url');
    
    // Attempt to click - might be blocked by browser validation or just stay on page
    await page.getByTestId('execute-shorten-btn').click();
    
    // Verify input is marked invalid (Angular adds ng-invalid)
    await expect(input).toHaveClass(/ng-invalid/);
    
    // Button might be disabled depending on implementation, or just show no toast
    await expect(page.getByTestId('toast-notification')).not.toBeVisible();
  });

  test('should handle custom code collisions with conflict styling', async ({ page }) => {
    const code = 'fixed';
    await page.getByTestId('destination-url-input').fill('https://google.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();
    await expect(btn).toContainText('Execute');
    
    // Wait for the first one to appear
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();

    // Try to create another with the same code
    await page.getByTestId('destination-url-input').fill('https://bing.com');
    await page.getByTestId('custom-path-input').fill(code);
    await btn.click();

    // Verify Error Messaging
    const errorEl = page.getByTestId('shortening-error');
    await expect(errorEl).toBeVisible();
    await expect(errorEl).toContainText('already registered');
    await expect(errorEl).toHaveClass(/text-error/);
  });

  test('should auto-focus destination input on sidebar button click', async ({ page }) => {
    await page.getByTestId('sidebar-shorten-btn').click();
    const input = page.getByTestId('destination-url-input');
    await expect(input).toBeFocused();
  });
});
