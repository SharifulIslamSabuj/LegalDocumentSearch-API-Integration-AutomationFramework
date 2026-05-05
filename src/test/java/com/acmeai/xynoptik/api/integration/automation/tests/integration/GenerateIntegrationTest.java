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

public class GenerateIntegrationTest extends BaseTest {

    private final DocumentService documentService = new DocumentService();
    private final GenerateService generateService = new GenerateService();

    @Test(description = "Upload → Generate → Validate response stability", groups = {"integration", "regression"})
    public void uploadThenGenerate_shouldReturnValidResponse() {

        // Arrange
        UploadDocumentRequest uploadRequest = TestDataFactory.validUploadDocument();
        GenerateRequest generateRequest = TestDataFactory.validGenerateQuery();

        // Act
        ResponseWrapper uploadResponse = documentService.upload(uploadRequest);
        ResponseWrapper generateResponse = generateService.generate(generateRequest);

        // Assert
        AssertionUtil.assertStatusCode(uploadResponse, 200);
        AssertionUtil.assertStatusCode(generateResponse, 200);
        AssertionUtil.assertNotEmpty(generateResponse.getBodyAsString(),
                "Generated response should not be empty");

        // Schema validation
        uploadResponse.validateSchema("upload-schema.json");
        generateResponse.validateSchema("generate-schema.json");
    }

    @Test(description = "Generate without upload should still return valid response", groups = {"smoke", "regression"})
    public void generateWithoutUpload_shouldReturnValidResponse() {

        // Arrange
        GenerateRequest request = TestDataFactory.validGenerateQuery();

        // Act
        ResponseWrapper response = generateService.generate(request);

        // Assert
        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertNotEmpty(response.getBodyAsString(),
                "Response should not be empty");

        // Schema validation
        response.validateSchema("generate-schema.json");
    }

    @Test(description = "Multiple uploads should not break system behavior", groups = {"integration", "regression"})
    public void multipleUploads_shouldRemainStable() {

        // Arrange
        UploadDocumentRequest doc1 = TestDataFactory.validUploadDocument();
        UploadDocumentRequest doc2 = TestDataFactory.validUploadDocument();
        GenerateRequest request = TestDataFactory.validGenerateQuery();

        // Act
        ResponseWrapper resp1 = documentService.upload(doc1);
        ResponseWrapper resp2 = documentService.upload(doc2);
        ResponseWrapper response = generateService.generate(request);

        // Assert
        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertNotEmpty(response.getBodyAsString(),
                "Response should remain stable after multiple uploads");

        // Schema validation
        resp1.validateSchema("upload-schema.json");
        resp2.validateSchema("upload-schema.json");
        response.validateSchema("generate-schema.json");
    }

    @Test(description = "Repeated generate calls should be stable", groups = {"integration", "regression"})
    public void repeatedGenerateCalls_shouldBeConsistent() {

        // Arrange
        GenerateRequest request = TestDataFactory.validGenerateQuery();

        // Act & Assert
        for (int i = 0; i < 5; i++) {
            ResponseWrapper response = generateService.generate(request);

            AssertionUtil.assertStatusCode(response, 200);
            AssertionUtil.assertNotEmpty(response.getBodyAsString(),
                    "Response should remain stable across iterations");

            // Schema validation on each iteration
            response.validateSchema("generate-schema.json");
        }
    }
}