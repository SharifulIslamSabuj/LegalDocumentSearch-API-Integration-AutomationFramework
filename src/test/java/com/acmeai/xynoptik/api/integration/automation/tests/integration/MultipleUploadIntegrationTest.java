package com.acmeai.xynoptik.api.integration.automation.tests.integration;

import com.acmeai.xynoptik.api.integration.automation.base.BaseTest;
import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;
import com.acmeai.xynoptik.api.integration.automation.services.DocumentService;
import com.acmeai.xynoptik.api.integration.automation.services.GenerateService;
import com.acmeai.xynoptik.api.integration.automation.utils.AssertionUtil;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class MultipleUploadIntegrationTest extends BaseTest {

    private final DocumentService documentService = new DocumentService();
    private final GenerateService generateService = new GenerateService();

    @Test
    public void multipleUploadsShouldWork() {

        UploadDocumentRequest doc1 = new UploadDocumentRequest();
        doc1.setId(1);
        doc1.setTitle("A");
        doc1.setContent("Content A");

        UploadDocumentRequest doc2 = new UploadDocumentRequest();
        doc2.setId(2);
        doc2.setTitle("B");
        doc2.setContent("Content B");

        documentService.upload(doc1);
        documentService.upload(doc2);

        GenerateRequest request = new GenerateRequest();
        request.setQuery("Explain documents");

        Response response = generateService.generate(request);

        AssertionUtil.assertStatusCode(response, 200);
        AssertionUtil.assertNotEmpty(
                response.asString(),
                "Response should contain aggregated knowledge"
        );
    }
}