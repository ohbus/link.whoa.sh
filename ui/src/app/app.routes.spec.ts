import { describe, it, expect } from 'vitest';
import { routes } from './app.routes';

describe('AppRoutes', () => {
  it('should be defined as an array', () => {
    expect(routes).toBeDefined();
    expect(Array.isArray(routes)).toBe(true);
  });
});
