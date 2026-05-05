package com.acmeai.xynoptik.api.integration.automation.config;

import com.acmeai.xynoptik.api.integration.automation.observability.ObservabilityManager;

import java.io.InputStream;
import java.util.Properties;

/**
 * Enterprise Configuration Manager
 * Supports PROFILE-BASED ENVIRONMENT HANDLING:
 * - dev: localhost/mock endpoints
 * - qa: QA staging environment
 * - prod: production endpoint
 *
 * Uses ExecutionController as source for active profile.
 * Maintains backward compatibility with env switching.
 */
public class ConfigManager {

    private static final Properties properties = new Properties();
    private static String resolvedProfile = null;
    private static String resolvedBaseUrl = null;

    // Config Key Constants
    private static final String ENV = "env";
    private static final String DEV_BASE_URL = "dev.base.url";
    private static final String QA_BASE_URL = "qa.base.url";
    private static final String PROD_BASE_URL = "prod.base.url";
    private static final String DEFAULT_ENV = "dev";
    private static final String CONNECTION_TIMEOUT = "connection.timeout";
    private static final String READ_TIMEOUT = "read.timeout";
    private static final String RETRY_COUNT = "retry.count";
    private static final String RETRY_DELAY = "retry.delay";

    static {
        try (InputStream input = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("Config/config.properties not found in resources");
            }

            properties.load(input);

            // Initialize profile resolution (logged at first request)
            resolveActiveProfile();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    // Generic getter (future-proof)
    public static String get(String key) {
        return properties.getProperty(key);
    }

    // Get current environment
    public static String getEnv() {
        return properties.getProperty(ENV, DEFAULT_ENV);
    }

    /**
     * Resolve active profile using ExecutionController as source of truth
     * Log profile resolution using ObservabilityManager
     */
    private static void resolveActiveProfile() {
        // Get profile from ExecutionController (respects system properties)
        String profile = ExecutionController.getEnv();

        // Resolve base URL based on active profile
        String baseUrl = resolveBaseUrlForProfile(profile);

        // Cache resolution for consistent behavior
        resolvedProfile = profile;
        resolvedBaseUrl = baseUrl;

        // Log the profile resolution
        ObservabilityManager.logProfileResolution(profile, baseUrl);
    }

    /**
     * Resolve base URL for the given profile
     * Ensures profile-based URL selection
     */
    private static String resolveBaseUrlForProfile(String profile) {
        return switch (profile.toLowerCase()) {
            case "qa" -> properties.getProperty(QA_BASE_URL);
            case "prod" -> properties.getProperty(PROD_BASE_URL);
            case "dev" -> properties.getProperty(DEV_BASE_URL);
            default -> properties.getProperty(DEV_BASE_URL);
        };
    }

    /**
     * Get base URL - ACTIVE PROFILE RESOLUTION
     * Returns URL based on active profile from ExecutionController
     * Maintains backward compatibility
     */
    public static String getBaseUrl() {
        // If profile has changed (e.g., through system property), re-resolve
        String currentProfile = ExecutionController.getEnv();
        if (!currentProfile.equals(resolvedProfile)) {
            resolveActiveProfile();
        }

        return resolvedBaseUrl;
    }

    /**
     * Get active profile (explicit accessor)
     * Uses ExecutionController as SINGLE SOURCE OF TRUTH
     */
    public static String getActiveProfile() {
        return ExecutionController.getEnv();
    }

    // Connection timeout in milliseconds
    public static int getConnectionTimeout() {
        try {
            return Integer.parseInt(properties.getProperty(CONNECTION_TIMEOUT, "5000"));
        } catch (NumberFormatException e) {
            return 5000;
        }
    }

    // Read timeout in milliseconds
    public static int getReadTimeout() {
        try {
            return Integer.parseInt(properties.getProperty(READ_TIMEOUT, "10000"));
        } catch (NumberFormatException e) {
            return 10000;
        }
    }

    // Retry count for failed requests
    public static int getRetryCount() {
        try {
            return Integer.parseInt(properties.getProperty(RETRY_COUNT, "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Retry delay between retries in milliseconds
    public static int getRetryDelay() {
        try {
            return Integer.parseInt(properties.getProperty(RETRY_DELAY, "1000"));
        } catch (NumberFormatException e) {
            return 1000;
        }
    }
}