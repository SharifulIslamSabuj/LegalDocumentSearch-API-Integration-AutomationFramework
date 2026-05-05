package com.acmeai.xynoptik.api.integration.automation.client;

import com.acmeai.xynoptik.api.integration.automation.config.ConfigManager;
import com.acmeai.xynoptik.api.integration.automation.filters.ApiLoggingFilter;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class RequestSpecBuilderFactory {

    private RequestSpecBuilderFactory() {
        // Prevent instantiation
    }

    public static RequestSpecification build(Map<String, String> headers, Map<String, Object> queryParams, String token, boolean relaxHttps) {

        HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", ConfigManager.getConnectionTimeout())
                .setParam("http.socket.timeout", ConfigManager.getReadTimeout());

        RestAssuredConfig config = RestAssuredConfig.config().httpClient(httpClientConfig);

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUrl())
                .setContentType("application/json")
                .setConfig(config)
                .addFilter(new AllureRestAssured())
                .addFilter(new ApiLoggingFilter());

        if (headers != null && !headers.isEmpty()) {
            builder.addHeaders(headers);
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            builder.addQueryParams(queryParams);
        }

        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        if (relaxHttps) {
            builder.setRelaxedHTTPSValidation();
        }

        return builder.build();
    }
}