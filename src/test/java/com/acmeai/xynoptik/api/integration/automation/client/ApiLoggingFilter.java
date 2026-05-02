package com.acmeai.xynoptik.api.integration.automation.client;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class ApiLoggingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        // Log Request
        String requestLog =
                "METHOD: " + requestSpec.getMethod() + "\n" +
                        "URI: " + requestSpec.getURI() + "\n" +
                        "BODY: " + requestSpec.getBody();

        Allure.addAttachment("API REQUEST", requestLog);

        Response response = ctx.next(requestSpec, responseSpec);

        // Log Response
        String responseLog =
                "STATUS: " + response.getStatusCode() + "\n" +
                        "BODY: " + response.getBody().asPrettyString();

        Allure.addAttachment("API RESPONSE", responseLog);

        return response;
    }
}