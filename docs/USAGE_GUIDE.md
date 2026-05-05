# Usage Guide

This guide provides practical steps for writing tests and integrating new APIs into the framework.

---

## Writing Your First Test

### Pattern: Arrange → Act → Assert

All tests follow this structure:

```java
@Test(description = "Clear description of what you are testing", groups = {"smoke"})
@Description("Business context and expected behavior")
public void testName_scenario_expectedResult() {
    // ARRANGE: Setup test data & dependencies
    StepLogger.step("Prepare test data");
    GenerateRequest request = TestDataFactory.generateValidGenerateRequest();
    
    // ACT: Call service layer
    StepLogger.step("Call API endpoint");
    ResponseWrapper response = GenerateService.generateDocument(request);
    
    // ASSERT: Validate response
    StepLogger.step("Validate response structure");
    AssertionUtil.assertSuccess(response, "Document generation should succeed");
    
    StepLogger.step("Validate response schema");
    response.validateSchema("generate-schema.json");
    
    StepLogger.step("Validate business logic");
    AssertionUtil.assertFieldEquals(response.getData().get("status"), "completed", 
                                   "Document status should be completed");
}
```

### Key Points

✅ Use `@Test(groups = {...})` to categorize tests  
✅ Use `@Description(...)` for business context  
✅ Use `StepLogger.step(...)` for readable Allure steps  
✅ Use services, NEVER call `ApiClient` directly  
✅ Always validate schema at the end  
✅ Use `TestDataFactory` for all test data  
✅ Use `AssertionUtil` for all assertions  

---

## Test Groups

### Smoke Tests (`groups = {"smoke"}`)
- **Purpose:** Critical path validation
- **Run:** `./gradlew clean test -Dgroups=smoke`
- **Examples:** Login, generate document, upload document
- **Rule:** Must pass before any code merge

### Regression Tests (`groups = {"regression"}`)
- **Purpose:** Full feature coverage
- **Run:** `./gradlew clean test -Dgroups=regression`
- **Examples:** Edge cases, boundary conditions, error scenarios
- **Rule:** Run daily in CI/CD

### Integration Tests (`groups = {"integration"}`)
- **Purpose:** End-to-end workflows
- **Run:** `./gradlew clean test -Dgroups=integration`
- **Examples:** Multi-step flows (generate → upload → verify)
- **Rule:** Skipped in CI mode by default (use `-DCI=false` to include)

### Test Group Example
```java
@Test(groups = {"smoke", "regression"})  // Part of both groups
public void testName() { }

@Test(groups = {"integration"})           // Only integration
public void workflowTest() { }
```

---

## Adding a New API Endpoint

### Step 1: Add Route in `ApiRoutes`
```java
public static final String GENERATE_ENDPOINT = "/api/v1/generate";
public static final String STATUS_ENDPOINT = "/api/v1/status/{documentId}";
```

### Step 2: Create Request/Response Models
```java
// src/test/java/.../models/StatusRequest.java
@Getter
@Setter
public class StatusRequest {
    private String documentId;
}

// src/test/java/.../models/StatusResponse.java
@Getter
@Setter
public class StatusResponse {
    private String status;
    private String message;
}
```

### Step 3: Add Service Method
```java
// src/test/java/.../services/DocumentService.java
public static ResponseWrapper getStatus(String documentId) {
    return ApiClient.get(
        ApiRoutes.STATUS_ENDPOINT.replace("{documentId}", documentId),
        null
    );
}
```

### Step 4: Add Schema Definition
```json
// src/test/resources/schemas/status-schema.json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["status", "message"],
  "properties": {
    "status": { "type": "string" },
    "message": { "type": "string" }
  }
}
```

### Step 5: Write Tests
```java
@Test(description = "Retrieve document status", groups = {"regression"})
public void getStatus_validId_shouldReturnStatus() {
    StepLogger.step("Create document first");
    GenerateRequest request = TestDataFactory.generateValidGenerateRequest();
    ResponseWrapper generateResponse = GenerateService.generateDocument(request);
    String documentId = generateResponse.getData().get("documentId");
    
    StepLogger.step("Get status");
    ResponseWrapper response = DocumentService.getStatus(documentId);
    
    StepLogger.step("Validate response");
    AssertionUtil.assertSuccess(response);
    response.validateSchema("status-schema.json");
    AssertionUtil.assertFieldEquals(
        response.getData().get("status"), 
        "completed"
    );
}
```

---

## Test Data Generation

### Using `TestDataFactory`

```java
// Valid data (normal workflow)
GenerateRequest request = TestDataFactory.generateValidGenerateRequest();

// Boundary cases
String emptyInput = TestDataFactory.generateEmptyStringInput();
int nullRequest = TestDataFactory.generateNullRequest();

// Invalid data (error scenarios)
String longString = TestDataFactory.generateLongStringInput();
String specialChars = TestDataFactory.generateSpecialCharacterInput();

// Edge cases
String unicode = TestDataFactory.generateUnicodeInput();
String maxSize = TestDataFactory.generateMaxSizeInput();
```

