package com.acmeai.xynoptik.api.integration.automation.tests.api;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;
import com.acmeai.xynoptik.api.integration.automation.services.DocumentService;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class ApiValidationTest extends BaseTest {

    private final DocumentService documentService = new DocumentService();
    private final GenerateService generateService = new GenerateService();


    // TC-API-01: Upload Valid Document
    @Test(description = "Upload Document (Valid)")
    public void uploadDocument_valid_shouldSucceed() {

        UploadDocumentRequest request = new UploadDocumentRequest();
        request.setId(1);
        request.setTitle("Test Doc");
        request.setContent("Sample legal content");

        Response response = documentService.uploadDocument(request);

        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertNotEmpty(response.asString(), "Response should not be empty");
    }


    //TC-API-02: Upload Missing Field

    @Test(description = "Upload Document with missing field should fail")
    public void uploadDocument_missingField_shouldReturnValidationError() {

        UploadDocumentRequest request = new UploadDocumentRequest();
        request.setId(2);
        request.setTitle("Invalid Doc");
        // missing content intentionally

        Response response = documentService.uploadDocument(request);

        AssertionUtil.assertStatusCode(response, 422);
        AssertionUtil.assertBodyContains(response, "detail");
    }


    //TC-API-03: Generate Valid Query

    @Test(description = "Generate response with valid query")
    public void generate_validQuery_shouldReturnResponse() {

        GenerateRequest request = new GenerateRequest();
        request.setQuery("Explain legal agreement");

        Response response = generateService.generateResponse(request);

        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertNotEmpty(response.asString(), "Generated response should not be empty");
    }


    //TC-API-04: Generate Empty Query

    @Test(description = "Generate with empty query should return validation error")
    public void generate_emptyQuery_shouldFailValidation() {

        GenerateRequest request = new GenerateRequest();
        request.setQuery("");

        Response response = generateService.generateResponse(request);

        AssertionUtil.assertStatusCode(response, 200);

        AssertionUtil.assertJsonFieldEquals(response, "success", false);
        AssertionUtil.assertJsonFieldEquals(response, "status", 400);

        AssertionUtil.assertBodyContains(response, "Please provide a search query");
    }
}