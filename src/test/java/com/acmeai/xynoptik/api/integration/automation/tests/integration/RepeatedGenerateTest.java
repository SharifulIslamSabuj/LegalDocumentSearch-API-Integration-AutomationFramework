package com.acmeai.xynoptik.api.integration.automation.tests.integration;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.client.ResponseWrapper;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.testdata.TestDataFactory;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import org.testng.annotations.Test;

public class RepeatedGenerateTest extends BaseTest {

    private final GenerateService generateService = new GenerateService();

    @Test(description = "Verify generate API stability under repeated execution", groups = {"integration", "regression"})
    public void repeatedGenerateCalls_shouldRemainStable() {

        // Arrange
        GenerateRequest request = TestDataFactory.validGenerateQuery();

        // Act & Assert (Stability Check)
        for (int i = 1; i <= 5; i++) {

            ResponseWrapper response = generateService.generate(request);

            AssertionUtil.assertStatusCode(response, 200);

            AssertionUtil.assertFieldEquals(response, "success", true);
            AssertionUtil.assertFieldEquals(response, "status", 200);

            AssertionUtil.assertNotEmpty(
                    response.getBodyAsString(),
                    "Iteration " + i + ": response should not be empty"
            );

            // Schema validation
            response.validateSchema("generate-schema.json");
        }
    }
}