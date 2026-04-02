# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: advanced-patterns.spec.ts >> Advanced Data Patterns & Monkey Testing >> should debounce network calls during rapid monkey scrolling
- Location: e2e/advanced-patterns.spec.ts:11:7

# Error details

```
Error: apiRequestContext.post: connect ECONNREFUSED ::1:8844
Call log:
  - → POST http://localhost:8844/api/testing/reset
    - user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.7727.15 Safari/537.36
    - accept: */*
    - accept-encoding: gzip,deflate,br

```