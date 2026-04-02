# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: advanced-patterns.spec.ts >> Advanced Data Patterns & Monkey Testing >> should handle immediate drawer click with loading indicator
- Location: e2e/advanced-patterns.spec.ts:89:7

# Error details

```
Error: apiRequestContext.post: connect ECONNREFUSED 127.0.0.1:8844
Call log:
  - → POST http://127.0.0.1:8844/api/testing/reset
    - user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.7727.15 Safari/537.36
    - accept: */*
    - accept-encoding: gzip,deflate,br

```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | 
  3   | test.describe('Advanced Data Patterns & Monkey Testing', () => {
  4   |   test.beforeEach(async ({ page, request }) => {
> 5   |     await request.post('http://127.0.0.1:8844/api/testing/reset');
      |                   ^ Error: apiRequestContext.post: connect ECONNREFUSED 127.0.0.1:8844
  6   |     await page.goto('/#/');
  7   |     await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
  8   |     await page.reload();
  9   |   });
  10  | 
  11  |   test('should debounce network calls during rapid monkey scrolling', async ({ page }) => {
  12  |     // 1. Seed links to enable scrolling
  13  |     for (let i = 1; i <= 20; i++) {
  14  |       await page.getByTestId('destination-url-input').fill(`https://monkey-${i}.com`);
  15  |       await page.getByTestId('execute-shorten-btn').click();
  16  |       await expect(page.getByTestId(`link-row-monkey-${i}`, { timeout: 2000 }).or(page.locator('tr').nth(i))).toBeDefined();
  17  |     }
  18  | 
  19  |     let syncRequestCount = 0;
  20  |     // Intercept bulk analytics calls
  21  |     await page.route('**/api/v1/urls/analytics/bulk', async route => {
  22  |       syncRequestCount++;
  23  |       await route.continue();
  24  |     });
  25  | 
  26  |     // 2. Perform "Monkey Scrolling"
  27  |     // Rapidly scroll up and down multiple times
  28  |     for (let i = 0; i < 5; i++) {
  29  |       await page.mouse.wheel(0, 500);
  30  |       await page.waitForTimeout(50);
  31  |       await page.mouse.wheel(0, -500);
  32  |       await page.waitForTimeout(50);
  33  |     }
  34  | 
  35  |     // 3. Wait a moment and check request count
  36  |     // Since we have a 500ms backoff, many scrolls should result in at most 1 or 2 calls 
  37  |     // depending on the initial state and final rest.
  38  |     await page.waitForTimeout(1000); 
  39  |     
  40  |     // We expect the count to be very low (usually 1 after the final rest)
  41  |     expect(syncRequestCount).toBeLessThanOrEqual(2);
  42  |   });
  43  | 
  44  |   test('should fire correct delta requests with map and timestamp', async ({ page }) => {
  45  |     const code = 'delta-test';
  46  |     await page.getByTestId('destination-url-input').fill('https://example.com');
  47  |     await page.getByTestId('custom-code-summary').click();
  48  |     await page.getByTestId('custom-path-input').fill(code);
  49  |     await page.getByTestId('execute-shorten-btn').click();
  50  |     await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
  51  | 
  52  |     // Capture the next sync request
  53  |     const syncRequestPromise = page.waitForRequest(request => 
  54  |       request.url().includes('/analytics/bulk') && request.method() === 'POST'
  55  |     );
  56  | 
  57  |     // Trigger a rest-event by scrolling slightly
  58  |     await page.mouse.wheel(0, 10);
  59  |     
  60  |     const request = await syncRequestPromise;
  61  |     const postData = JSON.parse(request.postData() || '{}');
  62  | 
  63  |     // Verify "intendedable" Map protocol
  64  |     expect(postData.currentCounts).toBeDefined();
  65  |     expect(postData.currentCounts[code]).toBe(0); // Initial local state
  66  |     
  67  |     // lastSyncedAt might be null on first call or a number
  68  |     // Subsequent calls will have the serverTimestamp
  69  |   });
  70  | 
  71  |   test('should show correct counts after backend update during delta sync', async ({ page, context }) => {
  72  |     const code = 'live-update';
  73  |     await page.getByTestId('destination-url-input').fill('https://example.com');
  74  |     await page.getByTestId('custom-code-summary').click();
  75  |     await page.getByTestId('custom-path-input').fill(code);
  76  |     await page.getByTestId('execute-shorten-btn').click();
  77  | 
  78  |     // Verify initial count is 0
  79  |     await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('0');
  80  | 
  81  |     // Simulate a hit on the backend
  82  |     await context.request.get(`http://127.0.0.1:8844/${code}`);
  83  | 
  84  |     // Wait for the background SyncService to pulse and update the UI
  85  |     // The animated counter will transition from 0 to 1
  86  |     await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('1', { timeout: 15000 });
  87  |   });
  88  | 
  89  |   test('should handle immediate drawer click with loading indicator', async ({ page, context }) => {
  90  |     const code = 'drawer-test';
  91  |     await page.getByTestId('destination-url-input').fill('https://example.com');
  92  |     await page.getByTestId('custom-path-input').fill(code);
  93  |     await page.getByTestId('execute-shorten-btn').click();
  94  | 
  95  |     // 1. Intercept analytics call and delay it to see loading state
  96  |     await page.route(`**/api/v1/urls/${code}/analytics`, async route => {
  97  |       await page.waitForTimeout(500);
  98  |       await route.continue();
  99  |     });
  100 | 
  101 |     // 2. Click row immediately
  102 |     await page.getByTestId(`link-row-${code}`).click();
  103 | 
  104 |     // 3. Verify loading pulse is visible
  105 |     await expect(page.getByTestId('drawer-sync-status')).toBeVisible();
```