### No Hardcoding Data
❌ Bad:
```java
GenerateRequest request = new GenerateRequest();
request.setDocumentType("pdf");  // Hardcoded
```

✅ Good:
```java
GenerateRequest request = TestDataFactory.generateValidGenerateRequest();
// TestDataFactory handles all values
```

---

## Assertions

### Using `AssertionUtil`

```java
// Assert success
AssertionUtil.assertSuccess(response);

// Assert error with custom message
AssertionUtil.assertError(response, "Should fail with invalid input");

// Assert specific status code
AssertionUtil.assertStatusCode(response, 400);

// Assert field value
AssertionUtil.assertFieldEquals(response.getData().get("status"), "completed");

// Assert field not empty
AssertionUtil.assertNotEmpty(response.getData().get("documentId"));

// Assert field not null
AssertionUtil.assertNotNull(response.getData(), "Data should not be null");

// Assert field contains value
AssertionUtil.assertBodyContains(response.getBodyAsString(), "error");
```

### Schema Validation

```java
// Always validate schema at end of test
response.validateSchema("generate-schema.json");

// Returns: JsonSchemaValidator validates response against schema
// Throws: AssertionError if validation fails
// Attached to: Allure report (schema validation details)
```

---

## Configuration

### Environment Switching
```bash
# Default: config.properties (env=qa)
./gradlew clean test

# Override: Use CLI
./gradlew clean test -Denv=dev
./gradlew clean test -Denv=prod
```

### Modify Timeouts
Edit `src/test/resources/config.properties`:
```properties
connection.timeout=5000   # Connection timeout
read.timeout=10000        # Read timeout
```

### Retry Configuration
```properties
retry.count=2              # Number of retries
retry.delay=1000           # Delay between retries (ms)
```

---

## Best Practices

### ✅ DO

- Keep tests focused (one scenario per test)
- Use meaningful test names (`what_when_then`)
- Add `@Description` for business context
- Always use service layer
- Always validate schema
- Use `StepLogger.step()` for readability
- Keep test data in `TestDataFactory`
- Use `AssertionUtil` for consistency

### ❌ DON'T

- Hardcode values in tests
- Call `ApiClient` directly
- Put assertions in services
- Skip schema validation
- Use generic test names like `test1()` or `apiTest()`
- Create new assertion methods (use `AssertionUtil`)
- Reuse test data across tests without resetting state
- Ignore failed assertions (investigate immediately)

---

## Viewing Test Reports

After running tests, open the Allure report:

### Generate Report
```bash
./gradlew clean test
```

### View Report
```bash
build/allure-report/index.html
```

### Report Includes
- Execution timeline
- API request/response bodies
- Step-by-step execution logs
- Pass/fail breakdown by group
- Test history trends

---

## Parallel Test Execution

Tests run in parallel by default (configured in `testng.xml`).

### Thread Safety Guarantees
- Each test gets isolated thread
- `Faker` instances are ThreadLocal
- No shared mutable state
- `ResponseWrapper` responses are test-isolated

### Running Sequentially (if needed)
Edit `testng.xml`:
```xml
<suite parallel="tests" thread-count="1">
```

---

## Debugging Failed Tests

### 1. Check Allure Report
```bash
build/allure-report/index.html
```
- View exact assertion failure
- Check API request/response bodies
- Review step-by-step logs

### 2. Check Console Output
```bash
./gradlew clean test 2>&1 | grep -i "error\|failed"
```

### 3. Check Response Body
Add temporary step:
```java
StepLogger.step("Response: " + response.getBodyAsString());
```

### 4. Verify Environment
```bash
./gradlew clean test -Denv=dev  # Test in different environment
```

---

## Common Issues

### Issue: "Schema validation failed"
**Solution:** Update schema file in `src/test/resources/schemas/` to match API contract

### Issue: "Connection timeout"
**Solution:** Increase `connection.timeout` in `config.properties`

### Issue: "Assertion failed: Field not found"
**Solution:** Check API response structure using Allure report

### Issue: "Test skipped in CI mode"
**Solution:** Integration tests are skipped by default with `-DCI=true`. Use `groups=smoke,regression` for CI

---

## Quick Reference

| Command | Purpose |
|---------|---------|
| `./gradlew clean test` | Run all tests |
| `./gradlew clean test -Dgroups=smoke` | Run smoke tests |
| `./gradlew clean test -Dgroups=integration` | Run integration tests |
| `./gradlew clean test -DCI=true` | Run in CI mode |
| `./gradlew clean test -Denv=qa` | Run against QA |

---

## Need Help?

- Check existing tests in `src/test/java/.../tests/` for patterns
- Review `TestDataFactory` for available test data
- Check `AssertionUtil` for available assertion methods
- Consult [ARCHITECTURE_DIAGRAMS.md](../ARCHITECTURE_DIAGRAMS.md) for design overview

