# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: analytics.spec.ts >> Analytics & Real-Time Pulse >> should handle backend connectivity failure
- Location: e2e/analytics.spec.ts:31:7

# Error details

```
Error: expect(locator).toContainText(expected) failed

Locator: getByTestId('system-status')
Expected substring: "Backend Offline"
Timeout: 15000ms
Error: element(s) not found

Call log:
  - Expect "toContainText" with timeout 15000ms
  - waiting for getByTestId('system-status')

```

# Page snapshot

```yaml
- generic [ref=e2]: "{\"statusCode\":404,\"errorCode\":\"200001\",\"message\":\"URL not found for short code: index.html\",\"timestamp\":\"2026-04-02T23:19:15.419236+02:00\"}"
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | test.describe('Analytics & Real-Time Pulse', () => {
  4  |   test.beforeEach(async ({ page, request }) => {
  5  |     await request.post('/api/testing/reset');
  6  |     await page.goto('/#/');
  7  |     await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
  8  |     await page.reload();
  9  |   });
  10 | 
  11 |   test('should observe authoritative global clicks incrementing', async ({ page }) => {
  12 |     // 1. Get initial value
  13 |     const initialText = await page.getByTestId('global-clicks-value').innerText();
  14 |     const initialValue = parseInt(initialText.replace(/,/g, '')) || 0;
  15 | 
  16 |     // 2. Wait for background simulation/heartbeat (10s interval)
  17 |     // We expect the counter to reel upwards
  18 |     await expect(async () => {
  19 |       const currentText = await page.getByTestId('global-clicks-value').innerText();
  20 |       const currentValue = parseInt(currentText.replace(/,/g, '')) || 0;
  21 |       expect(currentValue).toBeGreaterThan(initialValue);
  22 |     }).toPass({ timeout: 15000 });
  23 |   });
  24 | 
  25 |   test('should display live API latency from health-check', async ({ page }) => {
  26 |     // Latency should be a real number > 0ms
  27 |     await expect(page.getByTestId('latency-value')).not.toContainText('0ms');
  28 |     await expect(page.getByTestId('latency-value')).toContainText('ms');
  29 |   });
  30 | 
  31 |   test('should handle backend connectivity failure', async ({ page, context }) => {
  32 |     // Mock health check to return error
  33 |     await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
  34 |     
  35 |     // Wait for next heartbeat
> 36 |     await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 15000 });
     |                                                     ^ Error: expect(locator).toContainText(expected) failed
  37 |     await expect(page.getByTestId('system-status')).toHaveClass(/text-error/);
  38 |   });
  39 | });
  40 | 
```