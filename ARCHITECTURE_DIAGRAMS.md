# Architecture Overview

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    TEST EXECUTION FRAMEWORK                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Tests ────► Services ────► Client ────► API Endpoint       │
│    │           │             │                               │
│    ▼           ▼             ▼                               │
│  Assertions  Response      Request                          │
│  Schema       Wrapper      Builder                          │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  CONFIGURATION & CONTROL                           │    │
│  │  • ExecutionController (CI/groups decisions)       │    │
│  │  • ConfigManager (environment profiles)            │    │
│  │  • Filters (request/response logging)              │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Core Layers:**
- **Tests**: TestNG classes with groups (smoke/regression/integration)
- **Services**: Business logic for API operations (GenerateService, DocumentService)
- **Client**: Unified API client with retry mechanism (ApiClient)
- **Config**: Environment profiles and settings (ConfigManager)
- **ExecutionController**: Single source of truth for test execution decisions
- **Filters**: Centralized logging (ApiLoggingFilter)

## Execution Flow

1. **Test Starts**
   - ExecutionInterceptor intercepts TestNG methods
   - ExecutionController evaluates CI mode and group filters

2. **Execution Decision**
   - CI=true: Auto-sets groups to "smoke,regression"
   - CI=false: Uses specified groups or runs all
   - Integration tests skipped in CI by default

3. **Environment Resolution**
   - ConfigManager loads config.properties
   - Resolves base URL based on environment (dev/qa/prod)
   - Applies timeouts and retry settings

4. **API Request**
   - ApiClient sends HTTP request via RestAssured
   - RequestSpecBuilderFactory sets base URL and headers
   - ApiLoggingFilter captures request/response for Allure

5. **Validation**
   - AssertionUtil validates response status and fields
   - ResponseWrapper validates JSON schema
   - Failures trigger retry logic (configurable attempts)

6. **Reporting**
   - Allure generates test reports with attachments
   - Console logs show execution decisions and failures
   - StepLogger provides human-readable test steps

## Execution Decision Summary

| Mode | Groups | Behavior |
|------|--------|----------|
| Local | all | Run all tests |
| Local | smoke | Smoke tests only |
| Local | regression | Regression tests only |
| CI | default | Smoke + regression (integration skipped) |
| CI | with integration | All tests (override CI policy) |

**Key Points:**
- CI mode automatically excludes integration tests for faster pipelines
- Use `-DCI=false` to include integration tests in CI
- Environment switching via `-Denv=dev/qa/prod`
- Group filtering via `-Dgroups=smoke/regression/integration`

---

**Last Updated**: May 5, 2026
