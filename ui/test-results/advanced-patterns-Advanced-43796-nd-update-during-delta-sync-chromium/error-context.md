# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: advanced-patterns.spec.ts >> Advanced Data Patterns & Monkey Testing >> should show correct counts after backend update during delta sync
- Location: e2e/advanced-patterns.spec.ts:71:7

# Error details

```
Error: expect(locator).toContainText(expected) failed

Locator: getByTestId('link-clicks-live-update')
Expected substring: "0"
Timeout: 10000ms
Error: element(s) not found

Call log:
  - Expect "toContainText" with timeout 10000ms
  - waiting for getByTestId('link-clicks-live-update')

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
        - generic [ref=e12]: "934"
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
                - text: https://example.com
              - generic [ref=e46]: link
          - group [ref=e47]:
            - generic "tune Custom Short Code expand_more" [ref=e48] [cursor=pointer]:
              - generic [ref=e49]:
                - generic [ref=e50]: tune
                - generic [ref=e51]: Custom Short Code
              - generic [ref=e52]: expand_more
            - generic [ref=e53]:
              - generic [ref=e54]:
                - generic [ref=e55]: whoa.sh /
                - textbox "my-custom-path" [ref=e56]: live-update
              - generic [ref=e57]:
                - generic [ref=e58]: error
                - text: An unexpected system error occurred.
          - button "Execute Shorten" [ref=e60] [cursor=pointer]
        - generic [ref=e61]:
          - generic [ref=e62]:
            - heading "Efficiency Pulse" [level=3] [ref=e63]
            - paragraph [ref=e64]: 100% Uptime
          - generic [ref=e65]:
            - generic [ref=e66]: 21ms
            - generic [ref=e67]: Avg Latency
      - generic [ref=e69]:
        - generic [ref=e70]:
          - heading "Active Registry" [level=3] [ref=e71]
          - generic [ref=e72]:
            - button "filter_list" [ref=e73] [cursor=pointer]:
              - generic [ref=e74]: filter_list
            - button "download" [ref=e75] [cursor=pointer]:
              - generic [ref=e76]: download
        - table [ref=e78]:
          - rowgroup [ref=e79]:
            - row "Original URL Short URL Clicks Created Action" [ref=e80]:
              - columnheader "Original URL" [ref=e81]
              - columnheader "Short URL" [ref=e82]
              - columnheader "Clicks" [ref=e83]
              - columnheader "Created" [ref=e84]
              - columnheader "Action" [ref=e85]
          - rowgroup [ref=e86]:
            - row "dev001 https://github.com/ohbus/link.whoa.sh localhost:8844/dev001 content_copy 150 MAR 31, 2026 more_vert" [ref=e87] [cursor=pointer]:
              - cell "dev001 https://github.com/ohbus/link.whoa.sh" [ref=e88]:
                - generic [ref=e89]:
                  - generic [ref=e90]: dev001
                  - generic "https://github.com/ohbus/link.whoa.sh" [ref=e91]
              - cell "localhost:8844/dev001 content_copy" [ref=e92]:
                - generic [ref=e93]:
                  - code [ref=e94]: localhost:8844/dev001
                  - generic [ref=e95]: content_copy
              - cell "150" [ref=e96]:
                - generic [ref=e98]: "150"
              - cell "MAR 31, 2026" [ref=e99]
              - cell "more_vert" [ref=e100]:
                - button "more_vert" [ref=e101]:
                  - generic [ref=e102]: more_vert
            - row "dev006 https://docker.com localhost:8844/dev006 content_copy 154 MAR 28, 2026 more_vert" [ref=e103] [cursor=pointer]:
              - cell "dev006 https://docker.com" [ref=e104]:
                - generic [ref=e105]:
                  - generic [ref=e106]: dev006
                  - generic "https://docker.com" [ref=e107]
              - cell "localhost:8844/dev006 content_copy" [ref=e108]:
                - generic [ref=e109]:
                  - code [ref=e110]: localhost:8844/dev006
                  - generic [ref=e111]: content_copy
              - cell "154" [ref=e112]:
                - generic [ref=e114]: "154"
              - cell "MAR 28, 2026" [ref=e115]
              - cell "more_vert" [ref=e116]:
                - button "more_vert" [ref=e117]:
                  - generic [ref=e118]: more_vert
            - row "dev007 https://news.ycombinator.com localhost:8844/dev007 content_copy 236 MAR 22, 2026 more_vert" [ref=e119] [cursor=pointer]:
              - cell "dev007 https://news.ycombinator.com" [ref=e120]:
                - generic [ref=e121]:
                  - generic [ref=e122]: dev007
                  - generic "https://news.ycombinator.com" [ref=e123]
              - cell "localhost:8844/dev007 content_copy" [ref=e124]:
                - generic [ref=e125]:
                  - code [ref=e126]: localhost:8844/dev007
                  - generic [ref=e127]: content_copy
              - cell "236" [ref=e128]:
                - generic [ref=e130]: "236"
              - cell "MAR 22, 2026" [ref=e131]
              - cell "more_vert" [ref=e132]:
                - button "more_vert" [ref=e133]:
                  - generic [ref=e134]: more_vert
            - row "dev008 https://reddit.com localhost:8844/dev008 content_copy 119 FEB 21, 2026 more_vert" [ref=e135] [cursor=pointer]:
              - cell "dev008 https://reddit.com" [ref=e136]:
                - generic [ref=e137]:
                  - generic [ref=e138]: dev008
                  - generic "https://reddit.com" [ref=e139]
              - cell "localhost:8844/dev008 content_copy" [ref=e140]:
                - generic [ref=e141]:
                  - code [ref=e142]: localhost:8844/dev008
                  - generic [ref=e143]: content_copy
              - cell "119" [ref=e144]:
                - generic [ref=e146]: "119"
              - cell "FEB 21, 2026" [ref=e147]
              - cell "more_vert" [ref=e148]:
                - button "more_vert" [ref=e149]:
                  - generic [ref=e150]: more_vert
            - row "dev002 https://spring.io/projects/spring-boot localhost:8844/dev002 content_copy 135 FEB 02, 2026 more_vert" [ref=e151] [cursor=pointer]:
              - cell "dev002 https://spring.io/projects/spring-boot" [ref=e152]:
                - generic [ref=e153]:
                  - generic [ref=e154]: dev002
                  - generic "https://spring.io/projects/spring-boot" [ref=e155]
              - cell "localhost:8844/dev002 content_copy" [ref=e156]:
                - generic [ref=e157]:
                  - code [ref=e158]: localhost:8844/dev002
                  - generic [ref=e159]: content_copy
              - cell "135" [ref=e160]:
                - generic [ref=e162]: "135"
              - cell "FEB 02, 2026" [ref=e163]
              - cell "more_vert" [ref=e164]:
                - button "more_vert" [ref=e165]:
                  - generic [ref=e166]: more_vert
            - row "dev005 https://www.postgresql.org/docs/ localhost:8844/dev005 content_copy 82 JAN 27, 2026 more_vert" [ref=e167] [cursor=pointer]:
              - cell "dev005 https://www.postgresql.org/docs/" [ref=e168]:
                - generic [ref=e169]:
                  - generic [ref=e170]: dev005
                  - generic "https://www.postgresql.org/docs/" [ref=e171]
              - cell "localhost:8844/dev005 content_copy" [ref=e172]:
                - generic [ref=e173]:
                  - code [ref=e174]: localhost:8844/dev005
                  - generic [ref=e175]: content_copy
              - cell "82" [ref=e176]:
                - generic [ref=e178]: "82"
              - cell "JAN 27, 2026" [ref=e179]
              - cell "more_vert" [ref=e180]:
                - button "more_vert" [ref=e181]:
                  - generic [ref=e182]: more_vert
            - row "dev004 https://angular.dev/overview localhost:8844/dev004 content_copy 136 JAN 08, 2026 more_vert" [ref=e183] [cursor=pointer]:
              - cell "dev004 https://angular.dev/overview" [ref=e184]:
                - generic [ref=e185]:
                  - generic [ref=e186]: dev004
                  - generic "https://angular.dev/overview" [ref=e187]
              - cell "localhost:8844/dev004 content_copy" [ref=e188]:
                - generic [ref=e189]:
                  - code [ref=e190]: localhost:8844/dev004
                  - generic [ref=e191]: content_copy
              - cell "136" [ref=e192]:
                - generic [ref=e194]: "136"
              - cell "JAN 08, 2026" [ref=e195]
              - cell "more_vert" [ref=e196]:
                - button "more_vert" [ref=e197]:
                  - generic [ref=e198]: more_vert
            - row "dev003 https://kotlinlang.org/docs/home.html localhost:8844/dev003 content_copy 137 DEC 21, 2025 more_vert" [ref=e199] [cursor=pointer]:
              - cell "dev003 https://kotlinlang.org/docs/home.html" [ref=e200]:
                - generic [ref=e201]:
                  - generic [ref=e202]: dev003
                  - generic "https://kotlinlang.org/docs/home.html" [ref=e203]
              - cell "localhost:8844/dev003 content_copy" [ref=e204]:
                - generic [ref=e205]:
                  - code [ref=e206]: localhost:8844/dev003
                  - generic [ref=e207]: content_copy
              - cell "137" [ref=e208]:
                - generic [ref=e210]: "137"
              - cell "DEC 21, 2025" [ref=e211]
              - cell "more_vert" [ref=e212]:
                - button "more_vert" [ref=e213]:
                  - generic [ref=e214]: more_vert
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
> 79  |     await expect(page.getByTestId(`link-clicks-${code}`)).toContainText('0');
      |                                                           ^ Error: expect(locator).toContainText(expected) failed
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
  106 |     
  107 |     // 4. Wait for it to finish
  108 |     await expect(page.getByTestId('drawer-sync-status')).toBeHidden();
  109 |     await expect(page.getByTestId('drawer-total-clicks')).toContainText('0');
  110 |   });
  111 | 
  112 |   test('should gracefully handle offline transition and recovery', async ({ page, context }) => {
  113 |     // 1. Initial State: Online
  114 |     await expect(page.getByTestId('system-status')).toContainText('Backend Active');
  115 | 
  116 |     // 2. Go Offline (Mock error for health check)
  117 |     await context.route('**/actuator/health', route => route.fulfill({ status: 503 }));
  118 |     
  119 |     // Wait for heartbeat
  120 |     await expect(page.getByTestId('system-status')).toContainText('Backend Offline', { timeout: 15000 });
  121 |     
  122 |     // Create button should be disabled
  123 |     await expect(page.getByTestId('execute-shorten-btn')).toBeDisabled();
  124 | 
  125 |     // 3. Recover
  126 |     await context.unroute('**/actuator/health');
  127 |     await expect(page.getByTestId('system-status')).toContainText('Backend Active', { timeout: 15000 });
  128 |     
  129 |     // Create button should be re-enabled
  130 |     await expect(page.getByTestId('execute-shorten-btn')).toBeEnabled();
  131 |   });
  132 | });
  133 | 
```