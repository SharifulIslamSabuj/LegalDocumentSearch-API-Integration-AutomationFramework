package com.acmeai.xynoptik.api.integration.automation.tests.integration;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.client.ResponseWrapper;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.testdata.TestDataFactory;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import com.acmeai.xynoptik.api.integration.automation.utils.StepLogger;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class GenerateWithoutUploadTest extends BaseTest {

    private final GenerateService generateService = new GenerateService();

    @Test(description = "Generate API should return valid response without document upload", groups = {"smoke", "regression"})
    @Description("Validates that generate API works independently and returns proper response structure")
    public void generateWithoutUpload_shouldReturnSuccessResponse() {

        // Arrange
        StepLogger.step("Prepare valid generate request");
        GenerateRequest request = TestDataFactory.validGenerateQuery();

        // Act
        StepLogger.step("Execute generate API call");
        ResponseWrapper response = generateService.generate(request);

        // Assert
        StepLogger.step("Validate successful response");
        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertNotEmpty(response.getBodyAsString(),
                "Response body should not be empty");

        // Contract validation
        response.validateSchema("generate-schema.json");
    }
}