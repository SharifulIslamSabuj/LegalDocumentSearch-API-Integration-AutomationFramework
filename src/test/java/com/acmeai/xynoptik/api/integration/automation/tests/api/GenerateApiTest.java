package com.acmeai.xynoptik.api.integration.automation.tests.api;

import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class GenerateApiTest {

    private final GenerateService generateService = new GenerateService();

    @Test(groups = {"api"})
    public void generateShouldReturn200() {

        GenerateRequest request = new GenerateRequest();
        request.setQuery("What is law?");

        Response response = generateService.generateResponse(request);

        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertNotNull(response.asString(), "Response should not be null");
    }
}