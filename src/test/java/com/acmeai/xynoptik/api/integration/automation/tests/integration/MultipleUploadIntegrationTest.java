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

public class MultipleUploadIntegrationTest extends BaseTest {

    private final DocumentService documentService = new DocumentService();
    private final GenerateService generateService = new GenerateService();

    @Test(description = "Multiple uploads should not break system and generate should return valid response", groups = {"integration", "regression"})
    public void multipleUploads_shouldWorkCorrectly() {

        // Arrange
        UploadDocumentRequest doc1 = TestDataFactory.validUploadDocument();
        UploadDocumentRequest doc2 = TestDataFactory.validUploadDocument();
        GenerateRequest request = TestDataFactory.validGenerateQuery();

        // Act
        ResponseWrapper upload1 = documentService.upload(doc1);
        ResponseWrapper upload2 = documentService.upload(doc2);
        ResponseWrapper response = generateService.generate(request);

        // Assert
        AssertionUtil.assertStatusCode(upload1, 200);
        AssertionUtil.assertStatusCode(upload2, 200);
        AssertionUtil.assertStatusCode(response, 200);

        AssertionUtil.assertFieldEquals(response, "success", true);
        AssertionUtil.assertFieldEquals(response, "status", 200);

        AssertionUtil.assertNotEmpty(response.getBodyAsString(),
                "Generated response should not be empty after multiple uploads");

        // Schema validation
        upload1.validateSchema("upload-schema.json");
        upload2.validateSchema("upload-schema.json");
        response.validateSchema("generate-schema.json");
    }
}