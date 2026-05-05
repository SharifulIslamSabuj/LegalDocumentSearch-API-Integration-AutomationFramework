package com.acmeai.xynoptik.api.integration.automation.base;

import com.acmeai.xynoptik.api.integration.automation.config.ConfigManager;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {

        // Set Base URI globally for all API tests
        RestAssured.baseURI = ConfigManager.getBaseUrl();
    }
}