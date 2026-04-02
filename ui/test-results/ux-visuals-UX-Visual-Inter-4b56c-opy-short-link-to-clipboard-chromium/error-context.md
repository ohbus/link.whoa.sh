# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: ux-visuals.spec.ts >> UX & Visual Interactions >> should copy short link to clipboard
- Location: e2e/ux-visuals.spec.ts:29:7

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
- generic [ref=e2]: "{\"statusCode\":404,\"errorCode\":\"200001\",\"message\":\"URL not found for short code: index.html\",\"timestamp\":\"2026-04-02T23:24:05.346875+02:00\"}"
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | test.describe('UX & Visual Interactions', () => {
  4  |   test.beforeEach(async ({ page, request }) => {
  5  |     await request.post('http://127.0.0.1:8844/api/testing/reset');
  6  |     await page.goto('/#/');
  7  |     await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
  8  |     await page.reload();
  9  |   });
  10 | 
  11 |   test('should toggle sidebar and persist state layout', async ({ page }) => {
  12 |     const sidebar = page.getByTestId('sidebar');
  13 |     const mainCanvas = page.locator('main');
  14 | 
  15 |     // Initial state: Expanded
  16 |     await expect(sidebar).toHaveClass(/w-64/);
  17 |     await expect(mainCanvas).toHaveClass(/md:ml-64/);
  18 | 
  19 |     // Toggle: Collapse
  20 |     await page.getByTestId('sidebar-toggle').click();
  21 |     await expect(sidebar).toHaveClass(/w-20/);
  22 |     await expect(mainCanvas).toHaveClass(/md:ml-20/);
  23 | 
  24 |     // Toggle: Expand
  25 |     await page.getByTestId('sidebar-toggle').click();
  26 |     await expect(sidebar).toHaveClass(/w-64/);
  27 |   });
  28 | 
  29 |   test('should copy short link to clipboard', async ({ page, context }) => {
  30 |     // 1. Grant clipboard permissions
  31 |     await context.grantPermissions(['clipboard-read', 'clipboard-write']);
  32 | 
  33 |     // 2. Create a link
> 34 |     await page.getByTestId('destination-url-input').fill('https://example.com');
     |                                                     ^ Error: locator.fill: Test timeout of 60000ms exceeded.
  35 |     await page.getByTestId('execute-shorten-btn').click();
  36 |     
  37 |     const code = await page.getByTestId('destination-url-input').getAttribute('data-last-code'); // Assuming I add this or find it
  38 |     // Let's just find the first row
  39 |     const firstRowCode = await page.locator('tr').nth(1).getByTestId(/link-code-/).innerText();
  40 | 
  41 |     // 3. Click copy in registry
  42 |     await page.getByTestId(`copy-link-${firstRowCode}`).click();
  43 | 
  44 |     // 4. Verify toast
  45 |     await expect(page.getByTestId('toast-notification')).toContainText('Link copied');
  46 | 
  47 |     // 5. Verify clipboard content
  48 |     const clipboardText = await page.evaluate(() => navigator.clipboard.readText());
  49 |     expect(clipboardText).toContain(firstRowCode);
  50 |   });
  51 | 
  52 |   test('should open analytics and verify chart structure', async ({ page }) => {
  53 |     // Create link
  54 |     await page.getByTestId('destination-url-input').fill('https://example.com');
  55 |     await page.getByTestId('execute-shorten-btn').click();
  56 |     
  57 |     const firstRow = page.locator('tr').nth(1);
  58 |     await firstRow.click();
  59 | 
  60 |     // Verify Drawer
  61 |     await expect(page.getByTestId('analytics-drawer')).toBeVisible();
  62 |     
  63 |     // Verify Highcharts is rendering SVG
  64 |     const chartSvg = page.locator('.highcharts-container svg');
  65 |     await expect(chartSvg).toBeVisible();
  66 |     
  67 |     // Close using backdrop
  68 |     await page.getByTestId('drawer-backdrop').click({ position: { x: 10, y: 10 } });
  69 |     await expect(page.getByTestId('analytics-drawer')).toBeHidden();
  70 |   });
  71 | });
  72 | 
```