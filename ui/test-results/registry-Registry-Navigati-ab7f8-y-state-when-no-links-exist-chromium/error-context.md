# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: registry.spec.ts >> Registry & Navigation >> should show empty state when no links exist
- Location: e2e/registry.spec.ts:11:7

# Error details

```
Error: apiRequestContext.post: connect ECONNREFUSED ::1:4200
Call log:
  - → POST http://localhost:4200/api/testing/reset
    - user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.7727.15 Safari/537.36
    - accept: */*
    - accept-encoding: gzip,deflate,br

```