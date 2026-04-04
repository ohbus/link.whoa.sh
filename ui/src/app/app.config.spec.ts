import { describe, it, expect } from 'vitest';
import { appConfig } from './app.config';

describe('AppConfig', () => {
  it('should have providers defined', () => {
    expect(appConfig).toBeDefined();
    expect(appConfig.providers).toBeDefined();
    expect(Array.isArray(appConfig.providers)).toBe(true);
  });
});
