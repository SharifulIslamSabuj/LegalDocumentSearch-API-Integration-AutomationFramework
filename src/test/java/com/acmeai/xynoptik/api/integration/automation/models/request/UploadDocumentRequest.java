package com.acmeai.xynoptik.api.integration.automation.models.request;

public class UploadDocumentRequest {

    private int id;
    private String title;
    private String content;

    public UploadDocumentRequest() {}

    public UploadDocumentRequest(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}