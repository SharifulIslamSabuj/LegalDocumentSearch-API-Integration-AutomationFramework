package com.acmeai.xynoptik.api.integration.automation.utils;

import com.acmeai.xynoptik.api.integration.automation.client.ResponseWrapper;
import com.acmeai.xynoptik.api.integration.automation.observability.ObservabilityManager;
import org.testng.Assert;

/**
 * Enterprise Assertion Utility with failure classification
 * Integrates with ObservabilityManager for observability
 * Classifies failures as:
 * - ASSERTION_FAILURE: test assertion failed
 * - SERVER_FAILURE: 5xx response
 * - NETWORK_FAILURE: connection/network error
 */
public class AssertionUtil {

    // =========================
    // STATUS CODE ASSERTIONS
    // =========================

    public static void assertSuccess(ResponseWrapper response) {
        int statusCode = response.getStatusCode();
        String failureMsg = "Expected success status code (200-299), but got: " + statusCode +
                            ". Response body: " + response.getBodyAsString();

        try {
            Assert.assertTrue(
                    statusCode >= 200 && statusCode < 300,
                    failureMsg
            );
        } catch (AssertionError e) {
            classifyAndLogFailure(response, "success status (200-299)", String.valueOf(statusCode));
            throw e;
        }
    }

    public static void assertError(ResponseWrapper response) {
        int statusCode = response.getStatusCode();
        String failureMsg = "Expected error status code (>=400), but got: " + statusCode +
                            ". Response body: " + response.getBodyAsString();

        try {
            Assert.assertTrue(
                    statusCode >= 400,
                    failureMsg
            );
        } catch (AssertionError e) {
            classifyAndLogFailure(response, "error status (>=400)", String.valueOf(statusCode));
            throw e;
        }
    }

    public static void assertStatusCode(ResponseWrapper response, int expected) {
        int actual = response.getStatusCode();
        String failureMsg = "Status code mismatch. Expected: " + expected + ", Actual: " + actual +
                            ". Response body: " + response.getBodyAsString();

        try {
            Assert.assertEquals(actual, expected, failureMsg);
        } catch (AssertionError e) {
            ObservabilityManager.logAssertionFailure(
                    "Status Code",
                    String.valueOf(actual),
                    String.valueOf(expected)
            );
            throw e;
        }
    }

    // =========================
    // FIELD ASSERTIONS
    // =========================

    public static void assertFieldEquals(ResponseWrapper response, String path, Object expected) {
        Object actual = response.get(path);
        String failureMsg = "Field '" + path + "' mismatch. Expected: " + expected + ", Actual: " + actual +
                            ". Response body: " + response.getBodyAsString();

        try {
            Assert.assertEquals(actual, expected, failureMsg);
        } catch (AssertionError e) {
            ObservabilityManager.logAssertionFailure(
                    "Field: " + path,
                    String.valueOf(actual),
                    String.valueOf(expected)
            );
            throw e;
        }
    }

    // =========================
    // EXISTENCE ASSERTIONS
    // =========================

    public static void assertNotEmpty(String actual, String message) {
        try {
            Assert.assertNotNull(actual, message);
            Assert.assertFalse(actual.trim().isEmpty(), message);
        } catch (AssertionError e) {
            ObservabilityManager.logAssertionFailure(
                    "Not Empty",
                    actual != null ? actual : "null",
                    "non-empty value"
            );
            throw e;
        }
    }

    public static void assertNotNull(Object obj, String message) {
        try {
            Assert.assertNotNull(obj, message);
        } catch (AssertionError e) {
            ObservabilityManager.logAssertionFailure(
                    "Not Null",
                    "null",
                    "non-null value"
            );
            throw e;
        }
    }

    // =========================
    // CONTENT ASSERTIONS
    // =========================

    public static void assertBodyContains(ResponseWrapper response, String expectedText) {
        String body = response.getBodyAsString();
        String failureMsg = "Response does not contain: " + expectedText +
                            ". Response body: " + body;

        try {
            Assert.assertTrue(
                    body.contains(expectedText),
                    failureMsg
            );
        } catch (AssertionError e) {
            ObservabilityManager.logAssertionFailure(
                    "Body Contains",
                    body.substring(0, Math.min(50, body.length())) + "...",
                    "contains: " + expectedText
            );
            throw e;
        }
    }

    // =========================
    // FAILURE CLASSIFICATION
    // =========================

    /**
     * Classify and log API failure based on status code
     */
    private static void classifyAndLogFailure(ResponseWrapper response, String expected, String actual) {
        int statusCode = response.getStatusCode();

        if (statusCode >= 500) {
            ObservabilityManager.logAssertionFailure(
                    "Server Error (5xx)",
                    String.valueOf(statusCode),
                    expected
            );
        } else if (statusCode >= 400) {
            ObservabilityManager.logAssertionFailure(
                    "Client Error (4xx)",
                    String.valueOf(statusCode),
                    expected
            );
        }
    }
}