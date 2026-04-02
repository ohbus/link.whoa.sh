import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright Configuration optimized for high-performance CI/CD.
 * See https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  testDir: './e2e',
  /* CI Optimization: Sequential execution to protect stateful database reset hooks */
  workers: 1,
  fullyParallel: false,
  forbidOnly: !!process.env['CI'],
  retries: process.env['CI'] ? 2 : 0,
  /* Verbose reporting for CI visibility */
  reporter: process.env['CI'] ? [['list'], ['html']] : [['list']],
  
  use: {
    /* Use 127.0.0.1 to bypass IPv6 resolution overhead in CI containers */
    baseURL: 'http://127.0.0.1:4200',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'on-first-retry',
  },

  /* Standard Playwright WebServer orchestration */
  webServer: [
    {
      command: 'npm start',
      url: 'http://127.0.0.1:4200',
      reuseExistingServer: !process.env['CI'],
      timeout: 180 * 1000,
    },
    {
      /* Start backend only after pre-build is complete in CI */
      command: 'cd .. && ./gradlew bootRun --args="--spring.profiles.active=dev"',
      url: 'http://127.0.0.1:8844/actuator/health',
      reuseExistingServer: !process.env['CI'],
      timeout: 240 * 1000,
    }
  ],

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
