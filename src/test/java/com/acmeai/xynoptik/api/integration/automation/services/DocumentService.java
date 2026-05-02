package com.acmeai.xynoptik.api.integration.automation.services;

import com.acmeai.xynoptik.api.integration.automation.client.ApiClient;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;
import io.restassured.response.Response;

public class DocumentService {

    //Primary clean method (recommended)
    public Response upload(UploadDocumentRequest request) {
        return ApiClient.post("/upload", request);
    }

    //Backward compatibility (for existing tests)
    public Response uploadDocument(UploadDocumentRequest request) {
        return upload(request);
    }
}