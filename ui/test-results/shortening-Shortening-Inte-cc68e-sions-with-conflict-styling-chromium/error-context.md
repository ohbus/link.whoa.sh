# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: shortening.spec.ts >> Shortening & Integrity >> should handle custom code collisions with conflict styling
- Location: e2e/shortening.spec.ts:15:7

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: getByTestId('link-row-fixed-path')
Expected: visible
Timeout: 10000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 10000ms
  - waiting for getByTestId('link-row-fixed-path')

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
        - generic [ref=e12]: 1,132
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
                - text: https://google.com
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
                - textbox "my-custom-path" [ref=e56]: fixed-path
              - generic [ref=e57]:
                - generic [ref=e58]: error
                - text: An unexpected system error occurred.
          - button "Execute Shorten" [ref=e60] [cursor=pointer]
        - generic [ref=e61]:
          - generic [ref=e62]:
            - heading "Efficiency Pulse" [level=3] [ref=e63]
            - paragraph [ref=e64]: 100% Uptime
          - generic [ref=e65]:
            - generic [ref=e66]: 19ms
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
            - row "dev008 https://reddit.com localhost:8844/dev008 content_copy 154 MAR 05, 2026 more_vert" [ref=e87] [cursor=pointer]:
              - cell "dev008 https://reddit.com" [ref=e88]:
                - generic [ref=e89]:
                  - generic [ref=e90]: dev008
                  - generic "https://reddit.com" [ref=e91]
              - cell "localhost:8844/dev008 content_copy" [ref=e92]:
                - generic [ref=e93]:
                  - code [ref=e94]: localhost:8844/dev008
                  - generic [ref=e95]: content_copy
              - cell "154" [ref=e96]:
                - generic [ref=e98]: "154"
              - cell "MAR 05, 2026" [ref=e99]
              - cell "more_vert" [ref=e100]:
                - button "more_vert" [ref=e101]:
                  - generic [ref=e102]: more_vert
            - row "dev005 https://www.postgresql.org/docs/ localhost:8844/dev005 content_copy 112 MAR 03, 2026 more_vert" [ref=e103] [cursor=pointer]:
              - cell "dev005 https://www.postgresql.org/docs/" [ref=e104]:
                - generic [ref=e105]:
                  - generic [ref=e106]: dev005
                  - generic "https://www.postgresql.org/docs/" [ref=e107]
              - cell "localhost:8844/dev005 content_copy" [ref=e108]:
                - generic [ref=e109]:
                  - code [ref=e110]: localhost:8844/dev005
                  - generic [ref=e111]: content_copy
              - cell "112" [ref=e112]:
                - generic [ref=e114]: "112"
              - cell "MAR 03, 2026" [ref=e115]
              - cell "more_vert" [ref=e116]:
                - button "more_vert" [ref=e117]:
                  - generic [ref=e118]: more_vert
            - row "dev004 https://angular.dev/overview localhost:8844/dev004 content_copy 149 MAR 03, 2026 more_vert" [ref=e119] [cursor=pointer]:
              - cell "dev004 https://angular.dev/overview" [ref=e120]:
                - generic [ref=e121]:
                  - generic [ref=e122]: dev004
                  - generic "https://angular.dev/overview" [ref=e123]
              - cell "localhost:8844/dev004 content_copy" [ref=e124]:
                - generic [ref=e125]:
                  - code [ref=e126]: localhost:8844/dev004
                  - generic [ref=e127]: content_copy
              - cell "149" [ref=e128]:
                - generic [ref=e130]: "149"
              - cell "MAR 03, 2026" [ref=e131]
              - cell "more_vert" [ref=e132]:
                - button "more_vert" [ref=e133]:
                  - generic [ref=e134]: more_vert
            - row "dev003 https://kotlinlang.org/docs/home.html localhost:8844/dev003 content_copy 222 FEB 22, 2026 more_vert" [ref=e135] [cursor=pointer]:
              - cell "dev003 https://kotlinlang.org/docs/home.html" [ref=e136]:
                - generic [ref=e137]:
                  - generic [ref=e138]: dev003
                  - generic "https://kotlinlang.org/docs/home.html" [ref=e139]
              - cell "localhost:8844/dev003 content_copy" [ref=e140]:
                - generic [ref=e141]:
                  - code [ref=e142]: localhost:8844/dev003
                  - generic [ref=e143]: content_copy
              - cell "222" [ref=e144]:
                - generic [ref=e146]: "222"
              - cell "FEB 22, 2026" [ref=e147]
              - cell "more_vert" [ref=e148]:
                - button "more_vert" [ref=e149]:
                  - generic [ref=e150]: more_vert
            - row "dev006 https://docker.com localhost:8844/dev006 content_copy 137 FEB 10, 2026 more_vert" [ref=e151] [cursor=pointer]:
              - cell "dev006 https://docker.com" [ref=e152]:
                - generic [ref=e153]:
                  - generic [ref=e154]: dev006
                  - generic "https://docker.com" [ref=e155]
              - cell "localhost:8844/dev006 content_copy" [ref=e156]:
                - generic [ref=e157]:
                  - code [ref=e158]: localhost:8844/dev006
                  - generic [ref=e159]: content_copy
              - cell "137" [ref=e160]:
                - generic [ref=e162]: "137"
              - cell "FEB 10, 2026" [ref=e163]
              - cell "more_vert" [ref=e164]:
                - button "more_vert" [ref=e165]:
                  - generic [ref=e166]: more_vert
            - row "dev007 https://news.ycombinator.com localhost:8844/dev007 content_copy 218 FEB 02, 2026 more_vert" [ref=e167] [cursor=pointer]:
              - cell "dev007 https://news.ycombinator.com" [ref=e168]:
                - generic [ref=e169]:
                  - generic [ref=e170]: dev007
                  - generic "https://news.ycombinator.com" [ref=e171]
              - cell "localhost:8844/dev007 content_copy" [ref=e172]:
                - generic [ref=e173]:
                  - code [ref=e174]: localhost:8844/dev007
                  - generic [ref=e175]: content_copy
              - cell "218" [ref=e176]:
                - generic [ref=e178]: "218"
              - cell "FEB 02, 2026" [ref=e179]
              - cell "more_vert" [ref=e180]:
                - button "more_vert" [ref=e181]:
                  - generic [ref=e182]: more_vert
            - row "dev001 https://github.com/ohbus/link.whoa.sh localhost:8844/dev001 content_copy 85 JAN 09, 2026 more_vert" [ref=e183] [cursor=pointer]:
              - cell "dev001 https://github.com/ohbus/link.whoa.sh" [ref=e184]:
                - generic [ref=e185]:
                  - generic [ref=e186]: dev001
                  - generic "https://github.com/ohbus/link.whoa.sh" [ref=e187]
              - cell "localhost:8844/dev001 content_copy" [ref=e188]:
                - generic [ref=e189]:
                  - code [ref=e190]: localhost:8844/dev001
                  - generic [ref=e191]: content_copy
              - cell "85" [ref=e192]:
                - generic [ref=e194]: "85"
              - cell "JAN 09, 2026" [ref=e195]
              - cell "more_vert" [ref=e196]:
                - button "more_vert" [ref=e197]:
                  - generic [ref=e198]: more_vert
            - row "dev002 https://spring.io/projects/spring-boot localhost:8844/dev002 content_copy 245 DEC 22, 2025 more_vert" [ref=e199] [cursor=pointer]:
              - cell "dev002 https://spring.io/projects/spring-boot" [ref=e200]:
                - generic [ref=e201]:
                  - generic [ref=e202]: dev002
                  - generic "https://spring.io/projects/spring-boot" [ref=e203]
              - cell "localhost:8844/dev002 content_copy" [ref=e204]:
                - generic [ref=e205]:
                  - code [ref=e206]: localhost:8844/dev002
                  - generic [ref=e207]: content_copy
              - cell "245" [ref=e208]:
                - generic [ref=e210]: "245"
              - cell "DEC 22, 2025" [ref=e211]
              - cell "more_vert" [ref=e212]:
                - button "more_vert" [ref=e213]:
                  - generic [ref=e214]: more_vert
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | test.describe('Shortening & Integrity', () => {
  4  |   test.beforeEach(async ({ page, request }) => {
  5  |     // Reset Backend State
  6  |     await request.post('http://127.0.0.1:8844/api/testing/reset');
  7  |     
  8  |     await page.goto('/#/');
  9  |     await page.evaluate(async () => { 
  10 |       await indexedDB.deleteDatabase('WhoaDatabase'); 
  11 |     });
  12 |     await page.reload();
  13 |   });
  14 | 
  15 |   test('should handle custom code collisions with conflict styling', async ({ page }) => {
  16 |     const code = 'fixed-path';
  17 |     await page.getByTestId('destination-url-input').fill('https://google.com');
  18 |     await page.getByTestId('custom-code-summary').click();
  19 |     await page.getByTestId('custom-path-input').fill(code);
  20 |     await page.getByTestId('execute-shorten-btn').click();
  21 |     
  22 |     // Wait for the first one to appear
> 23 |     await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
     |                                                        ^ Error: expect(locator).toBeVisible() failed
  24 | 
  25 |     // Try to create another with the same code
  26 |     await page.getByTestId('destination-url-input').fill('https://bing.com');
  27 |     await page.getByTestId('custom-path-input').fill(code);
  28 |     await page.getByTestId('execute-shorten-btn').click();
  29 | 
  30 |     // Verify Error Messaging
  31 |     await expect(page.getByTestId('shortening-error')).toContainText(`path '${code}' is already registered`);
  32 |     await expect(page.getByTestId('shortening-error')).toHaveClass(/text-error/);
  33 |   });
  34 | 
  35 |   test('should auto-focus destination input on sidebar button click', async ({ page }) => {
  36 |     // Scroll down away from input
  37 |     await page.evaluate(() => window.scrollTo(0, 1000));
  38 |     
  39 |     // Click sidebar button
  40 |     await page.getByTestId('sidebar-shorten-btn').click();
  41 |     
  42 |     // Verify focus and visibility
  43 |     const input = page.getByTestId('destination-url-input');
  44 |     await expect(input).toBeFocused();
  45 |     await expect(input).toBeInViewport();
  46 |   });
  47 | });
  48 | 
```