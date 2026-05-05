package com.acmeai.xynoptik.api.integration.automation.client;

import com.acmeai.xynoptik.api.integration.automation.config.ConfigManager;
import com.acmeai.xynoptik.api.integration.automation.observability.ObservabilityManager;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.Map;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;

/**
 * Enterprise API Client with observability
 * Integrates with ObservabilityManager for standardized logging
 */
public class ApiClient {

    private static final Logger logger = Logger.getLogger(ApiClient.class.getName());

    private ApiClient() {}

    public static ResponseWrapper request(Method method, String endpoint, Object body, Map<String, String> headers) {

        int retryCount = ConfigManager.getRetryCount();
        int retryDelay = ConfigManager.getRetryDelay();
        ResponseWrapper lastResponse = null;

        for (int attempt = 0; attempt <= retryCount; attempt++) {
            try {
                var request = given()
                        .spec(RequestSpecBuilderFactory.build(headers, null, null, false));

                if (body != null) {
                    request.body(body);
                }

                Response response = request.request(method, endpoint);
                lastResponse = new ResponseWrapper(response);

                if (response.getStatusCode() < 500) {
                    return lastResponse;
                } else {
                    ObservabilityManager.logServerFailure(method.name(), endpoint, response.getStatusCode(), attempt, retryCount);
                    if (attempt < retryCount) {
                        ObservabilityManager.logRetry(method.name(), endpoint, "SERVER_FAILURE", attempt, retryCount, retryDelay);
                        Thread.sleep(retryDelay);
                    }
                }

            } catch (Exception e) {
                ObservabilityManager.logNetworkFailure(method.name(), endpoint, e.getMessage(), attempt, retryCount);
                if (attempt < retryCount) {
                    ObservabilityManager.logRetry(method.name(), endpoint, "NETWORK_FAILURE", attempt, retryCount, retryDelay);
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        if (lastResponse != null) {
            logger.warning(String.format("[API_FAILURE] Request %s %s failed after %d attempts, returning status %d",
                    method, endpoint, retryCount + 1, lastResponse.getStatusCode()));
        } else {
            logger.severe(String.format("[API_FAILURE] Request %s %s failed completely with no response after %d attempts",
                    method, endpoint, retryCount + 1));
        }

        return lastResponse;
    }
}