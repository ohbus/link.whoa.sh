# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: shortening.spec.ts >> Shortening & Integrity >> should fail to shorten an invalid URL
- Location: e2e/shortening.spec.ts:12:7

# Error details

```
Error: expect(locator).toHaveClass(expected) failed

Locator: getByTestId('destination-url-input')
Expected pattern: /ng-invalid/
Received string:  "w-full bg-surface-container-lowest border-none ring-1 ring-outline-variant focus:ring-2 focus:ring-primary rounded-lg py-4 px-5 text-white placeholder-slate-600 transition-all font-body ng-valid ng-dirty ng-touched"
Timeout: 120000ms

Call log:
  - Expect "toHaveClass" with timeout 120000ms
  - waiting for getByTestId('destination-url-input')
    123 × locator resolved to <input type="text" id="destination-url" data-testid="destination-url-input" placeholder="https://very-long-and-boring-url.com/analytics/dashboard" class="w-full bg-surface-container-lowest border-none ring-1 ring-outline-variant focus:ring-2 focus:ring-primary rounded-lg py-4 px-5 text-white placeholder-slate-600 transition-all font-body ng-valid ng-dirty ng-touched"/>
        - unexpected value "w-full bg-surface-container-lowest border-none ring-1 ring-outline-variant focus:ring-2 focus:ring-primary rounded-lg py-4 px-5 text-white placeholder-slate-600 transition-all font-body ng-valid ng-dirty ng-touched"

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
                - text: not-a-url
              - text: link
          - group [ref=e35]:
            - generic "tuneCustom Short Code expand_more" [ref=e36]:
              - generic [ref=e37]: tuneCustom Short Code
              - text: expand_more
          - button "Execute Shorten" [ref=e39]
        - generic [ref=e40]:
          - generic [ref=e41]:
            - heading "Efficiency Pulse" [level=3] [ref=e42]
            - paragraph [ref=e43]: 100% Uptime
          - generic [ref=e44]: 15msAvg Latency
      - generic [ref=e45]:
        - generic [ref=e46]:
          - heading "Active Registry" [level=3] [ref=e47]
          - generic [ref=e48]:
            - button "filter_list" [ref=e49]
            - button "download" [ref=e50]
        - table [ref=e52]:
          - rowgroup [ref=e53]:
            - row "Original URL Short URL Clicks Created Action" [ref=e54]:
              - columnheader "Original URL" [ref=e55]
              - columnheader "Short URL" [ref=e56]
              - columnheader "Clicks" [ref=e57]
              - columnheader "Created" [ref=e58]
              - columnheader "Action" [ref=e59]
          - rowgroup [ref=e60]:
            - row "search_off Registry Empty No short links found in this browser's local storage." [ref=e61]:
              - cell "search_off Registry Empty No short links found in this browser's local storage." [ref=e62]:
                - generic [ref=e63]:
                  - generic [ref=e64]: search_off
                  - generic [ref=e65]:
                    - heading "Registry Empty" [level=4] [ref=e66]
                    - paragraph [ref=e67]: No short links found in this browser's local storage.
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | test.describe('Shortening & Integrity', () => {
  4  |   test.beforeEach(async ({ page, request }) => {
  5  |     await request.post('http://127.0.0.1:8844/api/testing/reset');
  6  |     await page.goto('/#/');
  7  |     await page.evaluate(async () => { await indexedDB.deleteDatabase('WhoaDatabase'); });
  8  |     await page.reload();
  9  |     await expect(page.getByTestId('app-logo')).toBeVisible();
  10 |   });
  11 | 
  12 |   test('should fail to shorten an invalid URL', async ({ page }) => {
  13 |     const input = page.getByTestId('destination-url-input');
  14 |     
  15 |     // Fill and trigger validation
  16 |     await input.fill('not-a-url');
  17 |     await input.blur();
  18 |     
  19 |     // Verify Angular validation state via standard class
> 20 |     await expect(input).toHaveClass(/ng-invalid/);
     |                         ^ Error: expect(locator).toHaveClass(expected) failed
  21 |     
  22 |     // Verify UI error message appears
  23 |     await expect(page.getByTestId('url-validation-error')).toBeVisible();
  24 |     
  25 |     // Button must be disabled
  26 |     const btn = page.getByTestId('execute-shorten-btn');
  27 |     await expect(btn).toBeDisabled();
  28 |   });
  29 | 
  30 |   test('should handle custom code collisions with conflict styling', async ({ page }) => {
  31 |     const code = 'fixed';
  32 |     await page.getByTestId('destination-url-input').fill('https://google.com');
  33 |     await page.getByTestId('custom-code-summary').click();
  34 |     await page.getByTestId('custom-path-input').fill(code);
  35 |     
  36 |     const btn = page.getByTestId('execute-shorten-btn');
  37 |     await expect(btn).toBeEnabled();
  38 |     await btn.click();
  39 |     await expect(btn).toContainText('Execute');
  40 |     
  41 |     // Wait for the first one to appear
  42 |     await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
  43 | 
  44 |     // Try to create another with the same code
  45 |     await page.getByTestId('destination-url-input').fill('https://bing.com');
  46 |     await page.getByTestId('custom-path-input').fill(code);
  47 |     
  48 |     // Form should still be valid
  49 |     await expect(btn).toBeEnabled();
  50 |     await btn.click();
  51 | 
  52 |     // Verify Error Messaging (from backend)
  53 |     const errorEl = page.getByTestId('shortening-error');
  54 |     await expect(errorEl).toBeVisible();
  55 |     await expect(errorEl).toContainText('already registered');
  56 |     await expect(errorEl).toHaveClass(/text-error/);
  57 |   });
  58 | 
  59 |   test('should auto-focus destination input on sidebar button click', async ({ page }) => {
  60 |     await page.getByTestId('sidebar-shorten-btn').click();
  61 |     const input = page.getByTestId('destination-url-input');
  62 |     await expect(input).toBeFocused();
  63 |   });
  64 | });
  65 | 
```