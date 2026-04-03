import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright Configuration optimized for high-performance CI/CD.
 */
export default defineConfig({
  testDir: './e2e',
  workers: 1,
  fullyParallel: false,
  forbidOnly: !!process.env['CI'],
  retries: process.env['CI'] ? 3 : 0,
  reporter: process.env['CI'] ? [['list'], ['html']] : [['list']],
  
  timeout: 120 * 1000,
  expect: {
    timeout: 15 * 1000,
  },

  use: {
    baseURL: 'http://127.0.0.1:8844',
    ignoreHTTPSErrors: true,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'on-first-retry',
  },

  webServer: {
    /* 
     * Pipe stdout/stderr to the CI logs so we can see why it's failing.
     */
    stdout: 'pipe',
    stderr: 'pipe',
    command: 'cd .. && java -jar build/libs/link.whoa-0.0.1-SNAPSHOT.jar',
    url: 'http://127.0.0.1:8844/actuator/health',
    reuseExistingServer: !process.env['CI'],
    timeout: 1200 * 1000,
    env: {
      'SPRING_PROFILES_ACTIVE': 'dev',
      'DB_HOST': '127.0.0.1'
    }
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
