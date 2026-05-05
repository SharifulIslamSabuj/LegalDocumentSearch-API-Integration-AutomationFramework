# Quick Reference Guide - Enterprise Automation Framework

## Overview

The Legal Document Search API Integration Automation Framework has been upgraded with enterprise-grade features:

1. **Execution Strategy Layer** - Centralized test execution control
2. **Environment Profile System** - Seamless environment switching
3. **Lightweight Observability** - Standardized logging and monitoring

---

## Quick Start Commands

### Run All Tests
```bash
./gradlew clean test
```

### Run Smoke Tests Only
```bash
./gradlew clean test -Dgroups=smoke
```

### Run Regression Tests Only
```bash
./gradlew clean test -Dgroups=regression
```

### Run Integration Tests Only
```bash
./gradlew clean test -Dgroups=integration
```

### Run in CI Mode (Smoke + Regression)
```bash
./gradlew clean test -DCI=true
```

### Run with Specific Environment
```bash
# Dev environment (localhost)
./gradlew clean test -Denv=dev

# QA environment
./gradlew clean test -Denv=qa

# Production environment
./gradlew clean test -Denv=prod
```

### Combined Flags
```bash
# CI mode with QA environment
./gradlew clean test -DCI=true -Denv=qa

# Smoke tests with Dev environment
./gradlew clean test -Dgroups=smoke -Denv=dev

# Regression tests with Prod environment
./gradlew clean test -Dgroups=regression -Denv=prod
```

---

## Understanding the Execution Strategy

### ExecutionController - Single Source of Truth

**Location**: `com.acmeai.xynoptik.api.integration.automation.config.ExecutionController`

The ExecutionController is the central decision engine for all test execution. It reads system properties and determines what tests should run.

**Key Methods**:
```java
ExecutionController.isCI()              // Check if in CI mode
ExecutionController.getEnv()            // Get active environment
ExecutionController.getGroups()         // Get active test groups
ExecutionController.isSmoke()           // Check if smoke tests should run
ExecutionController.isRegression()      // Check if regression tests should run
ExecutionController.isIntegration()     // Check if integration tests should run
```

### CI Mode Behavior

When `-DCI=true` is set:
- ✅ Smoke tests run
- ✅ Regression tests run
- ❌ Integration tests are skipped
- Groups parameter is automatically set to `smoke,regression`

**Log Output**:
```
[Observability] 14:23:45.123 CI Mode: true
[Observability] 14:23:45.124 Active Groups: smoke,regression
[Observability] 14:23:45.125 [SKIPPED] TestClass.integrationTest - Reason: CI POLICY
```

### Group-Based Filtering

Test methods use TestNG `@Test` annotation with groups:

```java
@Test(description = "...", groups = {"smoke", "regression"})
public void smokeTest() {
    // This test runs in smoke or regression mode
}

@Test(description = "...", groups = {"integration"})
public void integrationTest() {
    // This test only runs when integration group is active
    // Skipped in CI mode
}
```

---

## Environment Profiles

### Available Profiles

| Profile | URL | Use Case |
|---------|-----|----------|
| dev | http://localhost:8000 | Local development |
| qa | http://localhost:8000 | QA testing |
| prod | http://localhost:8000 | Production |

### ConfigManager - Environment Configuration

**Location**: `com.acmeai.xynoptik.api.integration.automation.config.ConfigManager`

```java
ConfigManager.getActiveProfile()  // Get current environment
ConfigManager.getBaseUrl()        // Get resolved base URL
ConfigManager.getEnv()            // Get env property
```

### Profile Resolution

The active profile is determined by:
1. ExecutionController.getEnv() reads the `env` system property
2. ConfigManager.getBaseUrl() resolves the URL based on profile
3. RequestSpecBuilderFactory uses the resolved URL for all requests

**Example**:
```bash
# This command uses QA environment
./gradlew clean test -Denv=qa

# Logs:
# [Observability] 14:23:45.123 [PROFILE] Environment: qa | Base URL: http://localhost:8000
```

---

## Observability & Logging

### ObservabilityManager - Centralized Logging

**Location**: `com.acmeai.xynoptik.api.integration.automation.observability.ObservabilityManager`

All logging is standardized through ObservabilityManager. Logs follow the format:

```
[Observability] HH:MM:SS.mmm [EVENT_TYPE] event_details
```

### Event Types

