package com.acmeai.xynoptik.api.integration.automation.client;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpecFactory {

    private ResponseSpecFactory() {}

    // Generic 200 OK response
    public static ResponseSpecification expect200() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }

    // Generic 201 Created response
    public static ResponseSpecification expect201() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .build();
    }

    // Generic 204 No Content response
    public static ResponseSpecification expect204() {
        return new ResponseSpecBuilder()
                .expectStatusCode(204)
                .build();
    }

    // Flexible success validation (future scalable)
    public static ResponseSpecification expectSuccess() {
        return new ResponseSpecBuilder()
                .build();
    }
}