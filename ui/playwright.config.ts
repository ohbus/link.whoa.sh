import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright Configuration optimized for high-performance CI/CD.
 * We serve the UI directly from the Spring Boot backend (port 8844) 
 * to avoid the overhead of starting a separate Angular dev server in CI.
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
  
  timeout: 60 * 1000,
  expect: {
    timeout: 15 * 1000,
  },

  use: {
    /* 
     * Base URL points to the Backend which serves the static frontend assets.
     * This ensures we are testing the actual production-ready bundle.
     */
    baseURL: 'http://127.0.0.1:8844',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'on-first-retry',
  },

  /* 
   * Orchestrate the Backend. 
   * In CI, we use the pre-built JAR for maximum speed.
   * Locally, we fallback to bootRun for developer convenience.
   */
  webServer: {
    command: 'cd .. && (java -jar build/libs/link.whoa-0.0.1-SNAPSHOT.jar || ./gradlew bootRun --args="--spring.profiles.active=dev")',
    url: 'http://127.0.0.1:8844/actuator/health',
    reuseExistingServer: !process.env['CI'],
    timeout: 300 * 1000,
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