| Event | Format | Example |
|-------|--------|---------|
| TEST_START | [TEST_START] Class.method | [TEST_START] ApiValidationTest.uploadDocument_valid_shouldSucceed |
| TEST_END | [TEST_END] Class.method - Status | [TEST_END] ApiValidationTest.uploadDocument_valid_shouldSucceed - Status: PASSED |
| REQUEST | [REQUEST] METHOD endpoint | [REQUEST] POST /api/documents |
| RESPONSE | [RESPONSE] Status: code, Body: bytes | [RESPONSE] Status: 200, Body: 1024 bytes |
| RUNNING | [RUNNING] Class.method | [RUNNING] ApiValidationTest.uploadDocument_valid_shouldSucceed |
| SKIPPED | [SKIPPED] Class.method - Reason | [SKIPPED] ApiValidationTest.integrationTest - Reason: CI POLICY |
| RETRY | [RETRY] METHOD endpoint (TYPE) | [RETRY] POST /api/documents (SERVER_FAILURE) |
| ASSERTION_FAILURE | [ASSERTION_FAILURE] message | [ASSERTION_FAILURE] Status Code: Expected 200, Actual 404 |
| SERVER_FAILURE | [SERVER_FAILURE] message | [SERVER_FAILURE] POST /api/documents returned 500 |
| NETWORK_FAILURE | [NETWORK_FAILURE] message | [NETWORK_FAILURE] POST /api/documents - Connect timeout |
| PROFILE | [PROFILE] message | [PROFILE] Environment: qa | Base URL: http://localhost:8000 |

### Failure Classification

The framework automatically classifies failures:

1. **ASSERTION_FAILURE**: Test assertion failed
   - Status code mismatch
   - Field value mismatch
   - Content validation failed
   - Logged by: AssertionUtil

2. **SERVER_FAILURE**: API returned 5xx (500-599)
   - Automatically triggers retry
   - Logged by: ApiClient
   - Retried up to `retry.count` times

3. **NETWORK_FAILURE**: Connection/network error
   - Timeout, connection refused, etc.
   - Automatically triggers retry
   - Logged by: ApiClient
   - Retried up to `retry.count` times

---

## Examining Logs

### View Test Output
```bash
# Run tests with detailed logging
./gradlew clean test -Dgroups=smoke --info
```

### View Allure Report
```bash
# Generate Allure report (runs automatically after tests)
./gradlew generateSingleAllureHtml

# Open the report
# File: build/allure-report/LegalDocumentSearch-ApiAutomationReport.html
```

### Grep Logs
```bash
# Find specific events in logs
grep "\[REQUEST\]" build/test-results/*.log
grep "\[RESPONSE\]" build/test-results/*.log
grep "\[FAILURE\]" build/test-results/*.log
```

---

## Configuration

### config.properties

**Location**: `src/test/resources/config.properties`

```properties
# Current environment (default: dev)
env=dev

# Base URLs for each environment
dev.base.url=http://localhost:8000
qa.base.url=http://localhost:8000
prod.base.url=http://localhost:8000

# Timeout settings (milliseconds)
connection.timeout=5000
read.timeout=10000

# Retry settings
retry.count=2
retry.delay=1000
```

### System Properties (Runtime Override)

Override config.properties with system properties:

```bash
# Override environment
./gradlew clean test -Denv=qa

# Override groups
./gradlew clean test -Dgroups=smoke

# Enable CI mode
./gradlew clean test -DCI=true

# Combine multiple properties
./gradlew clean test -DCI=true -Denv=qa -Dgroups=regression
```

---

## Test Writing Guide

### Writing Tests with Groups

```java
@Test(description = "Verify successful upload", groups = {"smoke", "regression"})
public void uploadDocument_valid_shouldSucceed() {
    // This test runs in:
    // - smoke group
    // - regression group
    // - CI mode (smoke + regression)
    // - All groups mode (-Dgroups=all)
}

@Test(description = "Verify integration", groups = {"integration"})
public void someIntegrationTest() {
    // This test runs ONLY when:
    // - -Dgroups=integration
    // - -Dgroups=all (and CI=false)
    // 
    // This test is SKIPPED when:
    // - -DCI=true (CI policy skips integration)
    // - -Dgroups=smoke
    // - -Dgroups=regression
}

@Test(description = "Verify error handling")
public void errorHandling() {
    // This test runs in ALL modes:
    // - No groups specified = always runs
    // - Except when explicitly filtering by groups
}
```

### Using Assertions with Observability

```java
@Test(groups = {"smoke"})
public void testApi() {
    // ... setup code ...

    ResponseWrapper response = apiClient.request(...);

    // These assertions automatically log failures
    AssertionUtil.assertStatusCode(response, 200);
    // Logs: [ASSERTION_FAILURE] Status Code: Expected 200, Actual 404

    AssertionUtil.assertFieldEquals(response, "success", true);
    // Logs: [ASSERTION_FAILURE] Field: success - Expected true, Actual false

    AssertionUtil.assertBodyContains(response, "expected_text");
    // Logs: [ASSERTION_FAILURE] Body Contains - Expected "expected_text" but not found
}
```

