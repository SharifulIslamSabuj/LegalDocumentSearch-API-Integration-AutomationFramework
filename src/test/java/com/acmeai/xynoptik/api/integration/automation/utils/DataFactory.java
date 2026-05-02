package com.acmeai.xynoptik.api.integration.automation.utils;

import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;

public class DataFactory {

    public static GenerateRequest generateQuery(String query) {
        return new GenerateRequest(query);
    }

    public static UploadDocumentRequest uploadDoc(int id, String title, String content) {
        return new UploadDocumentRequest(id, title, content);
    }
}