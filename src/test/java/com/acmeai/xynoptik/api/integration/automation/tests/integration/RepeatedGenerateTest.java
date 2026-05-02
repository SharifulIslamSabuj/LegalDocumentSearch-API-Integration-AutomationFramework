package com.acmeai.xynoptik.api.integration.automation.tests.integration;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import org.testng.annotations.Test;

public class RepeatedGenerateTest extends BaseTest {

    private final GenerateService generateService = new GenerateService();

    @Test
    public void repeatedCallsShouldBeStable() {

        GenerateRequest request = new GenerateRequest("What is law?");

        for (int i = 0; i < 5; i++) {

            var response = generateService.generate(request);

            AssertionUtil.assertStatusCode(response, 200);
            AssertionUtil.assertNotNull(response.asString(),
                    "Response should be consistent");
        }
    }
}