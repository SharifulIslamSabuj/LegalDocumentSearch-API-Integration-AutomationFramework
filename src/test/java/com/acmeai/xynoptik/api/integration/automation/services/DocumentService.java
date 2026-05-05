package com.acmeai.xynoptik.api.integration.automation.services;

import com.acmeai.xynoptik.api.integration.automation.client.ApiClient;
import com.acmeai.xynoptik.api.integration.automation.client.ApiRoutes;
import com.acmeai.xynoptik.api.integration.automation.client.ResponseWrapper;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;
import io.restassured.http.Method;

public class DocumentService {

    // =========================
    // ENDPOINT (CENTRALIZED)
    // =========================
    private static final String BASE_ENDPOINT = ApiRoutes.UPLOAD;

    // =========================
    // PRIMARY METHOD
    // =========================
    public ResponseWrapper upload(UploadDocumentRequest request) {
        return ApiClient.request(Method.POST, BASE_ENDPOINT, request, null);
    }
}