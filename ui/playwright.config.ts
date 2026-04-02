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
  
  /* Increased global timeout for heavy E2E flows in CI */
  timeout: 60 * 1000,
  expect: {
    timeout: 10 * 1000,
  },

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
      /* 
       * Start backend with dev profile. 
       * Explicitly overriding DB_HOST to ensure it talks to the CI postgres service on localhost.
       */
      command: 'cd .. && DB_HOST=127.0.0.1 SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun',
      url: 'http://127.0.0.1:8844/actuator/health',
      reuseExistingServer: !process.env['CI'],
      timeout: 300 * 1000,
    }
  ],

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
