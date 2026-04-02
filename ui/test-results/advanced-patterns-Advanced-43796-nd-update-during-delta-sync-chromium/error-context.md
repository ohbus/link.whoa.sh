# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: advanced-patterns.spec.ts >> Advanced Data Patterns & Monkey Testing >> should show correct counts after backend update during delta sync
- Location: e2e/advanced-patterns.spec.ts:63:7

# Error details

```
Test timeout of 60000ms exceeded.
```

```
Error: locator.fill: Test timeout of 60000ms exceeded.
Call log:
  - waiting for getByTestId('destination-url-input')

```

# Page snapshot

```yaml
- generic [ref=e2]: "{\"statusCode\":404,\"errorCode\":\"200001\",\"message\":\"URL not found for short code: index.html\",\"timestamp\":\"2026-04-02T23:15:43.116064+02:00\"}"
```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | 
  3   | test.describe('Advanced Data Patterns & Monkey Testing', () => {
  4   |   test.beforeEach(async ({ page, request }) => {
  5   |     await request.post('http://127.0.0.1:8844/api/testing/reset');
  6   |     await page.goto('/#/');
  7   |     await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
  8   |     await page.reload();
  9   |   });
  10  | 
  11  |   test('should debounce network calls during rapid monkey scrolling', async ({ page }) => {
  12  |     // 1. Seed links to enable scrolling
  13  |     for (let i = 1; i <= 20; i++) {
  14  |       await page.getByTestId('destination-url-input').fill(`https://monkey-${i}.com`);
  15  |       const btn = page.getByTestId('execute-shorten-btn');
  16  |       await expect(btn).toBeEnabled();
  17  |       await btn.click();
  18  |       // Wait for registry to update
  19  |       await expect(page.getByTestId(/link-row-monkey-/).first()).toBeVisible();
  20  |     }
  21  | 
  22  |     let syncRequestCount = 0;
  23  |     await page.route('**/api/v1/urls/analytics/bulk', async route => {
  24  |       syncRequestCount++;
  25  |       await route.continue();
  26  |     });
  27  | 
  28  |     for (let i = 0; i < 5; i++) {
  29  |       await page.mouse.wheel(0, 500);
  30  |       await page.waitForTimeout(50);
  31  |       await page.mouse.wheel(0, -500);
  32  |       await page.waitForTimeout(50);
  33  |     }
  34  | 
  35  |     await page.waitForTimeout(1000); 
  36  |     expect(syncRequestCount).toBeLessThanOrEqual(2);
  37  |   });
  38  | 
  39  |   test('should fire correct delta requests with map and timestamp', async ({ page }) => {
  40  |     const code = 'delta-test';
  41  |     await page.getByTestId('destination-url-input').fill('https://example.com');
  42  |     await page.getByTestId('custom-code-summary').click();
  43  |     await page.getByTestId('custom-path-input').fill(code);
  44  |     
  45  |     const btn = page.getByTestId('execute-shorten-btn');
  46  |     await expect(btn).toBeEnabled();
  47  |     await btn.click();
  48  |     await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
  49  | 
  50  |     const syncRequestPromise = page.waitForRequest(request => 
  51  |       request.url().includes('/analytics/bulk') && request.method() === 'POST'
  52  |     );
  53  | 
  54  |     await page.mouse.wheel(0, 10);
  55  |     
  56  |     const request = await syncRequestPromise;
  57  |     const postData = JSON.parse(request.postData() || '{}');
  58  | 
  59  |     expect(postData.currentCounts).toBeDefined();
  60  |     expect(postData.currentCounts[code]).toBe(0);
  61  |   });
  62  | 
  63  |   test('should show correct counts after backend update during delta sync', async ({ page, context }) => {
  64  |     const code = 'live-update';
> 65  |     await page.getByTestId('destination-url-input').fill('https://example.com');
      |                                                     ^ Error: locator.fill: Test timeout of 60000ms exceeded.
  66  |     await page.getByTestId('custom-code-summary').click();
  67  |     await page.getByTestId('custom-path-input').fill(code);
  68  |     
  69  |     const btn = page.getByTestId('execute-shorten-btn');
  70  |     await expect(btn).toBeEnabled();
  71  |     await btn.click();
  72  | 
  73  |     await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('0');
  74  | 
  75  |     await context.request.get(`http://127.0.0.1:8844/${code}`);
  76  | 
  77  |     await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('1', { timeout: 15000 });
  78  |   });
  79  | 
  80  |   test('should handle immediate drawer click with loading indicator', async ({ page }) => {
  81  |     const code = 'drawer-test';
  82  |     await page.getByTestId('destination-url-input').fill('https://example.com');
  83  |     await page.getByTestId('custom-path-input').fill(code);
  84  |     
  85  |     const btn = page.getByTestId('execute-shorten-btn');
  86  |     await expect(btn).toBeEnabled();
  87  |     await btn.click();
  88  | 
  89  |     await page.route(`**/api/v1/urls/${code}/analytics`, async route => {
  90  |       await page.waitForTimeout(1000);
  91  |       await route.continue();
  92  |     });
  93  | 
  94  |     await page.getByTestId(`link-row-${code}`).click();
  95  |     await expect(page.getByTestId('drawer-sync-status')).toBeVisible();
  96  |     await expect(page.getByTestId('drawer-sync-status')).toBeHidden({ timeout: 10000 });
  97  |     await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  98  |   });
  99  | 
  100 |   test('should gracefully handle offline transition and recovery', async ({ page, context }) => {
  101 |     await expect(page.getByTestId('system-status')).toContainText('Backend Active');
  102 | 
  103 |     await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
  104 |     await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 15000 });
  105 |     await expect(page.getByTestId('execute-shorten-btn')).toBeDisabled();
  106 | 
  107 |     await context.unroute('**/actuator/health');
  108 |     await expect(page.getByTestId('system-status')).toContainText('Backend Active', { timeout: 15000 });
  109 |     await expect(page.getByTestId('execute-shorten-btn')).toBeEnabled();
  110 |   });
  111 | });
  112 | 
```