package com.acmeai.xynoptik.api.integration.automation.utils;

import io.restassured.response.Response;
import org.testng.Assert;

public class AssertionUtil {

    private AssertionUtil() {}


    // NULL VALIDATION
    public static void assertNotNull(Object actual, String message) {
        Assert.assertNotNull(actual, message);
    }


    // EMPTY VALIDATION
    public static void assertNotEmpty(String value, String message) {
        Assert.assertTrue(
                value != null && !value.trim().isEmpty(),
                message
        );
    }


    // BOOLEAN VALIDATION
    public static void assertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }


    // EQUALS VALIDATION
    public static void assertEquals(Object actual, Object expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }


    // STATUS CODE VALIDATION
    public static void assertStatusCode(Response response, int expectedCode) {
        Assert.assertEquals(
                response.getStatusCode(),
                expectedCode,
                "Status code mismatch"
        );
    }


    // RESPONSE BODY VALIDATION
    public static void assertBodyContains(Response response, String expectedText) {
        Assert.assertTrue(
                response.asString().contains(expectedText),
                "Response does not contain: " + expectedText
        );
    }

    public static void assertJsonFieldEquals(Response response, String jsonPath, Object expected) {
        Object actual = response.jsonPath().get(jsonPath);
        Assert.assertEquals(actual, expected, "JSON field mismatch: " + jsonPath);
    }
}