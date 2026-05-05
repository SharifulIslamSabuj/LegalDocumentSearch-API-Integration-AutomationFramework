package com.acmeai.xynoptik.api.integration.automation.tests.integration;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.client.ResponseWrapper;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;
import com.acmeai.xynoptik.api.integration.automation.services.DocumentService;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.testdata.TestDataFactory;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import org.testng.annotations.Test;

public class UploadGenerateIntegrationTest extends BaseTest {

    private final DocumentService documentService = new DocumentService();
    private final GenerateService generateService = new GenerateService();

    @Test(description = "End-to-end flow: Upload document → Generate response with context validation", groups = {"integration", "regression"})
    public void uploadThenGenerate_shouldReturnValidContextResponse() {

        // Arrange
        UploadDocumentRequest uploadRequest = TestDataFactory.validUploadDocument();
        GenerateRequest generateRequest = TestDataFactory.validGenerateQuery();

        // Act
        ResponseWrapper uploadResponse = documentService.upload(uploadRequest);
        ResponseWrapper generateResponse = generateService.generate(generateRequest);

        // Assert - Upload validation
        AssertionUtil.assertStatusCode(uploadResponse, 200);
        uploadResponse.validateSchema("upload-schema.json");

        // Assert - Generate validation
        AssertionUtil.assertStatusCode(generateResponse, 200);

        AssertionUtil.assertFieldEquals(generateResponse, "success", true);
        AssertionUtil.assertFieldEquals(generateResponse, "status", 200);

        AssertionUtil.assertNotEmpty(
                generateResponse.getBodyAsString(),
                "Generated response should not be empty after document upload"
        );

        // Contract validation
        generateResponse.validateSchema("generate-schema.json");
    }
}