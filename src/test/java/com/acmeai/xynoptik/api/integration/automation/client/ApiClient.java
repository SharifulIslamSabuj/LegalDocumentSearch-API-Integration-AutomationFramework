package com.acmeai.xynoptik.api.integration.automation.client;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class ApiClient {

    private ApiClient() {}

    public static Response get(String endpoint) {
        return given()
                .spec(RequestSpecFactory.getSpec())
                .when()
                .get(endpoint);
    }

    public static Response post(String endpoint, Object body) {

        return given()
                .spec(RequestSpecFactory.getSpec())
                .body(body)
                .when()
                .post(endpoint);
    }

    public static Response postWithAuth(String endpoint, Object body, String token) {

        return given()
                .spec(RequestSpecFactory.getSpec())
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post(endpoint);
    }

    public static Response getWithAuth(String endpoint, String token) {

        return given()
                .spec(RequestSpecFactory.getSpec())
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint);
    }

    public static Response deleteWithAuth(String endpoint, String token) {

        return given()
                .spec(RequestSpecFactory.getSpec())
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(endpoint);
    }
}