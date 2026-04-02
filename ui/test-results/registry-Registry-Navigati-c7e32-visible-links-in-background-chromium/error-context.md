# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: registry.spec.ts >> Registry & Navigation >> should only sync visible links in background
- Location: e2e/registry.spec.ts:38:7

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
- generic [ref=e2]: "{\"statusCode\":404,\"errorCode\":\"200001\",\"message\":\"URL not found for short code: index.html\",\"timestamp\":\"2026-04-02T23:20:48.654292+02:00\"}"
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | test.describe('Registry & Navigation', () => {
  4  |   test.beforeEach(async ({ page, request }) => {
  5  |     await request.post('/api/testing/reset');
  6  |     await page.goto('/#/');
  7  |     await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
  8  |     await page.reload();
  9  |   });
  10 | 
  11 |   test('should show empty state when no links exist', async ({ page }) => {
  12 |     await expect(page.getByTestId('registry-empty-state')).toBeVisible();
  13 |     await expect(page.getByTestId('registry-empty-state')).toContainText('Registry Empty');
  14 |   });
  15 | 
  16 |   test('should handle multi-page keyset pagination with data stability', async ({ page }) => {
  17 |     // 1. Create 12 links (Page 1 holds 10)
  18 |     for (let i = 1; i <= 12; i++) {
  19 |       await page.getByTestId('destination-url-input').fill(`https://link-${i}.com`);
  20 |       await page.getByTestId('execute-shorten-btn').click();
  21 |       await page.waitForTimeout(100); // Small delay for sequential timestamps
  22 |     }
  23 | 
  24 |     // 2. Verify Page 1
  25 |     await expect(page.getByTestId('pagination-controls')).toBeVisible();
  26 |     await expect(page.getByTestId('current-page-label')).toContainText('PAGE 1');
  27 |     
  28 |     // 3. Navigate to Page 2
  29 |     await page.getByTestId('next-page-btn').click();
  30 |     await expect(page.getByTestId('current-page-label')).toContainText('PAGE 2');
  31 |     
  32 |     // 4. Verify Keyset Stability: Create new link on Page 2 (should reset to Page 1)
  33 |     await page.getByTestId('destination-url-input').fill('https://new-link.com');
  34 |     await page.getByTestId('execute-shorten-btn').click();
  35 |     await expect(page.getByTestId('current-page-label')).toContainText('PAGE 1');
  36 |   });
  37 | 
  38 |   test('should only sync visible links in background', async ({ page }) => {
  39 |     // 1. Create 15 links
  40 |     for (let i = 1; i <= 15; i++) {
> 41 |       await page.getByTestId('destination-url-input').fill(`https://sync-test-${i}.com`);
     |                                                       ^ Error: locator.fill: Test timeout of 60000ms exceeded.
  42 |       await page.getByTestId('execute-shorten-btn').click();
  43 |     }
  44 | 
  45 |     // 2. Scroll to bottom
  46 |     await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
  47 |     
  48 |     // 3. Wait for scroll-rest stabilization (500ms)
  49 |     await page.waitForTimeout(1000);
  50 |     
  51 |     // Check if the system-status pulses (meaning a sync request was sent)
  52 |     await expect(page.getByTestId('system-status')).toBeVisible();
  53 |   });
  54 | });
  55 | 
```