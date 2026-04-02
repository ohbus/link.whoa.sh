import { test, expect } from '@playwright/test';

test.describe('Shortening & Integrity', () => {
  test.beforeEach(async ({ page, request }) => {
    // Reset Backend State
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    
    await page.goto('/#/');
    await page.evaluate(async () => { 
      await indexedDB.deleteDatabase('WhoaDatabase'); 
    });
    await page.reload();
  });

  test('should handle custom code collisions with conflict styling', async ({ page }) => {
    const code = 'fixed-path';
    await page.getByTestId('destination-url-input').fill('https://google.com');
    await page.getByTestId('custom-code-summary').click();
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();
    
    // Wait for the first one to appear
    await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();

    // Try to create another with the same code
    await page.getByTestId('destination-url-input').fill('https://bing.com');
    await page.getByTestId('custom-path-input').fill(code);
    await page.getByTestId('execute-shorten-btn').click();

    // Verify Error Messaging
    await expect(page.getByTestId('shortening-error')).toContainText(`path '${code}' is already registered`);
    await expect(page.getByTestId('shortening-error')).toHaveClass(/text-error/);
  });

  test('should auto-focus destination input on sidebar button click', async ({ page }) => {
    // Scroll down away from input
    await page.evaluate(() => window.scrollTo(0, 1000));
    
    // Click sidebar button
    await page.getByTestId('sidebar-shorten-btn').click();
    
    // Verify focus and visibility
    const input = page.getByTestId('destination-url-input');
    await expect(input).toBeFocused();
    await expect(input).toBeInViewport();
  });
});
