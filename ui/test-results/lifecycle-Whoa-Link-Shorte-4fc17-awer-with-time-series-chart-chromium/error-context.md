# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: lifecycle.spec.ts >> Whoa Link Shortener Full Lifecycle >> should show detailed analytics drawer with time-series chart
- Location: e2e/lifecycle.spec.ts:63:7

# Error details

```
Error: expect(received).toBeTruthy()

Received: false
```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | 
  3   | test.describe('Whoa Link Shortener Full Lifecycle', () => {
  4   |   
  5   |   test.beforeEach(async ({ page, request }) => {
  6   |     // 1. Reset Backend State (PostgreSQL + Caffeine)
  7   |     const resetResponse = await request.post('http://127.0.0.1:8844/api/testing/reset');
> 8   |     expect(resetResponse.ok()).toBeTruthy();
      |                                ^ Error: expect(received).toBeTruthy()
  9   | 
  10  |     // 2. Clear Browser State (IndexedDB)
  11  |     await page.goto('/#/');
  12  |     await page.evaluate(async () => {
  13  |       await indexedDB.deleteDatabase('WhoaDatabase');
  14  |     });
  15  |     
  16  |     // 3. Navigate back to ensure a fresh session
  17  |     await page.reload();
  18  |     await expect(page.getByTestId('app-logo')).toBeVisible();
  19  |   });
  20  | 
  21  |   test('should shorten a URL and verify its presence in the registry', async ({ page }) => {
  22  |     const targetUrl = 'https://playwright.dev';
  23  |     const customCode = 'pw-test-' + Date.now();
  24  | 
  25  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  26  |     await page.getByTestId('custom-code-summary').click();
  27  |     await page.getByTestId('custom-path-input').fill(customCode);
  28  | 
  29  |     const btn = page.getByTestId('execute-shorten-btn');
  30  |     await expect(btn).toBeEnabled();
  31  |     await btn.click();
  32  | 
  33  |     await expect(page.getByTestId('toast-notification')).toBeVisible();
  34  |     await expect(page.getByTestId('toast-notification')).toContainText('Short URL generated successfully');
  35  | 
  36  |     const row = page.getByTestId(`link-row-${customCode}`);
  37  |     await expect(row).toBeVisible();
  38  |     await expect(row.getByTestId(`link-code-${customCode}`)).toContainText(customCode);
  39  |   });
  40  | 
  41  |   test('should track analytics when a short link is visited', async ({ page, context }) => {
  42  |     const targetUrl = 'https://github.com/ohbus/link.whoa.sh';
  43  |     const customCode = 'link-whoa-' + Date.now();
  44  | 
  45  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  46  |     await page.getByTestId('custom-code-summary').click();
  47  |     await page.getByTestId('custom-path-input').fill(customCode);
  48  |     
  49  |     const btn = page.getByTestId('execute-shorten-btn');
  50  |     await expect(btn).toBeEnabled();
  51  |     await btn.click();
  52  |     await expect(page.getByTestId(`link-row-${customCode}`)).toBeVisible();
  53  | 
  54  |     const redirectUrl = `http://127.0.0.1:8844/${customCode}`;
  55  |     const visitPage = await context.newPage();
  56  |     await visitPage.goto(redirectUrl);
  57  |     await visitPage.close();
  58  | 
  59  |     await expect(page.getByTestId(`link-clicks-${customCode}`)).toContainText('1', { timeout: 15000 });
  60  |     await expect(page.getByTestId('global-clicks-value')).not.toContainText('0');
  61  |   });
  62  | 
  63  |   test('should show detailed analytics drawer with time-series chart', async ({ page }) => {
  64  |     const targetUrl = 'https://kotlinlang.org';
  65  |     const code = 'kt-test';
  66  | 
  67  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  68  |     await page.getByTestId('custom-code-summary').click();
  69  |     await page.getByTestId('custom-path-input').fill(code);
  70  |     
  71  |     const btn = page.getByTestId('execute-shorten-btn');
  72  |     await expect(btn).toBeEnabled();
  73  |     await btn.click();
  74  | 
  75  |     await page.getByTestId(`link-row-${code}`).click();
  76  | 
  77  |     await expect(page.getByTestId('analytics-drawer')).toBeVisible();
  78  |     await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  79  |     await expect(page.getByTestId('drawer-original-url')).toContainText(targetUrl);
  80  |     await expect(page.getByTestId('drawer-chart')).toBeVisible();
  81  | 
  82  |     await page.getByTestId('close-drawer-btn').click();
  83  |     await expect(page.getByTestId('analytics-drawer')).toBeHidden();
  84  |   });
  85  | 
  86  |   test('should handle custom code collisions gracefully', async ({ page }) => {
  87  |     const code = 'collision-test';
  88  |     
  89  |     await page.getByTestId('destination-url-input').fill('https://first.com');
  90  |     await page.getByTestId('custom-code-summary').click();
  91  |     await page.getByTestId('custom-path-input').fill(code);
  92  |     
  93  |     const btn = page.getByTestId('execute-shorten-btn');
  94  |     await expect(btn).toBeEnabled();
  95  |     await btn.click();
  96  |     await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
  97  | 
  98  |     await page.getByTestId('destination-url-input').fill('https://second.com');
  99  |     await page.getByTestId('custom-path-input').fill(code);
  100 |     await btn.click();
  101 | 
  102 |     await expect(page.getByTestId('shortening-error')).toBeVisible();
  103 |     await expect(page.getByTestId('shortening-error')).toContainText(`The path '${code}' is already registered`);
  104 |   });
  105 | });
  106 | 
```