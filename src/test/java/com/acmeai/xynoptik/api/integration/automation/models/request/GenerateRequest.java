package com.acmeai.xynoptik.api.integration.automation.models.request;

public class GenerateRequest {

    private String query;

    public GenerateRequest() {}

    public GenerateRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}