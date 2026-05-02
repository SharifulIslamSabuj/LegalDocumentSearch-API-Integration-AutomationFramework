package com.acmeai.xynoptik.api.integration.automation.base;

import com.acmeai.xynoptik.api.integration.automation.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {

        // Set Base URI globally for all API tests
        RestAssured.baseURI = ConfigManager.getBaseUrl();

        // Enable request & response logging
        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );

        // Extra safety: log only when validation fails
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        System.out.println("BaseTest Setup Completed");
        System.out.println("Base URI: " + RestAssured.baseURI);
    }
}