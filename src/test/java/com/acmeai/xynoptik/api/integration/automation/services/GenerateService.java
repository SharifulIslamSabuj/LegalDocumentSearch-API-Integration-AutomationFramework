package com.acmeai.xynoptik.api.integration.automation.services;

import com.acmeai.xynoptik.api.integration.automation.client.ApiClient;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import io.restassured.response.Response;

public class GenerateService {

    //Primary clean method (recommended)
    public Response generate(GenerateRequest request) {
        return ApiClient.post("/generate", request);
    }

    //Backward compatibility (for existing tests)
    public Response generateResponse(GenerateRequest request) {
        return generate(request);
    }

    //GET endpoint support
    public Response getGenerate() {
        return ApiClient.get("/generate");
    }
}