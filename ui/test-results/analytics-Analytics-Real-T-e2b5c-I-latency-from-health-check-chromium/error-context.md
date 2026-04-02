# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: analytics.spec.ts >> Analytics & Real-Time Pulse >> should display live API latency from health-check
- Location: e2e/analytics.spec.ts:25:7

# Error details

```
Error: expect(locator).not.toContainText(expected) failed

Locator: getByTestId('latency-value')
Expected substring: not "0ms"
Received string: "0ms"
Timeout: 5000ms

Call log:
  - Expect "not toContainText" with timeout 5000ms
  - waiting for getByTestId('latency-value')
    9 × locator resolved to <span data-testid="latency-value" class="text-4xl font-black text-secondary tracking-tighter">0ms</span>
      - unexpected value "0ms"

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
        - generic [ref=e12]: 1,137
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
          - generic [ref=e38]: Backend Offline
      - generic [ref=e40]:
        - generic [ref=e42]:
          - generic [ref=e43]:
            - text: Destination URL
            - generic [ref=e44]:
              - textbox "Destination URL" [disabled] [ref=e45]:
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
            - paragraph [ref=e58]: 0% Uptime
          - generic [ref=e59]:
            - generic [ref=e60]: 0ms
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
            - row "dev008 https://reddit.com localhost:8844/dev008 content_copy 238 MAR 17, 2026 more_vert" [ref=e81] [cursor=pointer]:
              - cell "dev008 https://reddit.com" [ref=e82]:
                - generic [ref=e83]:
                  - generic [ref=e84]: dev008
                  - generic "https://reddit.com" [ref=e85]
              - cell "localhost:8844/dev008 content_copy" [ref=e86]:
                - generic [ref=e87]:
                  - code [ref=e88]: localhost:8844/dev008
                  - generic [ref=e89]: content_copy
              - cell "238" [ref=e90]:
                - generic [ref=e92]: "238"
              - cell "MAR 17, 2026" [ref=e93]
              - cell "more_vert" [ref=e94]:
                - button "more_vert" [ref=e95]:
                  - generic [ref=e96]: more_vert
            - row "dev006 https://docker.com localhost:8844/dev006 content_copy 170 MAR 07, 2026 more_vert" [ref=e97] [cursor=pointer]:
              - cell "dev006 https://docker.com" [ref=e98]:
                - generic [ref=e99]:
                  - generic [ref=e100]: dev006
                  - generic "https://docker.com" [ref=e101]
              - cell "localhost:8844/dev006 content_copy" [ref=e102]:
                - generic [ref=e103]:
                  - code [ref=e104]: localhost:8844/dev006
                  - generic [ref=e105]: content_copy
              - cell "170" [ref=e106]:
                - generic [ref=e108]: "170"
              - cell "MAR 07, 2026" [ref=e109]
              - cell "more_vert" [ref=e110]:
                - button "more_vert" [ref=e111]:
                  - generic [ref=e112]: more_vert
            - row "dev002 https://spring.io/projects/spring-boot localhost:8844/dev002 content_copy 97 MAR 02, 2026 more_vert" [ref=e113] [cursor=pointer]:
              - cell "dev002 https://spring.io/projects/spring-boot" [ref=e114]:
                - generic [ref=e115]:
                  - generic [ref=e116]: dev002
                  - generic "https://spring.io/projects/spring-boot" [ref=e117]
              - cell "localhost:8844/dev002 content_copy" [ref=e118]:
                - generic [ref=e119]:
                  - code [ref=e120]: localhost:8844/dev002
                  - generic [ref=e121]: content_copy
              - cell "97" [ref=e122]:
                - generic [ref=e124]: "97"
              - cell "MAR 02, 2026" [ref=e125]
              - cell "more_vert" [ref=e126]:
                - button "more_vert" [ref=e127]:
                  - generic [ref=e128]: more_vert
            - row "dev005 https://www.postgresql.org/docs/ localhost:8844/dev005 content_copy 61 FEB 24, 2026 more_vert" [ref=e129] [cursor=pointer]:
              - cell "dev005 https://www.postgresql.org/docs/" [ref=e130]:
                - generic [ref=e131]:
                  - generic [ref=e132]: dev005
                  - generic "https://www.postgresql.org/docs/" [ref=e133]
              - cell "localhost:8844/dev005 content_copy" [ref=e134]:
                - generic [ref=e135]:
                  - code [ref=e136]: localhost:8844/dev005
                  - generic [ref=e137]: content_copy
              - cell "61" [ref=e138]:
                - generic [ref=e140]: "61"
              - cell "FEB 24, 2026" [ref=e141]
              - cell "more_vert" [ref=e142]:
                - button "more_vert" [ref=e143]:
                  - generic [ref=e144]: more_vert
            - row "dev003 https://kotlinlang.org/docs/home.html localhost:8844/dev003 content_copy 215 FEB 05, 2026 more_vert" [ref=e145] [cursor=pointer]:
              - cell "dev003 https://kotlinlang.org/docs/home.html" [ref=e146]:
                - generic [ref=e147]:
                  - generic [ref=e148]: dev003
                  - generic "https://kotlinlang.org/docs/home.html" [ref=e149]
              - cell "localhost:8844/dev003 content_copy" [ref=e150]:
                - generic [ref=e151]:
                  - code [ref=e152]: localhost:8844/dev003
                  - generic [ref=e153]: content_copy
              - cell "215" [ref=e154]:
                - generic [ref=e156]: "215"
              - cell "FEB 05, 2026" [ref=e157]
              - cell "more_vert" [ref=e158]:
                - button "more_vert" [ref=e159]:
                  - generic [ref=e160]: more_vert
            - row "dev004 https://angular.dev/overview localhost:8844/dev004 content_copy 124 JAN 18, 2026 more_vert" [ref=e161] [cursor=pointer]:
              - cell "dev004 https://angular.dev/overview" [ref=e162]:
                - generic [ref=e163]:
                  - generic [ref=e164]: dev004
                  - generic "https://angular.dev/overview" [ref=e165]
              - cell "localhost:8844/dev004 content_copy" [ref=e166]:
                - generic [ref=e167]:
                  - code [ref=e168]: localhost:8844/dev004
                  - generic [ref=e169]: content_copy
              - cell "124" [ref=e170]:
                - generic [ref=e172]: "124"
              - cell "JAN 18, 2026" [ref=e173]
              - cell "more_vert" [ref=e174]:
                - button "more_vert" [ref=e175]:
                  - generic [ref=e176]: more_vert
            - row "dev001 https://github.com/ohbus/link.whoa.sh localhost:8844/dev001 content_copy 111 DEC 17, 2025 more_vert" [ref=e177] [cursor=pointer]:
              - cell "dev001 https://github.com/ohbus/link.whoa.sh" [ref=e178]:
                - generic [ref=e179]:
                  - generic [ref=e180]: dev001
                  - generic "https://github.com/ohbus/link.whoa.sh" [ref=e181]
              - cell "localhost:8844/dev001 content_copy" [ref=e182]:
                - generic [ref=e183]:
                  - code [ref=e184]: localhost:8844/dev001
                  - generic [ref=e185]: content_copy
              - cell "111" [ref=e186]:
                - generic [ref=e188]: "111"
              - cell "DEC 17, 2025" [ref=e189]
              - cell "more_vert" [ref=e190]:
                - button "more_vert" [ref=e191]:
                  - generic [ref=e192]: more_vert
            - row "dev007 https://news.ycombinator.com localhost:8844/dev007 content_copy 121 DEC 09, 2025 more_vert" [ref=e193] [cursor=pointer]:
              - cell "dev007 https://news.ycombinator.com" [ref=e194]:
                - generic [ref=e195]:
                  - generic [ref=e196]: dev007
                  - generic "https://news.ycombinator.com" [ref=e197]
              - cell "localhost:8844/dev007 content_copy" [ref=e198]:
                - generic [ref=e199]:
                  - code [ref=e200]: localhost:8844/dev007
                  - generic [ref=e201]: content_copy
              - cell "121" [ref=e202]:
                - generic [ref=e204]: "121"
              - cell "DEC 09, 2025" [ref=e205]
              - cell "more_vert" [ref=e206]:
                - button "more_vert" [ref=e207]:
                  - generic [ref=e208]: more_vert
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
> 27 |     await expect(page.getByTestId('latency-value')).not.toContainText('0ms');
     |                                                         ^ Error: expect(locator).not.toContainText(expected) failed
  28 |     await expect(page.getByTestId('latency-value')).toContainText('ms');
  29 |   });
  30 | 
  31 |   test('should handle backend connectivity failure', async ({ page, context }) => {
  32 |     // Mock health check to return error
  33 |     await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
  34 |     
  35 |     // Wait for next heartbeat
  36 |     await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 15000 });
  37 |     await expect(page.getByTestId('system-status')).toHaveClass(/text-error/);
  38 |   });
  39 | });
  40 | 
```