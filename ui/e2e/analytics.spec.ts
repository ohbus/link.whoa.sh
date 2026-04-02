import { test, expect } from '@playwright/test';

test.describe('Analytics & Real-Time Pulse', () => {
  test.beforeEach(async ({ page, request }) => {
    await request.post('/api/testing/reset');
    await page.goto('/#/');
    await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
    await page.reload();
  });

  test('should observe authoritative global clicks incrementing', async ({ page }) => {
    // 1. Get initial value
    const initialText = await page.getByTestId('global-clicks-value').innerText();
    const initialValue = parseInt(initialText.replace(/,/g, '')) || 0;

    // 2. Wait for background simulation/heartbeat (10s interval)
    // We expect the counter to reel upwards
    await expect(async () => {
      const currentText = await page.getByTestId('global-clicks-value').innerText();
      const currentValue = parseInt(currentText.replace(/,/g, '')) || 0;
      expect(currentValue).toBeGreaterThan(initialValue);
    }).toPass({ timeout: 15000 });
  });

  test('should display live API latency from health-check', async ({ page }) => {
    // Latency should be a real number > 0ms
    await expect(page.getByTestId('latency-value')).not.toContainText('0ms');
    await expect(page.getByTestId('latency-value')).toContainText('ms');
  });

  test('should handle backend connectivity failure', async ({ page, context }) => {
    // Mock health check to return error
    await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
    
    // Wait for next heartbeat
    await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 15000 });
    await expect(page.getByTestId('system-status')).toHaveClass(/text-error/);
  });
});
