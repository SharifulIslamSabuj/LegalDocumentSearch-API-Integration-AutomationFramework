package com.acmeai.xynoptik.api.integration.automation.utils;

import io.qameta.allure.Allure;

public class StepLogger {

    private StepLogger() {}

    public static void step(String name) {
        Allure.step(name);
    }
}