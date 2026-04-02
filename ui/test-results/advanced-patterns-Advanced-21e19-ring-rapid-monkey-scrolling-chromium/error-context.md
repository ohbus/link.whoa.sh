# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: advanced-patterns.spec.ts >> Advanced Data Patterns & Monkey Testing >> should debounce network calls during rapid monkey scrolling
- Location: e2e/advanced-patterns.spec.ts:11:7

# Error details

```
Test timeout of 60000ms exceeded.
```

```
Error: locator.click: Test timeout of 60000ms exceeded.
Call log:
  - waiting for getByTestId('execute-shorten-btn')
    - locator resolved to <button disabled data-testid="execute-shorten-btn" class="px-8 py-4 bg-primary text-on-primary font-black uppercase tracking-widest text-sm rounded-lg hover:brightness-110 active:scale-[0.98] transition-all disabled:opacity-50 disabled:cursor-not-allowed"> Executing... </button>
  - attempting click action
    2 × waiting for element to be visible, enabled and stable
      - element is not enabled
    - retrying click action
    - waiting 20ms
    2 × waiting for element to be visible, enabled and stable
      - element is not enabled
    - retrying click action
      - waiting 100ms
    114 × waiting for element to be visible, enabled and stable
        - element is not enabled
      - retrying click action
        - waiting 500ms

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
        - generic [ref=e12]: "0"
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
            - generic [ref=e60]: 20ms
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
            - row "nhvtow https://monkey-2.com localhost:8844/nhvtow content_copy 0 APR 02, 2026 more_vert" [ref=e81] [cursor=pointer]:
              - cell "nhvtow https://monkey-2.com" [ref=e82]:
                - generic [ref=e83]:
                  - generic [ref=e84]: nhvtow
                  - generic "https://monkey-2.com" [ref=e85]
              - cell "localhost:8844/nhvtow content_copy" [ref=e86]:
                - generic [ref=e87]:
                  - code [ref=e88]: localhost:8844/nhvtow
                  - generic [ref=e89]: content_copy
              - cell "0" [ref=e90]:
                - generic [ref=e92]: "0"
              - cell "APR 02, 2026" [ref=e93]
              - cell "more_vert" [ref=e94]:
                - button "more_vert" [ref=e95]:
                  - generic [ref=e96]: more_vert
            - row "olakie https://monkey-1.com localhost:8844/olakie content_copy 0 APR 02, 2026 more_vert" [ref=e97] [cursor=pointer]:
              - cell "olakie https://monkey-1.com" [ref=e98]:
                - generic [ref=e99]:
                  - generic [ref=e100]: olakie
                  - generic "https://monkey-1.com" [ref=e101]
              - cell "localhost:8844/olakie content_copy" [ref=e102]:
                - generic [ref=e103]:
                  - code [ref=e104]: localhost:8844/olakie
                  - generic [ref=e105]: content_copy
              - cell "0" [ref=e106]:
                - generic [ref=e108]: "0"
              - cell "APR 02, 2026" [ref=e109]
              - cell "more_vert" [ref=e110]:
                - button "more_vert" [ref=e111]:
                  - generic [ref=e112]: more_vert
            - row "dev002 https://spring.io/projects/spring-boot localhost:8844/dev002 content_copy 147 MAR 19, 2026 more_vert" [ref=e113] [cursor=pointer]:
              - cell "dev002 https://spring.io/projects/spring-boot" [ref=e114]:
                - generic [ref=e115]:
                  - generic [ref=e116]: dev002
                  - generic "https://spring.io/projects/spring-boot" [ref=e117]
              - cell "localhost:8844/dev002 content_copy" [ref=e118]:
                - generic [ref=e119]:
                  - code [ref=e120]: localhost:8844/dev002
                  - generic [ref=e121]: content_copy
              - cell "147" [ref=e122]:
                - generic [ref=e124]: "147"
              - cell "MAR 19, 2026" [ref=e125]
              - cell "more_vert" [ref=e126]:
                - button "more_vert" [ref=e127]:
                  - generic [ref=e128]: more_vert
            - row "dev001 https://github.com/ohbus/link.whoa.sh localhost:8844/dev001 content_copy 221 MAR 18, 2026 more_vert" [ref=e129] [cursor=pointer]:
              - cell "dev001 https://github.com/ohbus/link.whoa.sh" [ref=e130]:
                - generic [ref=e131]:
                  - generic [ref=e132]: dev001
                  - generic "https://github.com/ohbus/link.whoa.sh" [ref=e133]
              - cell "localhost:8844/dev001 content_copy" [ref=e134]:
                - generic [ref=e135]:
                  - code [ref=e136]: localhost:8844/dev001
                  - generic [ref=e137]: content_copy
              - cell "221" [ref=e138]:
                - generic [ref=e140]: "221"
              - cell "MAR 18, 2026" [ref=e141]
              - cell "more_vert" [ref=e142]:
                - button "more_vert" [ref=e143]:
                  - generic [ref=e144]: more_vert
            - row "dev003 https://kotlinlang.org/docs/home.html localhost:8844/dev003 content_copy 201 MAR 12, 2026 more_vert" [ref=e145] [cursor=pointer]:
              - cell "dev003 https://kotlinlang.org/docs/home.html" [ref=e146]:
                - generic [ref=e147]:
                  - generic [ref=e148]: dev003
                  - generic "https://kotlinlang.org/docs/home.html" [ref=e149]
              - cell "localhost:8844/dev003 content_copy" [ref=e150]:
                - generic [ref=e151]:
                  - code [ref=e152]: localhost:8844/dev003
                  - generic [ref=e153]: content_copy
              - cell "201" [ref=e154]:
                - generic [ref=e156]: "201"
              - cell "MAR 12, 2026" [ref=e157]
              - cell "more_vert" [ref=e158]:
                - button "more_vert" [ref=e159]:
                  - generic [ref=e160]: more_vert
            - row "dev007 https://news.ycombinator.com localhost:8844/dev007 content_copy 161 FEB 18, 2026 more_vert" [ref=e161] [cursor=pointer]:
              - cell "dev007 https://news.ycombinator.com" [ref=e162]:
                - generic [ref=e163]:
                  - generic [ref=e164]: dev007
                  - generic "https://news.ycombinator.com" [ref=e165]
              - cell "localhost:8844/dev007 content_copy" [ref=e166]:
                - generic [ref=e167]:
                  - code [ref=e168]: localhost:8844/dev007
                  - generic [ref=e169]: content_copy
              - cell "161" [ref=e170]:
                - generic [ref=e172]: "161"
              - cell "FEB 18, 2026" [ref=e173]
              - cell "more_vert" [ref=e174]:
                - button "more_vert" [ref=e175]:
                  - generic [ref=e176]: more_vert
            - row "dev004 https://angular.dev/overview localhost:8844/dev004 content_copy 100 FEB 14, 2026 more_vert" [ref=e177] [cursor=pointer]:
              - cell "dev004 https://angular.dev/overview" [ref=e178]:
                - generic [ref=e179]:
                  - generic [ref=e180]: dev004
                  - generic "https://angular.dev/overview" [ref=e181]
              - cell "localhost:8844/dev004 content_copy" [ref=e182]:
                - generic [ref=e183]:
                  - code [ref=e184]: localhost:8844/dev004
                  - generic [ref=e185]: content_copy
              - cell "100" [ref=e186]:
                - generic [ref=e188]: "100"
              - cell "FEB 14, 2026" [ref=e189]
              - cell "more_vert" [ref=e190]:
                - button "more_vert" [ref=e191]:
                  - generic [ref=e192]: more_vert
            - row "dev005 https://www.postgresql.org/docs/ localhost:8844/dev005 content_copy 91 JAN 23, 2026 more_vert" [ref=e193] [cursor=pointer]:
              - cell "dev005 https://www.postgresql.org/docs/" [ref=e194]:
                - generic [ref=e195]:
                  - generic [ref=e196]: dev005
                  - generic "https://www.postgresql.org/docs/" [ref=e197]
              - cell "localhost:8844/dev005 content_copy" [ref=e198]:
                - generic [ref=e199]:
                  - code [ref=e200]: localhost:8844/dev005
                  - generic [ref=e201]: content_copy
              - cell "91" [ref=e202]:
                - generic [ref=e204]: "91"
              - cell "JAN 23, 2026" [ref=e205]
              - cell "more_vert" [ref=e206]:
                - button "more_vert" [ref=e207]:
                  - generic [ref=e208]: more_vert
            - row "dev006 https://docker.com localhost:8844/dev006 content_copy 242 JAN 07, 2026 more_vert" [ref=e209] [cursor=pointer]:
              - cell "dev006 https://docker.com" [ref=e210]:
                - generic [ref=e211]:
                  - generic [ref=e212]: dev006
                  - generic "https://docker.com" [ref=e213]
              - cell "localhost:8844/dev006 content_copy" [ref=e214]:
                - generic [ref=e215]:
                  - code [ref=e216]: localhost:8844/dev006
                  - generic [ref=e217]: content_copy
              - cell "242" [ref=e218]:
                - generic [ref=e220]: "242"
              - cell "JAN 07, 2026" [ref=e221]
              - cell "more_vert" [ref=e222]:
                - button "more_vert" [ref=e223]:
                  - generic [ref=e224]: more_vert
            - row "dev008 https://reddit.com localhost:8844/dev008 content_copy 218 DEC 10, 2025 more_vert" [ref=e225] [cursor=pointer]:
              - cell "dev008 https://reddit.com" [ref=e226]:
                - generic [ref=e227]:
                  - generic [ref=e228]: dev008
                  - generic "https://reddit.com" [ref=e229]
              - cell "localhost:8844/dev008 content_copy" [ref=e230]:
                - generic [ref=e231]:
                  - code [ref=e232]: localhost:8844/dev008
                  - generic [ref=e233]: content_copy
              - cell "218" [ref=e234]:
                - generic [ref=e236]: "218"
              - cell "DEC 10, 2025" [ref=e237]
              - cell "more_vert" [ref=e238]:
                - button "more_vert" [ref=e239]:
                  - generic [ref=e240]: more_vert
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
> 15  |       await page.getByTestId('execute-shorten-btn').click();
      |                                                     ^ Error: locator.click: Test timeout of 60000ms exceeded.
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
```