# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: lifecycle.spec.ts >> Whoa Link Shortener Full Lifecycle >> should show detailed analytics drawer with time-series chart
- Location: e2e/lifecycle.spec.ts:73:7

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: getByTestId('analytics-drawer')
Expected: visible
Timeout: 10000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 10000ms
  - waiting for getByTestId('analytics-drawer')

```

# Page snapshot

```yaml
- generic [ref=e2]:
  - banner [ref=e3]:
    - generic [ref=e4]:
      - button "Toggle Sidebar" [ref=e5]: menu_open
      - generic [ref=e6]: link.whoa.sh
      - generic [ref=e7]:
        - text: "analyticsGlobal Clicks:"
        - generic [ref=e9]: "0"
    - generic [ref=e10]:
      - button "bar_chart" [ref=e11]
      - button "settings" [ref=e12]
  - complementary [ref=e13]:
    - generic [ref=e14]:
      - heading "Link.Whoa" [level=2] [ref=e15]
      - paragraph [ref=e16]: Precision Shortening
    - navigation [ref=e17]:
      - link "grid_viewDashboard" [ref=e18] [cursor=pointer]:
        - /url: "#"
    - button "addShorten URL" [ref=e20]
  - main [ref=e21]:
    - generic [ref=e22]:
      - generic [ref=e23]:
        - generic [ref=e24]:
          - heading "COMMAND CENTER" [level=1] [ref=e25]
          - paragraph [ref=e26]: Scale your presence with hyper-shortened redirects.
        - generic [ref=e27]:
          - text: System Status
          - generic [ref=e28]: Backend Active
      - generic [ref=e29]:
        - generic [ref=e31]:
          - generic [ref=e32]:
            - text: Destination URL
            - generic [ref=e33]:
              - textbox "Destination URL" [ref=e34]:
                - /placeholder: https://very-long-and-boring-url.com/analytics/dashboard
              - text: link
          - group [ref=e35]:
            - generic "tuneCustom Short Code expand_more" [ref=e36]:
              - generic [ref=e37]: tuneCustom Short Code
              - text: expand_more
            - generic [ref=e39]:
              - text: whoa.sh /
              - textbox "my-custom-path" [ref=e40]
          - button "Execute Shorten" [disabled] [ref=e42]
        - generic [ref=e43]:
          - generic [ref=e44]:
            - heading "Efficiency Pulse" [level=3] [ref=e45]
            - paragraph [ref=e46]: 100% Uptime
          - generic [ref=e47]: 11msAvg Latency
      - generic [ref=e48]:
        - generic [ref=e49]:
          - heading "Active Registry" [level=3] [ref=e50]
          - generic [ref=e51]:
            - button "filter_list" [ref=e52]
            - button "download" [ref=e53]
        - table [ref=e55]:
          - rowgroup [ref=e56]:
            - row "Original URL Short URL Clicks Created Action" [ref=e57]:
              - columnheader "Original URL" [ref=e58]
              - columnheader "Short URL" [ref=e59]
              - columnheader "Clicks" [ref=e60]
              - columnheader "Created" [ref=e61]
              - columnheader "Action" [ref=e62]
          - rowgroup [ref=e63]:
            - row "ktesthttps://kotlinlang.org localhost:8844/ktestcontent_copy 0 APR 03, 2026 more_vert" [ref=e64]:
              - cell "ktesthttps://kotlinlang.org" [ref=e65]:
                - generic [ref=e66]: ktesthttps://kotlinlang.org
              - cell "localhost:8844/ktestcontent_copy" [ref=e67]:
                - generic [ref=e68]:
                  - code [ref=e69]: localhost:8844/ktest
                  - text: content_copy
              - cell "0" [ref=e70]:
                - generic [ref=e72]: "0"
              - cell "APR 03, 2026" [ref=e73]
              - cell "more_vert" [ref=e74]:
                - button "more_vert" [ref=e75]
