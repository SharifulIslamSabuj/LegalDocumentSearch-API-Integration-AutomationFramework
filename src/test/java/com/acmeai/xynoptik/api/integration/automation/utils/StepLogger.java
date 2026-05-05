package com.acmeai.xynoptik.api.integration.automation.utils;

import io.qameta.allure.Step;

public final class StepLogger {

    private StepLogger() {}

    /**
     * Log a human-readable test step in Allure report
     * @param message the step description
     */
    @Step("{message}")
    public static void step(String message) {
        // Allure @Step annotation handles the logging
    }
}
