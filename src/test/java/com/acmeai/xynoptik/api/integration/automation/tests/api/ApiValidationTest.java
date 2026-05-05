package com.acmeai.xynoptik.api.integration.automation.tests.api;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.client.ResponseWrapper;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;
import com.acmeai.xynoptik.api.integration.automation.services.DocumentService;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.testdata.TestDataFactory;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import com.acmeai.xynoptik.api.integration.automation.utils.StepLogger;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class ApiValidationTest extends BaseTest {

    private final DocumentService documentService = new DocumentService();
    private final GenerateService generateService = new GenerateService();

    // =========================================================
    // TC-API-01: VALID DOCUMENT UPLOAD
    // =========================================================
    @Test(description = "Verify successful document upload with valid payload", groups = {"smoke", "regression"})
    @Description("Validates successful document upload and proper API response structure")
    public void uploadDocument_valid_shouldSucceed() {

        // Arrange
        StepLogger.step("Prepare valid document upload request");
        UploadDocumentRequest request = TestDataFactory.validUploadDocument();

        // Act
        StepLogger.step("Execute document upload API call");
        ResponseWrapper response = documentService.upload(request);

        // Assert (contract-level validation)
        StepLogger.step("Validate upload response");
        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertFieldEquals(response, "success", true);
        AssertionUtil.assertFieldEquals(response, "status", 200);
        AssertionUtil.assertNotEmpty(response.getBodyAsString(), "Response body should not be empty");

        // Contract validation
        response.validateSchema("upload-schema.json");
    }

    // =========================================================
    // TC-API-02: INVALID DOCUMENT (MISSING CONTENT)
    // =========================================================
    @Test(description = "Verify validation error when required field is missing", groups = {"regression"})
    @Description("Validates API error handling for missing required fields")
    public void uploadDocument_missingField_shouldFail() {

        // Arrange
        StepLogger.step("Prepare invalid upload request with missing content");
        UploadDocumentRequest request = TestDataFactory.invalidUploadMissingContent();

        // Act
        StepLogger.step("Execute invalid upload API call");
        ResponseWrapper response = documentService.upload(request);

        // Assert (contract-safe validation)
        StepLogger.step("Validate error response");
        AssertionUtil.assertStatusCode(response, 422);
        AssertionUtil.assertBodyContains(response, "detail");
    }

    // =========================================================
    // TC-API-03: VALID GENERATE REQUEST
    // =========================================================
    @Test(description = "Verify generate API returns valid response for valid query", groups = {"smoke", "regression"})
    @Description("Validates successful generation with proper response structure")
    public void generate_validQuery_shouldWork() {

        // Arrange
        StepLogger.step("Prepare valid generate request");
        GenerateRequest request = TestDataFactory.validGenerateQuery();

        // Act
        StepLogger.step("Execute generate API call");
        ResponseWrapper response = generateService.generate(request);

        // Assert
        StepLogger.step("Validate generate response");
        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertFieldEquals(response, "success", true);
        AssertionUtil.assertFieldEquals(response, "status", 200);
        AssertionUtil.assertNotEmpty(response.getBodyAsString(), "Response should not be empty");

        // Contract validation
        response.validateSchema("generate-schema.json");
    }

    // =========================================================
    // TC-API-04: EMPTY GENERATE QUERY
    // =========================================================
    @Test(description = "Verify validation error for empty query payload", groups = {"regression"})
    @Description("Validates API error handling for empty input data")
    public void generate_emptyQuery_shouldReturnError() {

        // Arrange
        StepLogger.step("Prepare empty generate request");
        GenerateRequest request = TestDataFactory.emptyGenerateQuery();

        // Act
        StepLogger.step("Execute generate API call with empty query");
        ResponseWrapper response = generateService.generate(request);

        // Assert (important: API returns 200 with error payload)
        StepLogger.step("Validate error response for empty query");
        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertFieldEquals(response, "success", false);
        AssertionUtil.assertFieldEquals(response, "status", 400);
        AssertionUtil.assertBodyContains(response, "Please provide a search query");

        // Contract validation
        response.validateSchema("generate-schema.json");
    }
}