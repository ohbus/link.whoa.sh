# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: shortening.spec.ts >> Shortening & Integrity >> should auto-focus destination input on sidebar button click
- Location: e2e/shortening.spec.ts:35:7

# Error details

```
Error: apiRequestContext.post: connect ECONNREFUSED 127.0.0.1:8844
Call log:
  - → POST http://127.0.0.1:8844/api/testing/reset
    - user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.7727.15 Safari/537.36
    - accept: */*
    - accept-encoding: gzip,deflate,br

```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | test.describe('Shortening & Integrity', () => {
  4  |   test.beforeEach(async ({ page, request }) => {
  5  |     // Reset Backend State
> 6  |     await request.post('http://127.0.0.1:8844/api/testing/reset');
     |                   ^ Error: apiRequestContext.post: connect ECONNREFUSED 127.0.0.1:8844
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
  23 |     await expect(page.getByTestId(`link-row-${code}`)).toBeVisible();
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