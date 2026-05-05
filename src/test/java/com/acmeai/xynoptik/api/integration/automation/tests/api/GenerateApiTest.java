package com.acmeai.xynoptik.api.integration.automation.tests.api;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.client.ResponseWrapper;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.testdata.TestDataFactory;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import com.acmeai.xynoptik.api.integration.automation.utils.StepLogger;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class GenerateApiTest extends BaseTest {

    private final GenerateService generateService = new GenerateService();

    // =========================================================
    // TC-GEN-01: VALID GENERATE API
    // =========================================================
    @Test(description = "Verify generate API returns successful response for valid request", groups = {"smoke", "regression"})
    @Description("Validates core generate API functionality and response structure")
    public void generateShouldReturn200() {

        // Arrange
        StepLogger.step("Prepare valid generate request");
        GenerateRequest request = TestDataFactory.validGenerateQuery();

        // Act
        StepLogger.step("Execute generate API call");
        ResponseWrapper response = generateService.generate(request);

        // Assert (contract validation)
        StepLogger.step("Validate successful response");
        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertFieldEquals(response, "success", true);
        AssertionUtil.assertFieldEquals(response, "status", 200);
        AssertionUtil.assertNotNull(response.getBodyAsString(), "Response body should not be null");
        AssertionUtil.assertNotEmpty(response.getBodyAsString(), "Response body should not be empty");

        // Schema validation (contract-driven testing)
        response.validateSchema("generate-schema.json");
    }
}