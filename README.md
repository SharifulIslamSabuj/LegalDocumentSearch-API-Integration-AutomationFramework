# API Automation Framework

A robust, enterprise-grade API automation testing framework built with **Java**, **TestNG**, **RestAssured**, and **Allure** for reliable and scalable test execution.

## Overview

This framework provides a comprehensive solution for testing REST APIs with support for multiple environments, intelligent retry mechanisms, parallel execution, and contract-based validation. It's designed for teams building enterprise automation strategies with CI/CD integration.

**Core Stack:**
- **Java 11+** — Programming language
- **TestNG** — Test framework with grouping & parallel execution
- **RestAssured** — HTTP client & API assertions
- **Allure** — Test reporting & observability
- **Gradle** — Build automation

## Key Features

✅ **Multi-Environment Support** — Dev, QA, Prod with isolated configuration  
✅ **Retry Mechanism** — Automatic failure recovery (configurable attempts & delays)  
✅ **Parallel Execution** — Safe concurrent test runs with ThreadLocal state management  
✅ **Test Grouping** — Smoke, Regression, Integration for selective execution  
✅ **Schema Validation** — Centralized JSON Schema contract testing  
✅ **Centralized API Client** — Single unified endpoint for all HTTP operations  
✅ **Professional Logging** — Standardized request/response logging with Allure attachments  
✅ **Execution Control** — CI-aware test filtering & group management  

## Project Structure

```
src/test/java/.../automation/
│
├── base/              → BaseTest superclass with common setup
├── client/            → ApiClient, ApiRoutes, ResponseWrapper, RequestSpecBuilder
├── config/            → ConfigManager, ExecutionController, ExecutionInterceptor
├── filters/           → ApiLoggingFilter (request/response logging)
├── models/            → Request/Response POJOs
├── services/          → Business logic for API operations (GenerateService, DocumentService)
├── schema/            → JSON schema definitions & validation utilities
├── testdata/          → TestDataFactory, RandomDataUtil
├── tests/             → Test classes organized by domain (api/, integration/)
└── utils/             → AssertionUtil, StepLogger, utilities
```

## Running Tests

### Default (all tests)
```bash
./gradlew clean test
```

### By Test Group
```bash
# Smoke tests (critical path)
./gradlew clean test -Dgroups=smoke

# Regression tests (full coverage)
./gradlew clean test -Dgroups=regression

# Integration tests (end-to-end flows)
./gradlew clean test -Dgroups=integration
```

### By Environment
```bash
./gradlew clean test -Denv=dev    # Development
./gradlew clean test -Denv=qa     # QA
./gradlew clean test -Denv=prod   # Production
```

### CI Mode (excludes integration tests)
```bash
./gradlew clean test -DCI=true
```

### Combined
```bash
./gradlew clean test -Dgroups=smoke -Denv=qa
./gradlew clean test -DCI=true -Dgroups=regression -Denv=prod
```

## Getting Started

### Requirements
- **Java 11+** (recommended: Java 11 LTS or Java 17+)
- **Gradle** (included via wrapper: `./gradlew`)

### Quick Start
```bash
# Clone repository
git clone <repository-url>
cd LegalDocumentSearch-API-Integration-AutomationFramework

# Run all tests using Gradle wrapper
./gradlew clean test

# View Allure report
open build/allure-report/index.html
```

The Gradle wrapper (`gradlew` / `gradlew.bat`) is pre-configured and requires **no additional setup**. It automatically downloads the correct Gradle version.

## Viewing Test Reports

After test execution, open the Allure report:
```bash
build/allure-report/index.html
```

Reports include:
- Test execution timeline
- Request/response attachments
- Step-by-step execution logs
- Pass/fail statistics by group

## Execution Strategy

The framework uses **ExecutionController** as the single source of truth for test execution decisions. It dynamically filters tests based on runtime parameters.

### Default Behavior
- **Local execution** (no flags): All tests execute (smoke + regression + integration)
- **CI mode** (-DCI=true): Smoke + regression tests execute; integration tests are skipped

### Test Group Control
Use `-Dgroups` to run specific test categories:

```bash
# Smoke tests only (critical path validation)
./gradlew clean test -Dgroups=smoke

# Regression tests only (full feature coverage)
./gradlew clean test -Dgroups=regression

# Integration tests only (end-to-end workflows)
./gradlew clean test -Dgroups=integration
```

### CI/CD Pipeline Execution
```bash
# Standard CI run: smoke + regression (fastest)
./gradlew clean test -DCI=true

# CI with integration tests (optional for nightly/weekly)
./gradlew clean test -DCI=true -Dgroups=smoke,regression,integration

# Combined: specific group + environment + CI mode
./gradlew clean test -DCI=true -Dgroups=regression -Denv=qa
```

## Configuration

Modify `src/test/resources/config.properties`:

```properties
# Environment: dev, qa, prod
env=qa

# Base URLs per environment
dev.base.url=http://localhost:8000
qa.base.url=https://qa-api.example.com
prod.base.url=https://api.example.com

# Connection/Read timeouts (milliseconds)
connection.timeout=5000
read.timeout=10000

# Retry mechanism
retry.count=2
retry.delay=1000
```

## Core Rules

🚫 **No API calls in tests** — Use service layer  
🚫 **No assertions in services** — Only in test classes  
🚫 **No hardcoded values** — Use TestDataFactory  
✅ **Always validate schema** — Call `response.validateSchema("endpoint-schema.json")`  
✅ **Always use AssertionUtil** — For consistent error reporting  

## Next Steps

- Read [USAGE_GUIDE.md](docs/USAGE_GUIDE.md) for detailed examples
- Check [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for common patterns
- Review [ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md) for design decisions

## Support

For issues or questions, refer to the framework documentation or contact the QA automation team.

