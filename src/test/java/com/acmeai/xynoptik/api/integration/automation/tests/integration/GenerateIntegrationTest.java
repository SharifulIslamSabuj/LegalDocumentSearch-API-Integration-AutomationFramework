package com.acmeai.xynoptik.api.integration.automation.tests.integration;

import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;
import com.acmeai.xynoptik.api.integration.automation.services.DocumentService;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GenerateIntegrationTest {

    private final DocumentService documentService = new DocumentService();
    private final GenerateService generateService = new GenerateService();

    @Test(description = "Upload → Generate → Validate response stability")
    public void shouldGenerateResponseFromUploadedDocument() {

        UploadDocumentRequest uploadRequest = new UploadDocumentRequest();
        uploadRequest.setId(101);
        uploadRequest.setTitle("Contract Law");
        uploadRequest.setContent("A contract is a legally binding agreement.");

        Response uploadResponse = documentService.uploadDocument(uploadRequest);
        uploadResponse.then().statusCode(200);

        GenerateRequest generateRequest = new GenerateRequest();
        generateRequest.setQuery("What is a contract?");

        Response generateResponse = generateService.generateResponse(generateRequest);
        generateResponse.then().statusCode(200);

        String responseBody = generateResponse.asString();

        Assert.assertNotNull(responseBody, "Response should not be null");
        Assert.assertTrue(responseBody.trim().length() > 0,
                "Response should not be empty");
    }

    @Test(description = "Generate without upload should still return response")
    public void shouldHandleGenerateWithoutUpload() {

        GenerateRequest request = new GenerateRequest();
        request.setQuery("What is law?");

        Response response = generateService.generateResponse(request);

        response.then().statusCode(200);

        Assert.assertTrue(response.asString().length() > 0);
    }

    @Test(description = "Multiple uploads should not break system")
    public void shouldHandleMultipleUploads() {

        UploadDocumentRequest doc1 = new UploadDocumentRequest();
        doc1.setId(1);
        doc1.setTitle("Law A");
        doc1.setContent("Law governs society.");
        documentService.uploadDocument(doc1).then().statusCode(200);

        UploadDocumentRequest doc2 = new UploadDocumentRequest();
        doc2.setId(2);
        doc2.setTitle("Law B");
        doc2.setContent("Contracts are binding.");
        documentService.uploadDocument(doc2).then().statusCode(200);

        GenerateRequest request = new GenerateRequest();
        request.setQuery("Explain law");

        Response response = generateService.generateResponse(request);

        response.then().statusCode(200);

        Assert.assertTrue(response.asString().length() > 0);
    }

    @Test(description = "Repeated generate calls should be stable")
    public void shouldHandleSequentialGenerateCalls() {

        GenerateRequest request = new GenerateRequest();
        request.setQuery("What is legal document?");

        for (int i = 0; i < 5; i++) {

            Response response = generateService.generateResponse(request);

            response.then().statusCode(200);

            Assert.assertTrue(response.asString().length() > 0);
        }
    }
}