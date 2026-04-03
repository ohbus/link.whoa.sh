import { test, expect } from '@playwright/test';

test.describe('Analytics & Real-Time Pulse', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('http://127.0.0.1:8844/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
    await expect(page.getByTestId('app-logo')).toBeVisible();
  });

  test('should observe authoritative global clicks incrementing', async ({ page, context }) => {
    // 1. Initial State
    const counter = page.getByTestId('global-clicks-value');
    const initialText = await counter.innerText();
    const initialValue = parseInt(initialText.replace(/,/g, '')) || 0;

    // 2. Shorten a link
    await page.getByTestId('destination-url-input').fill('https://example.com');
    await page.getByTestId('execute-shorten-btn').click();
    
    // Wait for button stability
    await expect(page.getByTestId('execute-shorten-btn')).toContainText('Execute');
    const code = await page.locator('tr').nth(1).getByTestId(/link-code-/).innerText();

    // 3. Generate hits (using no-redirect to avoid SSL issues)
    for(let i=0; i<3; i++) {
      await context.request.get(`http://127.0.0.1:8844/${code}`, { maxRedirects: 0 });
    }

    // 4. Force UI refresh and Verify Global Pulse
    await page.evaluate(() => (window as any).WhoaApp.forceRefreshAnalytics());
    
    await expect(async () => {
      const currentText = await counter.innerText();
      const currentValue = parseInt(currentText.replace(/,/g, '')) || 0;
      expect(currentValue).toBeGreaterThan(initialValue);
    }).toPass({ timeout: 15000 });
  });

  test('should display live API latency from health-check', async ({ page }) => {
    await expect(page.getByTestId('api-latency-pulse')).toBeVisible();
    const latencyText = await page.getByTestId('api-latency-pulse').innerText();
    expect(latencyText).toMatch(/\d+ms/);
  });

  test('should handle backend connectivity failure', async ({ page, context }) => {
    // Mock failure
    await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
    
    // Wait for next heartbeat (max 15s)
    await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 30000 });
    await expect(page.getByTestId('system-status')).toHaveClass(/text-error/);
  });
});