```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | 
  3   | test.describe('Whoa Link Shortener Full Lifecycle', () => {
  4   |   
  5   |   test.beforeEach(async ({ page, request }) => {
  6   |     // 1. Reset Backend State
  7   |     const resetResponse = await request.post('http://127.0.0.1:8844/api/testing/reset');
  8   |     expect(resetResponse.ok()).toBeTruthy();
  9   | 
  10  |     // 2. Clear Browser State
  11  |     await page.goto('/#/');
  12  |     await page.evaluate(async () => {
  13  |       await indexedDB.deleteDatabase('WhoaDatabase');
  14  |       // @ts-ignore
  15  |       window.SyncService_skipSync = true;
  16  |     });
  17  |     
  18  |     // Navigate again to ensure the flag is set BEFORE the app starts
  19  |     await page.goto('/#/');
  20  |     await expect(page.getByTestId('app-logo')).toBeVisible();
  21  |   });
  22  | 
  23  |   test('should shorten a URL and verify its presence in the registry', async ({ page }) => {
  24  |     const targetUrl = 'https://playwright.dev';
  25  |     const customCode = 'pw' + Math.floor(Math.random() * 1000000);
  26  | 
  27  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  28  |     await page.getByTestId('custom-code-summary').click();
  29  |     await page.getByTestId('custom-path-input').fill(customCode);
  30  | 
  31  |     const btn = page.getByTestId('execute-shorten-btn');
  32  |     await expect(btn).toBeEnabled();
  33  |     await btn.click();
  34  | 
  35  |     await expect(page.getByTestId('toast-notification')).toBeVisible();
  36  |     const row = page.getByTestId(`link-row-${customCode}`);
  37  |     await expect(row).toBeVisible();
  38  |   });
  39  | 
  40  |   test('should track analytics when a short link is visited', async ({ page, context }) => {
  41  |     const targetUrl = 'https://github.com';
  42  |     const customCode = 'v' + Math.floor(Math.random() * 1000000);
  43  | 
  44  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  45  |     await page.getByTestId('custom-code-summary').click();
  46  |     await page.getByTestId('custom-path-input').fill(customCode);
  47  |     
  48  |     const btn = page.getByTestId('execute-shorten-btn');
  49  |     await expect(btn).toBeEnabled();
  50  |     await btn.click();
  51  |     
  52  |     const row = page.getByTestId(`link-row-${customCode}`);
  53  |     await expect(row).toBeVisible();
  54  | 
  55  |     const redirectUrl = `http://127.0.0.1:8844/${customCode}`;
  56  |     const visitPage = await context.newPage();
  57  |     await visitPage.goto(redirectUrl);
  58  |     await visitPage.close();
  59  | 
  60  |     // Manually trigger sync since background sync is disabled
  61  |     await page.evaluate(async () => {
  62  |       // @ts-ignore
  63  |       const app = document.querySelector('app-root');
  64  |       // Actually we can just wait for the component to poll or trigger it
  65  |       // For now, let's just re-enable it for a second
  66  |       // @ts-ignore
  67  |       window.SyncService_skipSync = false;
  68  |     });
  69  | 
  70  |     await expect(page.getByTestId(`link-clicks-${customCode}`)).toContainText('1', { timeout: 20000 });
  71  |   });
  72  | 
  73  |   test('should show detailed analytics drawer with time-series chart', async ({ page }) => {
  74  |     const targetUrl = 'https://kotlinlang.org';
  75  |     const code = 'ktest';
  76  | 
  77  |     await page.getByTestId('destination-url-input').fill(targetUrl);
  78  |     await page.getByTestId('custom-code-summary').click();
  79  |     await page.getByTestId('custom-path-input').fill(code);
  80  |     
  81  |     const btn = page.getByTestId('execute-shorten-btn');
  82  |     await expect(btn).toBeEnabled();
  83  |     await btn.click();
  84  | 
  85  |     const row = page.getByTestId(`link-row-${code}`);
  86  |     await row.scrollIntoViewIfNeeded();
  87  |     await expect(row).toBeVisible();
  88  |     
  89  |     await row.click();
  90  | 
  91  |     const drawer = page.getByTestId('analytics-drawer');
> 92  |     await expect(drawer).toBeVisible({ timeout: 10000 });
      |                          ^ Error: expect(locator).toBeVisible() failed
  93  |     await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  94  |     await expect(page.getByTestId('drawer-chart')).toBeVisible();
  95  |   });
  96  | 
  97  |   test('should handle custom code collisions gracefully', async ({ page }) => {
  98  |     const code = 'coll';
  99  |     
  100 |     await page.getByTestId('destination-url-input').fill('https://first.com');
  101 |     await page.getByTestId('custom-code-summary').click();
  102 |     await page.getByTestId('custom-path-input').fill(code);
  103 |     
  104 |     const btn = page.getByTestId('execute-shorten-btn');
  105 |     await expect(btn).toBeEnabled();
  106 |     await btn.click();
  107 |     await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
  108 | 
  109 |     await page.getByTestId('destination-url-input').fill('https://second.com');
  110 |     await page.getByTestId('custom-path-input').fill(code);
  111 |     await btn.click();
  112 | 
  113 |     await expect(page.getByTestId('shortening-error')).toBeVisible();
  114 |     await expect(page.getByTestId('shortening-error')).toContainText(`The path '${code}' is already registered`);
  115 |   });
  116 | });
  117 | 
```