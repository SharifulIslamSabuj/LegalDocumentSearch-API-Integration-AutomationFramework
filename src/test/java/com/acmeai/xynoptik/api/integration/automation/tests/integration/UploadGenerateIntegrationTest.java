package com.acmeai.xynoptik.api.integration.automation.tests.integration;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;
import com.acmeai.xynoptik.api.integration.automation.services.DocumentService;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import org.testng.annotations.Test;

public class UploadGenerateIntegrationTest extends BaseTest {

    private final DocumentService documentService = new DocumentService();
    private final GenerateService generateService = new GenerateService();

    @Test(description = "Upload → Generate → Validate Response")
    public void uploadThenGenerateShouldReturnContext() {

        var uploadResponse = documentService.upload(
                new UploadDocumentRequest(
                        101,
                        "Contract Law",
                        "A contract is a legally binding agreement."
                )
        );

        AssertionUtil.assertStatusCode(uploadResponse, 200);

        var generateResponse = generateService.generate(
                new GenerateRequest("What is a contract?")
        );

        AssertionUtil.assertStatusCode(generateResponse, 200);
        AssertionUtil.assertNotEmpty(generateResponse.asString(),
                "Generated response should not be empty");
    }
}