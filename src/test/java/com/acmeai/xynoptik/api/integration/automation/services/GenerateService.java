package com.acmeai.xynoptik.api.integration.automation.services;

import com.acmeai.xynoptik.api.integration.automation.client.ApiClient;
import com.acmeai.xynoptik.api.integration.automation.client.ApiRoutes;
import com.acmeai.xynoptik.api.integration.automation.client.ResponseWrapper;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import io.restassured.http.Method;

public class GenerateService {

    // =========================
    // ENDPOINTS (CENTRALIZED)
    // =========================
    private static final String BASE_ENDPOINT = ApiRoutes.GENERATE;

    // =========================
    // PRIMARY ACTION (POST)
    // =========================
    public ResponseWrapper generate(GenerateRequest request) {
        return ApiClient.request(Method.POST, BASE_ENDPOINT, request, null);
    }

    // =========================
    // OPTIONAL QUERY SUPPORT (GET)
    // =========================
    public ResponseWrapper getGeneratedData() {
        return ApiClient.request(Method.GET, BASE_ENDPOINT, null, null);
    }
}