---

## Troubleshooting

### Tests Not Running in CI Mode

**Problem**: Integration tests still run when `-DCI=true`

**Solution**: Ensure test has `groups = {"integration"}`
```java
@Test(groups = {"integration"})  // ✅ Correct - will be skipped in CI
public void integrationTest() { }

@Test  // ❌ Wrong - will still run in CI
public void integrationTest() { }
```

### Environment Not Switching

**Problem**: Tests still use dev URL even with `-Denv=qa`

**Solution**: Verify config.properties has all environment URLs defined
```properties
dev.base.url=http://localhost:8000   # ✅ Must be defined
qa.base.url=http://localhost:8000    # ✅ Must be defined
prod.base.url=http://localhost:8000  # ✅ Must be defined
```

### Logs Not Showing Up

**Problem**: Can't find observability logs

**Solution**: Logs are in Java Logger format. Check:
1. Console output during test execution
2. Allure reports (embed in test attachments)
3. Build logs: `./gradlew clean test 2>&1 | grep "\[Observability\]"`

---

## Common Scenarios

### Scenario 1: Local Development - All Tests with Dev Environment
```bash
./gradlew clean test -Denv=dev
# Runs all smoke, regression, and integration tests with dev URL
```

### Scenario 2: CI/CD Pipeline - Smoke & Regression with QA
```bash
./gradlew clean test -DCI=true -Denv=qa
# Runs only smoke + regression tests with QA URL
# Integration tests skipped automatically
```

### Scenario 3: Pre-Release Testing - All Tests with Prod
```bash
./gradlew clean test -Dgroups=all -Denv=prod
# Runs all tests (smoke, regression, integration) with prod URL
```

### Scenario 4: Smoke Testing - Quick Validation in QA
```bash
./gradlew clean test -Dgroups=smoke -Denv=qa
# Quick smoke test suite against QA environment
```

### Scenario 5: Regression Testing - Full Suite with Dev
```bash
./gradlew clean test -Dgroups=regression -Denv=dev
# Full regression suite with dev environment
```

---

## Performance Tips

### Faster Builds
```bash
# Skip report generation (Allure report)
./gradlew clean test -Dgroups=smoke -x generateSingleAllureHtml
```

### Parallel Execution
Tests are already configured for parallel execution (3 threads). To adjust:

Edit `src/test/resources/testng.xml`:
```xml
<suite name="ApiAutomationSuite" verbose="1" parallel="methods" thread-count="5">
    <!-- Increase thread-count for faster execution -->
</suite>
```

### Monitor Execution
```bash
# Watch test execution with verbose output
./gradlew clean test -Dgroups=smoke --info
```

---

## Support & Documentation

### Key Files
- **Implementation**: `ENTERPRISE_UPGRADES_IMPLEMENTATION.md`
- **Validation**: `VALIDATION_REPORT.md`
- **This Guide**: `QUICK_REFERENCE.md`

### Source Code Locations
- ExecutionController: `src/test/java/com/acmeai/xynoptik/api/integration/automation/config/ExecutionController.java`
- ConfigManager: `src/test/java/com/acmeai/xynoptik/api/integration/automation/config/ConfigManager.java`
- ExecutionInterceptor: `src/test/java/com/acmeai/xynoptik/api/integration/automation/config/ExecutionInterceptor.java`
- ObservabilityManager: `src/test/java/com/acmeai/xynoptik/api/integration/automation/observability/ObservabilityManager.java`
- ApiClient: `src/test/java/com/acmeai/xynoptik/api/integration/automation/client/ApiClient.java`
- AssertionUtil: `src/test/java/com/acmeai/xynoptik/api/integration/automation/utils/AssertionUtil.java`

### Configuration
- Config File: `src/test/resources/config.properties`
- TestNG Suite: `src/test/resources/testng.xml`
- Build Config: `build.gradle`

---

## Cheat Sheet

| Goal | Command |
|------|---------|
| Run all tests | `./gradlew clean test` |
| Smoke tests | `./gradlew clean test -Dgroups=smoke` |
| Regression tests | `./gradlew clean test -Dgroups=regression` |
| CI mode | `./gradlew clean test -DCI=true` |
| QA environment | `./gradlew clean test -Denv=qa` |
| Dev environment | `./gradlew clean test -Denv=dev` |
| Prod environment | `./gradlew clean test -Denv=prod` |
| CI + QA | `./gradlew clean test -DCI=true -Denv=qa` |
| Smoke + Dev | `./gradlew clean test -Dgroups=smoke -Denv=dev` |
| Generate report | `./gradlew generateSingleAllureHtml` |

---

**Last Updated**: May 5, 2026
**Framework Version**: 1.0-UpgradesComplete
**Status**: Production Ready

