package com.acmeai.xynoptik.api.integration.automation.config;

import java.util.List;

/**
 * Enterprise-grade Execution Controller
 * Centralizes all runtime execution decisions for CI-aware test execution
 */
public final class ExecutionController {

    private ExecutionController() {}

    // =========================
    // SYSTEM PROPERTY READERS
    // =========================

    /**
     * Get active environment (dev/qa/prod)
     */
    public static String getEnv() {
        return System.getProperty("env", "dev");
    }

    /**
     * Get active test groups (smoke/regression/integration/all)
     */
    public static String getGroups() {
        return System.getProperty("groups", "all");
    }

    /**
     * Check if running in CI environment
     */
    public static boolean isCI() {
        String ciProp = System.getProperty("CI", "false");
        String ciEnv = System.getenv("CI");
        return "true".equalsIgnoreCase(ciProp) || "true".equalsIgnoreCase(ciEnv);
    }

    // =========================
    // EXECUTION MODE CHECKERS
    // =========================

    /**
     * Check if smoke tests should run
     */
    public static boolean isSmoke() {
        String groups = getGroups().toLowerCase();
        return groups.contains("smoke") || groups.equals("all") || groups.isEmpty();
    }

    /**
     * Check if regression tests should run
     */
    public static boolean isRegression() {
        String groups = getGroups().toLowerCase();
        return groups.contains("regression") || groups.equals("all") || groups.isEmpty();
    }

    /**
     * Check if integration tests should run
     */
    public static boolean isIntegration() {
        String groups = getGroups().toLowerCase();
        // In CI mode, integration tests are optional unless explicitly requested
        if (isCI() && !groups.contains("integration")) {
            return false;
        }
        return groups.contains("integration") || groups.equals("all") || groups.isEmpty();
    }

    // =========================
    // OPTIONAL TEST FILTER HOOK
    // =========================

    /**
     * Check if a test with given groups should run
     * @param groups comma-separated list of groups (e.g., "smoke,regression")
     */
    public static boolean shouldRun(String groups) {
        if (groups == null || groups.trim().isEmpty()) {
            return true; // No groups specified, run by default
        }

        List<String> testGroups = List.of(groups.split(","));
        String activeGroups = getGroups().toLowerCase();

        // If all groups requested, run everything
        if (activeGroups.equals("all") || activeGroups.isEmpty()) {
            return true;
        }

        // Check if any of the test's groups match active execution mode
        for (String testGroup : testGroups) {
            String trimmedGroup = testGroup.trim().toLowerCase();
            if (activeGroups.contains(trimmedGroup)) {
                return true;
            }
        }

        return false;
    }

    // =========================
    // CI/CD STRATEGY BEHAVIOR
    // =========================

    /**
     * Get recommended default groups for current execution context
     */
    public static String getRecommendedGroups() {
        if (isCI()) {
            return "smoke,regression"; // CI prioritizes smoke + regression
        }
        return "all"; // Local execution allows everything
    }

    /**
     * Check if current execution is in recommended CI mode
     */
    public static boolean isRecommendedCIMode() {
        return isCI() && ("smoke,regression".equals(getGroups()) ||
                         "smoke".equals(getGroups()) ||
                         "regression".equals(getGroups()));
    }
}
