import { test, expect } from '@playwright/test';

test.describe('Shortening & Integrity', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
  });

  test('should fail to shorten an invalid URL', async ({ page }) => {
    await page.getByTestId('destination-url-input').fill('not-a-url');
    const btn = page.getByTestId('execute-shorten-btn');
    await btn.click();
    
    // Frontend validation should trigger (browser validation or custom)
    const input = page.getByTestId('destination-url-input');
    const isValid = await input.evaluate((el: HTMLInputElement) => el.checkValidity());
    expect(isValid).toBe(false);
  });

  test('should handle custom code collisions with conflict styling', async ({ page }) => {
    const code = 'fixed';
    await page.getByTestId('destination-url-input').fill('https://google.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    
    const btn = page.getByTestId('execute-shorten-btn');
    await expect(btn).toBeEnabled();
    await btn.click();
    
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
    // Navigate away or just click
    await page.getByTestId('sidebar-shorten-btn').click();
    
    const input = page.getByTestId('destination-url-input');
    await expect(input).toBeFocused();
  });
});
