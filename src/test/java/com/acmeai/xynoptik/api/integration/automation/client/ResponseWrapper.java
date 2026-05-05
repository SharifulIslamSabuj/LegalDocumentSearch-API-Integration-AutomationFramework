package com.acmeai.xynoptik.api.integration.automation.client;

import com.acmeai.xynoptik.api.integration.automation.schema.JsonSchemaLoader;
import com.acmeai.xynoptik.api.integration.automation.schema.JsonSchemaValidator;


import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ResponseWrapper {

    private final Response response;
    private final JsonPath jsonPath;

    public ResponseWrapper(Response response) {
        this.response = response;
        this.jsonPath = response.jsonPath();
    }

    // =========================
    // BASIC INFO
    // =========================

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public String getBodyAsString() {
        return response.asString();
    }

    public Response getRawResponse() {
        return response;
    }

    // =========================
    // STATUS HELPERS
    // =========================

    public boolean isSuccess() {
        return getStatusCode() >= 200 && getStatusCode() < 300;
    }

    // =========================
    // JSON EXTRACTION
    // =========================

    public <T> T get(String path) {
        return jsonPath.get(path);
    }

    public String getString(String path) {
        return jsonPath.getString(path);
    }

    public int getInt(String path) {
        return jsonPath.getInt(path);
    }

    public boolean getBoolean(String path) {
        return jsonPath.getBoolean(path);
    }

    // =========================
    // ERROR HELPERS
    // =========================

    public String getErrorMessage() {
        return jsonPath.getString("message");
    }

    public String getDetailError() {
        return jsonPath.getString("detail");
    }

    // =========================
    // JSON SCHEMA VALIDATION
    // =========================

    /**
     * Validate response against JSON schema for contract-based testing
     * @param schemaFileName the schema file name (e.g., "generate-schema.json")
     *        Schema must be located at: src/test/resources/schemas/
     * @throws AssertionError if validation fails
     */
    public void validateSchema(String schemaFileName) {

        String schema = JsonSchemaLoader.loadSchema(schemaFileName);

        JsonSchemaValidator.validate(
                this.getBodyAsString(),
                schema,
                schemaFileName
        );
    }
}