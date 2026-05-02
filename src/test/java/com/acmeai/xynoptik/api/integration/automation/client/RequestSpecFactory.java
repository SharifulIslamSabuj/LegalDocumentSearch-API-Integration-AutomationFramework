package com.acmeai.xynoptik.api.integration.automation.client;

import com.acmeai.xynoptik.api.integration.automation.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpecFactory {

    private RequestSpecFactory() {
        // Prevent instantiation
    }

    public static RequestSpecification getSpec() {

        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("base.url"))
                .setContentType("application/json")
                .addFilter(new AllureRestAssured())
                .addFilter(new ApiLoggingFilter())
                .build();
    }
}