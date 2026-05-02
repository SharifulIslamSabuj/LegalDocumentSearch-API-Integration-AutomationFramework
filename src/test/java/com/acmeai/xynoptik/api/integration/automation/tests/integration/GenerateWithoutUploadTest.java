package com.acmeai.xynoptik.api.integration.automation.tests.integration;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import org.testng.annotations.Test;

public class GenerateWithoutUploadTest extends BaseTest {

    private final GenerateService generateService = new GenerateService();

    @Test(description = "Generate without upload")
    public void generateWithoutUploadShouldReturn200() {

        var response = generateService.generate(
                new GenerateRequest("What is law?")
        );

        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertNotNull(response.asString(), "Response should not be null");
    }
}