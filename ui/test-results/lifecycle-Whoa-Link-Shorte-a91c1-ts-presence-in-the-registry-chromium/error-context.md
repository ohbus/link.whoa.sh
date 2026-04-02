# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: lifecycle.spec.ts >> Whoa Link Shortener Full Lifecycle >> should shorten a URL and verify its presence in the registry
- Location: e2e/lifecycle.spec.ts:22:7

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
  7   |     const resetResponse = await request.post('/api/testing/reset');
> 8   |     expect(resetResponse.ok()).toBeTruthy();
      |                                ^ Error: expect(received).toBeTruthy()
  9   | 
  10  |     // 2. Clear Browser State (IndexedDB)
  11  |     await page.goto('/#/');
  12  |     await page.evaluate(async () => {
  13  |       // @ts-ignore - Dexie might not be globally exposed, but we can target the DB name directly
  14  |       await indexedDB.deleteDatabase('WhoaDatabase');
  15  |     });
  16  |     
  17  |     // 3. Navigate back to ensure a fresh session
  18  |     await page.reload();
  19  |     await expect(page.getByTestId('app-logo')).toBeVisible();
  20  |   });
  21  | 
  22  |   test('should shorten a URL and verify its presence in the registry', async ({ page }) => {
  23  |     const targetUrl = 'https://playwright.dev';
  24  |     const customCode = 'pw-test-' + Date.now();
  25  | 
  26  |     // Fill the shortening form
  27  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  28  |     
  29  |     // Expand custom code section
  30  |     await page.getByTestId('custom-code-summary').click();
  31  |     await page.getByTestId('custom-path-input').fill(customCode);
  32  | 
  33  |     // Execute shortening
  34  |     await page.getByTestId('execute-shorten-btn').click();
  35  | 
  36  |     // Verify Success Notification
  37  |     await expect(page.getByTestId('toast-notification')).toBeVisible();
  38  |     await expect(page.getByTestId('toast-notification')).toContainText('Short URL generated successfully');
  39  | 
  40  |     // Verify presence in registry table
  41  |     const row = page.getByTestId(`link-row-${customCode}`);
  42  |     await expect(row).toBeVisible();
  43  |     await expect(row.getByTestId(`link-code-${customCode}`)).toContainText(customCode);
  44  |   });
  45  | 
  46  |   test('should track analytics when a short link is visited', async ({ page, context }) => {
  47  |     const targetUrl = 'https://github.com/ohbus/link.whoa.sh';
  48  |     const customCode = 'link-whoa-' + Date.now();
  49  | 
  50  |     // 1. Create the link
  51  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  52  |     await page.getByTestId('custom-code-summary').click();
  53  |     await page.getByTestId('custom-path-input').fill(customCode);
  54  |     await page.getByTestId('execute-shorten-btn').click();
  55  |     await expect(page.getByTestId(`link-row-${customCode}`)).toBeVisible();
  56  | 
  57  |     // 2. Perform a real redirect in a new tab
  58  |     const [newPage] = await Promise.all([
  59  |       context.waitForEvent('page'),
  60  |       page.getByTestId(`copy-link-${customCode}`).click() // Not actually clicking copy, just using the row trigger
  61  |     ]);
  62  |     
  63  |     // Actually, let's navigate to the redirect URL manually to be sure
  64  |     const redirectUrl = `http://127.0.0.1:8844/${customCode}`;
  65  |     const visitPage = await context.newPage();
  66  |     await visitPage.goto(redirectUrl);
  67  |     await visitPage.close();
  68  | 
  69  |     // 3. Verify analytics sync in the UI
  70  |     // Wait for the background SyncService to pulse (delta sync)
  71  |     await expect(page.getByTestId(`link-clicks-${customCode}`)).toContainText('1', { timeout: 15000 });
  72  | 
  73  |     // 4. Verify Global Clicks increment
  74  |     await expect(page.getByTestId('global-clicks-value')).not.toContainText('0');
  75  |   });
  76  | 
  77  |   test('should show detailed analytics drawer with time-series chart', async ({ page }) => {
  78  |     const targetUrl = 'https://kotlinlang.org';
  79  |     const code = 'kt-test';
  80  | 
  81  |     // Create link
  82  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  83  |     await page.getByTestId('custom-code-summary').click();
  84  |     await page.getByTestId('custom-path-input').fill(code);
  85  |     await page.getByTestId('execute-shorten-btn').click();
  86  | 
  87  |     // Open drawer
  88  |     await page.getByTestId(`link-row-${code}`).click();
  89  | 
  90  |     // Verify Drawer content
  91  |     await expect(page.getByTestId('analytics-drawer')).toBeVisible();
  92  |     await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  93  |     await expect(page.getByTestId('drawer-original-url')).toContainText(targetUrl);
  94  |     
  95  |     // Verify Chart visibility
  96  |     await expect(page.getByTestId('drawer-chart')).toBeVisible();
  97  | 
  98  |     // Close drawer
  99  |     await page.getByTestId('close-drawer-btn').click();
  100 |     await expect(page.getByTestId('analytics-drawer')).toBeHidden();
  101 |   });
  102 | 
  103 |   test('should handle custom code collisions gracefully', async ({ page }) => {
  104 |     const code = 'collision-test';
  105 |     
  106 |     // 1. Create first link
  107 |     await page.getByTestId('destination-url-input').fill('https://first.com');
  108 |     await page.getByTestId('custom-code-summary').click();
```