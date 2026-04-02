# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: ux-visuals.spec.ts >> UX & Visual Interactions >> should open analytics and verify chart structure
- Location: e2e/ux-visuals.spec.ts:52:7

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
      - button "Toggle Sidebar" [ref=e5] [cursor=pointer]:
        - generic [ref=e6]: menu_open
      - generic [ref=e7]: link.whoa.sh
      - generic [ref=e8]:
        - generic [ref=e9]: analytics
        - generic [ref=e10]: "Global Clicks:"
        - generic [ref=e12]: 1,001
    - generic [ref=e13]:
      - button "bar_chart" [ref=e14] [cursor=pointer]:
        - generic [ref=e15]: bar_chart
      - button "settings" [ref=e16] [cursor=pointer]:
        - generic [ref=e17]: settings
  - complementary [ref=e18]:
    - generic [ref=e19]:
      - heading "Link.Whoa" [level=2] [ref=e20]
      - paragraph [ref=e21]: Precision Shortening
    - navigation [ref=e22]:
      - link "grid_view Dashboard" [ref=e23] [cursor=pointer]:
        - /url: "#"
        - generic [ref=e24]: grid_view
        - generic [ref=e25]: Dashboard
    - button "add Shorten URL" [ref=e27] [cursor=pointer]:
      - generic [ref=e28]: add
      - generic [ref=e29]: Shorten URL
  - main [ref=e30]:
    - generic [ref=e31]:
      - generic [ref=e32]:
        - generic [ref=e33]:
          - heading "COMMAND CENTER" [level=1] [ref=e34]
          - paragraph [ref=e35]: Scale your presence with hyper-shortened redirects.
        - generic [ref=e36]:
          - generic [ref=e37]: System Status
          - generic [ref=e38]: Backend Active
      - generic [ref=e40]:
        - generic [ref=e42]:
          - generic [ref=e43]:
            - text: Destination URL
            - generic [ref=e44]:
              - textbox "Destination URL" [ref=e45]:
                - /placeholder: https://very-long-and-boring-url.com/analytics/dashboard
              - generic [ref=e46]: link
          - group [ref=e47]:
            - generic "tune Custom Short Code expand_more" [ref=e48] [cursor=pointer]:
              - generic [ref=e49]:
                - generic [ref=e50]: tune
                - generic [ref=e51]: Custom Short Code
              - generic [ref=e52]: expand_more
          - button "Execute Shorten" [disabled] [ref=e54]
        - generic [ref=e55]:
          - generic [ref=e56]:
            - heading "Efficiency Pulse" [level=3] [ref=e57]
            - paragraph [ref=e58]: 100% Uptime
          - generic [ref=e59]:
            - generic [ref=e60]: 21ms
            - generic [ref=e61]: Avg Latency
      - generic [ref=e63]:
        - generic [ref=e64]:
          - heading "Active Registry" [level=3] [ref=e65]
          - generic [ref=e66]:
            - button "filter_list" [ref=e67] [cursor=pointer]:
              - generic [ref=e68]: filter_list
            - button "download" [ref=e69] [cursor=pointer]:
              - generic [ref=e70]: download
        - table [ref=e72]:
          - rowgroup [ref=e73]:
            - row "Original URL Short URL Clicks Created Action" [ref=e74]:
              - columnheader "Original URL" [ref=e75]
              - columnheader "Short URL" [ref=e76]
              - columnheader "Clicks" [ref=e77]
              - columnheader "Created" [ref=e78]
              - columnheader "Action" [ref=e79]
          - rowgroup [ref=e80]:
            - row "oedziy https://example.com localhost:8844/oedziy content_copy 0 APR 02, 2026 more_vert" [ref=e81] [cursor=pointer]:
              - cell "oedziy https://example.com" [ref=e82]:
                - generic [ref=e83]:
                  - generic [ref=e84]: oedziy
                  - generic "https://example.com" [ref=e85]
              - cell "localhost:8844/oedziy content_copy" [ref=e86]:
                - generic [ref=e87]:
                  - code [ref=e88]: localhost:8844/oedziy
                  - generic [ref=e89]: content_copy
              - cell "0" [ref=e90]:
                - generic [ref=e92]: "0"
              - cell "APR 02, 2026" [ref=e93]
              - cell "more_vert" [ref=e94]:
                - button "more_vert" [ref=e95]:
                  - generic [ref=e96]: more_vert
            - row "dev005 https://www.postgresql.org/docs/ localhost:8844/dev005 content_copy 66 MAR 28, 2026 more_vert" [ref=e97] [cursor=pointer]:
              - cell "dev005 https://www.postgresql.org/docs/" [ref=e98]:
                - generic [ref=e99]:
                  - generic [ref=e100]: dev005
                  - generic "https://www.postgresql.org/docs/" [ref=e101]
              - cell "localhost:8844/dev005 content_copy" [ref=e102]:
                - generic [ref=e103]:
                  - code [ref=e104]: localhost:8844/dev005
                  - generic [ref=e105]: content_copy
              - cell "66" [ref=e106]:
                - generic [ref=e108]: "66"
              - cell "MAR 28, 2026" [ref=e109]
              - cell "more_vert" [ref=e110]:
                - button "more_vert" [ref=e111]:
                  - generic [ref=e112]: more_vert
            - row "dev007 https://news.ycombinator.com localhost:8844/dev007 content_copy 204 MAR 01, 2026 more_vert" [ref=e113] [cursor=pointer]:
              - cell "dev007 https://news.ycombinator.com" [ref=e114]:
                - generic [ref=e115]:
                  - generic [ref=e116]: dev007
                  - generic "https://news.ycombinator.com" [ref=e117]
              - cell "localhost:8844/dev007 content_copy" [ref=e118]:
                - generic [ref=e119]:
                  - code [ref=e120]: localhost:8844/dev007
                  - generic [ref=e121]: content_copy
              - cell "204" [ref=e122]:
                - generic [ref=e124]: "204"
              - cell "MAR 01, 2026" [ref=e125]
              - cell "more_vert" [ref=e126]:
                - button "more_vert" [ref=e127]:
                  - generic [ref=e128]: more_vert
            - row "dev006 https://docker.com localhost:8844/dev006 content_copy 89 FEB 23, 2026 more_vert" [ref=e129] [cursor=pointer]:
              - cell "dev006 https://docker.com" [ref=e130]:
                - generic [ref=e131]:
                  - generic [ref=e132]: dev006
                  - generic "https://docker.com" [ref=e133]
              - cell "localhost:8844/dev006 content_copy" [ref=e134]:
                - generic [ref=e135]:
                  - code [ref=e136]: localhost:8844/dev006
                  - generic [ref=e137]: content_copy
              - cell "89" [ref=e138]:
                - generic [ref=e140]: "89"
              - cell "FEB 23, 2026" [ref=e141]
              - cell "more_vert" [ref=e142]:
                - button "more_vert" [ref=e143]:
                  - generic [ref=e144]: more_vert
            - row "dev008 https://reddit.com localhost:8844/dev008 content_copy 89 FEB 22, 2026 more_vert" [ref=e145] [cursor=pointer]:
              - cell "dev008 https://reddit.com" [ref=e146]:
                - generic [ref=e147]:
                  - generic [ref=e148]: dev008
                  - generic "https://reddit.com" [ref=e149]
              - cell "localhost:8844/dev008 content_copy" [ref=e150]:
                - generic [ref=e151]:
                  - code [ref=e152]: localhost:8844/dev008
                  - generic [ref=e153]: content_copy
              - cell "89" [ref=e154]:
                - generic [ref=e156]: "89"
              - cell "FEB 22, 2026" [ref=e157]
              - cell "more_vert" [ref=e158]:
                - button "more_vert" [ref=e159]:
                  - generic [ref=e160]: more_vert
            - row "dev002 https://spring.io/projects/spring-boot localhost:8844/dev002 content_copy 126 FEB 14, 2026 more_vert" [ref=e161] [cursor=pointer]:
              - cell "dev002 https://spring.io/projects/spring-boot" [ref=e162]:
                - generic [ref=e163]:
                  - generic [ref=e164]: dev002
                  - generic "https://spring.io/projects/spring-boot" [ref=e165]
              - cell "localhost:8844/dev002 content_copy" [ref=e166]:
                - generic [ref=e167]:
                  - code [ref=e168]: localhost:8844/dev002
                  - generic [ref=e169]: content_copy
              - cell "126" [ref=e170]:
                - generic [ref=e172]: "126"
              - cell "FEB 14, 2026" [ref=e173]
              - cell "more_vert" [ref=e174]:
                - button "more_vert" [ref=e175]:
                  - generic [ref=e176]: more_vert
            - row "dev001 https://github.com/ohbus/link.whoa.sh localhost:8844/dev001 content_copy 169 FEB 11, 2026 more_vert" [ref=e177] [cursor=pointer]:
              - cell "dev001 https://github.com/ohbus/link.whoa.sh" [ref=e178]:
                - generic [ref=e179]:
                  - generic [ref=e180]: dev001
                  - generic "https://github.com/ohbus/link.whoa.sh" [ref=e181]
              - cell "localhost:8844/dev001 content_copy" [ref=e182]:
                - generic [ref=e183]:
                  - code [ref=e184]: localhost:8844/dev001
                  - generic [ref=e185]: content_copy
              - cell "169" [ref=e186]:
                - generic [ref=e188]: "169"
              - cell "FEB 11, 2026" [ref=e189]
              - cell "more_vert" [ref=e190]:
                - button "more_vert" [ref=e191]:
                  - generic [ref=e192]: more_vert
            - row "dev004 https://angular.dev/overview localhost:8844/dev004 content_copy 243 JAN 23, 2026 more_vert" [ref=e193] [cursor=pointer]:
              - cell "dev004 https://angular.dev/overview" [ref=e194]:
                - generic [ref=e195]:
                  - generic [ref=e196]: dev004
                  - generic "https://angular.dev/overview" [ref=e197]
              - cell "localhost:8844/dev004 content_copy" [ref=e198]:
                - generic [ref=e199]:
                  - code [ref=e200]: localhost:8844/dev004
                  - generic [ref=e201]: content_copy
              - cell "243" [ref=e202]:
                - generic [ref=e204]: "243"
              - cell "JAN 23, 2026" [ref=e205]
              - cell "more_vert" [ref=e206]:
                - button "more_vert" [ref=e207]:
                  - generic [ref=e208]: more_vert
            - row "dev003 https://kotlinlang.org/docs/home.html localhost:8844/dev003 content_copy 183 DEC 09, 2025 more_vert" [ref=e209] [cursor=pointer]:
              - cell "dev003 https://kotlinlang.org/docs/home.html" [ref=e210]:
                - generic [ref=e211]:
                  - generic [ref=e212]: dev003
                  - generic "https://kotlinlang.org/docs/home.html" [ref=e213]
              - cell "localhost:8844/dev003 content_copy" [ref=e214]:
                - generic [ref=e215]:
                  - code [ref=e216]: localhost:8844/dev003
                  - generic [ref=e217]: content_copy
              - cell "183" [ref=e218]:
                - generic [ref=e220]: "183"
              - cell "DEC 09, 2025" [ref=e221]
              - cell "more_vert" [ref=e222]:
                - button "more_vert" [ref=e223]:
                  - generic [ref=e224]: more_vert
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
  34 |     await page.getByTestId('destination-url-input').fill('https://example.com');
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
> 61 |     await expect(page.getByTestId('analytics-drawer')).toBeVisible();
     |                                                        ^ Error: expect(locator).toBeVisible() failed
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