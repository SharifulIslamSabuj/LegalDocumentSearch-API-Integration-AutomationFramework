package com.acmeai.xynoptik.api.integration.automation.observability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Enterprise Observability Manager
 * Provides centralized, standardized logging format for:
 * - Test execution lifecycle (TEST_START, TEST_END)
 * - API communication (REQUEST, RESPONSE)
 * - Retry logic (RETRY)
 * - Execution decisions (SKIP_REASON)
 * - Failure classification (ASSERTION_FAILURE, SERVER_FAILURE, NETWORK_FAILURE)
 */
public final class ObservabilityManager {

    private static final Logger logger = Logger.getLogger(ObservabilityManager.class.getName());
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final String SEPARATOR = " | ";
    private static final String LOG_PREFIX = "[Observability]";

    private ObservabilityManager() {}

    // =================================================================
    // TEST LIFECYCLE EVENTS
    // =================================================================

    /**
     * Log test start
     */
    public static void logTestStart(String testClass, String testMethod) {
        String message = String.format("[TEST_START] %s.%s",
                testClass,
                testMethod);
        log(message);
    }

    /**
     * Log test end
     */
    public static void logTestEnd(String testClass, String testMethod, String status) {
        String message = String.format("[TEST_END] %s.%s - Status: %s",
                testClass,
                testMethod,
                status);
        log(message);
    }

    // =================================================================
    // EXECUTION DECISION EVENTS
    // =================================================================

    /**
     * Log test execution
     */
    public static void logRunning(String testName) {
        String message = String.format("[RUNNING] %s", testName);
        log(message);
    }

    /**
     * Log test skip with reason
     */
    public static void logSkipped(String testName, String reason) {
        String message = String.format("[SKIPPED] %s - Reason: %s", testName, reason);
        log(message);
    }

    /**
     * Log execution strategy
     */
    public static void logExecutionStrategy(boolean isCI, String groups, String environment, int total, int executing) {
        log("=================================================================");
        log("EXECUTION STRATEGY");
        log("=================================================================");
        log(String.format("CI Mode: %s", isCI));
        log(String.format("Active Groups: %s", groups));
        log(String.format("Active Environment: %s", environment));
        log(String.format("Total Tests: %d → Executing: %d", total, executing));
        log("=================================================================");
    }

    // =================================================================
    // API COMMUNICATION EVENTS
    // =================================================================

    /**
     * Log API request
     */
    public static void logRequest(String method, String endpoint) {
        String message = String.format("[REQUEST] %s %s", method, endpoint);
        log(message);
    }

    /**
     * Log API response
     */
    public static void logResponse(int statusCode, int bodyLength) {
        String message = String.format("[RESPONSE] Status: %d, Body: %d bytes", statusCode, bodyLength);
        log(message);
    }

    // =================================================================
    // FAILURE CLASSIFICATION
    // =================================================================

    /**
     * Log assertion failure
     */
    public static void logAssertionFailure(String assertion, String actualValue, String expectedValue) {
        String message = String.format("[ASSERTION_FAILURE] %s | Expected: %s, Actual: %s",
                assertion, expectedValue, actualValue);
        logger.warning(message);
    }

    /**
     * Log server failure (5xx error)
     */
    public static void logServerFailure(String method, String endpoint, int statusCode, int attempt, int retryCount) {
        String message = String.format("[SERVER_FAILURE] %s %s returned %d | Attempt %d/%d",
                method, endpoint, statusCode, attempt + 1, retryCount + 1);
        logger.warning(message);
    }

    /**
     * Log network failure
     */
    public static void logNetworkFailure(String method, String endpoint, String error, int attempt, int retryCount) {
        String message = String.format("[NETWORK_FAILURE] %s %s | Attempt %d/%d | Error: %s",
                method, endpoint, attempt + 1, retryCount + 1, error);
        logger.warning(message);
    }

    // =================================================================
    // RETRY EVENTS
    // =================================================================

    /**
     * Log retry attempt
     */
    public static void logRetry(String method, String endpoint, String failureType, int attempt, int retryCount, int retryDelayMs) {
        String message = String.format("[RETRY] %s %s (%s) | Attempt %d/%d, Delay: %dms",
                method, endpoint, failureType, attempt + 1, retryCount + 1, retryDelayMs);
        log(message);
    }

    // =================================================================
    // PROFILE AND ENVIRONMENT EVENTS
    // =================================================================

    /**
     * Log active profile resolution
     */
    public static void logProfileResolution(String profile, String baseUrl) {
        String message = String.format("[PROFILE] Environment: %s | Base URL: %s", profile, baseUrl);
        log(message);
    }

    // =================================================================
    // UTILITY METHODS
    // =================================================================

    /**
     * Centralized logging method
     */
    private static void log(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String formattedMessage = String.format("%s %s %s", LOG_PREFIX, timestamp, message);
        logger.info(formattedMessage);
    }
}

