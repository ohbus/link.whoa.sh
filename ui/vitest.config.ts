/// <reference types="vitest" />
import { defineConfig } from 'vitest/config';
import angular from '@analogjs/vite-plugin-angular';

export default defineConfig({
  plugins: [
    // @ts-ignore
    angular(),
  ],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['src/vitest.setup.ts'],
    include: ['src/**/*.{spec,test}.ts'],
    reporters: ['default', 'junit'],
    outputFile: './test-results/junit.xml',
    /* 
     * IndexedDB (via fake-indexeddb) can be flakey with parallel execution in CI.
     * We enforce a single thread to ensure deterministic database state.
     */
    pool: 'threads',
    poolOptions: {
      threads: {
        singleThread: true,
      }
    },
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      include: ['src/app/**/*.ts'],
      exclude: ['src/app/**/*.spec.ts', 'src/app/app.config.ts', 'src/app/app.routes.ts', 'src/vitest.setup.ts']
    },
  },
});
