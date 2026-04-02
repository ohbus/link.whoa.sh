# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: lifecycle.spec.ts >> Whoa Link Shortener Full Lifecycle >> should handle custom code collisions gracefully
- Location: e2e/lifecycle.spec.ts:103:7

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: getByTestId('link-row-collision-test')
Expected: visible
Timeout: 10000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 10000ms
  - waiting for getByTestId('link-row-collision-test')

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
        - generic [ref=e12]: 1,092
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
                - text: https://first.com
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
                - textbox "my-custom-path" [ref=e56]: collision-test
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
            - row "dev001 https://github.com/ohbus/link.whoa.sh localhost:8844/dev001 content_copy 137 MAR 09, 2026 more_vert" [ref=e87] [cursor=pointer]:
              - cell "dev001 https://github.com/ohbus/link.whoa.sh" [ref=e88]:
                - generic [ref=e89]:
                  - generic [ref=e90]: dev001
                  - generic "https://github.com/ohbus/link.whoa.sh" [ref=e91]
              - cell "localhost:8844/dev001 content_copy" [ref=e92]:
                - generic [ref=e93]:
                  - code [ref=e94]: localhost:8844/dev001
                  - generic [ref=e95]: content_copy
              - cell "137" [ref=e96]:
                - generic [ref=e98]: "137"
              - cell "MAR 09, 2026" [ref=e99]
              - cell "more_vert" [ref=e100]:
                - button "more_vert" [ref=e101]:
                  - generic [ref=e102]: more_vert
            - row "dev007 https://news.ycombinator.com localhost:8844/dev007 content_copy 247 MAR 09, 2026 more_vert" [ref=e103] [cursor=pointer]:
              - cell "dev007 https://news.ycombinator.com" [ref=e104]:
                - generic [ref=e105]:
                  - generic [ref=e106]: dev007
                  - generic "https://news.ycombinator.com" [ref=e107]
              - cell "localhost:8844/dev007 content_copy" [ref=e108]:
                - generic [ref=e109]:
                  - code [ref=e110]: localhost:8844/dev007
                  - generic [ref=e111]: content_copy
              - cell "247" [ref=e112]:
                - generic [ref=e114]: "247"
              - cell "MAR 09, 2026" [ref=e115]
              - cell "more_vert" [ref=e116]:
                - button "more_vert" [ref=e117]:
                  - generic [ref=e118]: more_vert
            - row "dev003 https://kotlinlang.org/docs/home.html localhost:8844/dev003 content_copy 135 MAR 04, 2026 more_vert" [ref=e119] [cursor=pointer]:
              - cell "dev003 https://kotlinlang.org/docs/home.html" [ref=e120]:
                - generic [ref=e121]:
                  - generic [ref=e122]: dev003
                  - generic "https://kotlinlang.org/docs/home.html" [ref=e123]
              - cell "localhost:8844/dev003 content_copy" [ref=e124]:
                - generic [ref=e125]:
                  - code [ref=e126]: localhost:8844/dev003
                  - generic [ref=e127]: content_copy
              - cell "135" [ref=e128]:
                - generic [ref=e130]: "135"
              - cell "MAR 04, 2026" [ref=e131]
              - cell "more_vert" [ref=e132]:
                - button "more_vert" [ref=e133]:
                  - generic [ref=e134]: more_vert
            - row "dev004 https://angular.dev/overview localhost:8844/dev004 content_copy 63 FEB 11, 2026 more_vert" [ref=e135] [cursor=pointer]:
              - cell "dev004 https://angular.dev/overview" [ref=e136]:
                - generic [ref=e137]:
                  - generic [ref=e138]: dev004
                  - generic "https://angular.dev/overview" [ref=e139]
              - cell "localhost:8844/dev004 content_copy" [ref=e140]:
                - generic [ref=e141]:
                  - code [ref=e142]: localhost:8844/dev004
                  - generic [ref=e143]: content_copy
              - cell "63" [ref=e144]:
                - generic [ref=e146]: "63"
              - cell "FEB 11, 2026" [ref=e147]
              - cell "more_vert" [ref=e148]:
                - button "more_vert" [ref=e149]:
                  - generic [ref=e150]: more_vert
            - row "dev008 https://reddit.com localhost:8844/dev008 content_copy 216 JAN 09, 2026 more_vert" [ref=e151] [cursor=pointer]:
              - cell "dev008 https://reddit.com" [ref=e152]:
                - generic [ref=e153]:
                  - generic [ref=e154]: dev008
                  - generic "https://reddit.com" [ref=e155]
              - cell "localhost:8844/dev008 content_copy" [ref=e156]:
                - generic [ref=e157]:
                  - code [ref=e158]: localhost:8844/dev008
                  - generic [ref=e159]: content_copy
              - cell "216" [ref=e160]:
                - generic [ref=e162]: "216"
              - cell "JAN 09, 2026" [ref=e163]
              - cell "more_vert" [ref=e164]:
                - button "more_vert" [ref=e165]:
                  - generic [ref=e166]: more_vert
            - row "dev002 https://spring.io/projects/spring-boot localhost:8844/dev002 content_copy 225 DEC 31, 2025 more_vert" [ref=e167] [cursor=pointer]:
              - cell "dev002 https://spring.io/projects/spring-boot" [ref=e168]:
                - generic [ref=e169]:
                  - generic [ref=e170]: dev002
                  - generic "https://spring.io/projects/spring-boot" [ref=e171]
              - cell "localhost:8844/dev002 content_copy" [ref=e172]:
                - generic [ref=e173]:
                  - code [ref=e174]: localhost:8844/dev002
                  - generic [ref=e175]: content_copy
              - cell "225" [ref=e176]:
                - generic [ref=e178]: "225"
              - cell "DEC 31, 2025" [ref=e179]
              - cell "more_vert" [ref=e180]:
                - button "more_vert" [ref=e181]:
                  - generic [ref=e182]: more_vert
            - row "dev006 https://docker.com localhost:8844/dev006 content_copy 117 DEC 12, 2025 more_vert" [ref=e183] [cursor=pointer]:
              - cell "dev006 https://docker.com" [ref=e184]:
                - generic [ref=e185]:
                  - generic [ref=e186]: dev006
                  - generic "https://docker.com" [ref=e187]
              - cell "localhost:8844/dev006 content_copy" [ref=e188]:
                - generic [ref=e189]:
                  - code [ref=e190]: localhost:8844/dev006
                  - generic [ref=e191]: content_copy
              - cell "117" [ref=e192]:
                - generic [ref=e194]: "117"
              - cell "DEC 12, 2025" [ref=e195]
              - cell "more_vert" [ref=e196]:
                - button "more_vert" [ref=e197]:
                  - generic [ref=e198]: more_vert
            - row "dev005 https://www.postgresql.org/docs/ localhost:8844/dev005 content_copy 135 DEC 08, 2025 more_vert" [ref=e199] [cursor=pointer]:
              - cell "dev005 https://www.postgresql.org/docs/" [ref=e200]:
                - generic [ref=e201]:
                  - generic [ref=e202]: dev005
                  - generic "https://www.postgresql.org/docs/" [ref=e203]
              - cell "localhost:8844/dev005 content_copy" [ref=e204]:
                - generic [ref=e205]:
                  - code [ref=e206]: localhost:8844/dev005
                  - generic [ref=e207]: content_copy
              - cell "135" [ref=e208]:
                - generic [ref=e210]: "135"
              - cell "DEC 08, 2025" [ref=e211]
              - cell "more_vert" [ref=e212]:
                - button "more_vert" [ref=e213]:
                  - generic [ref=e214]: more_vert
```

# Test source

```ts
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
  109 |     await page.getByTestId('custom-path-input').fill(code);
  110 |     await page.getByTestId('execute-shorten-btn').click();
> 111 |     await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
      |                                                        ^ Error: expect(locator).toBeVisible() failed
  112 | 
  113 |     // 2. Try to create second link with same code
  114 |     await page.getByTestId('destination-url-input').fill('https://second.com');
  115 |     await page.getByTestId('custom-path-input').fill(code);
  116 |     await page.getByTestId('execute-shorten-btn').click();
  117 | 
  118 |     // 3. Verify Error Messaging
  119 |     await expect(page.getByTestId('shortening-error')).toBeVisible();
  120 |     await expect(page.getByTestId('shortening-error')).toContainText(`The path '${code}' is already registered`);
  121 |   });
  122 | });
  123 | 
```