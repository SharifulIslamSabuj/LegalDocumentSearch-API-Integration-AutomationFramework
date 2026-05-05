package com.acmeai.xynoptik.api.integration.automation.filters;

import com.acmeai.xynoptik.api.integration.automation.observability.ObservabilityManager;
import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Enterprise API Logging Filter
 * Provides standardized observability for all API requests/responses
 * Integrates with ObservabilityManager for consistent logging format
 */
public class ApiLoggingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        // ========== LOG REQUEST ==========
        String method = requestSpec.getMethod();
        String uri = requestSpec.getURI();
        String requestBody = requestSpec.getBody() != null ? requestSpec.getBody().toString() : "empty";

        // Use standardized observability logging
        ObservabilityManager.logRequest(method, uri);

        // Add to Allure report
        String requestLog =
                "METHOD: " + method + "\n" +
                        "URI: " + uri + "\n" +
                        "BODY: " + requestBody;

        Allure.addAttachment("API REQUEST", requestLog);

        // ========== EXECUTE REQUEST ==========
        Response response = ctx.next(requestSpec, responseSpec);

        // ========== LOG RESPONSE ==========
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asPrettyString();
        int bodyLength = responseBody != null ? responseBody.length() : 0;

        // Use standardized observability logging
        ObservabilityManager.logResponse(statusCode, bodyLength);

        // Add to Allure report
        String responseLog =
                "STATUS: " + statusCode + "\n" +
                        "BODY: " + responseBody;

        Allure.addAttachment("API RESPONSE", responseLog);

        return response;
    }
}
