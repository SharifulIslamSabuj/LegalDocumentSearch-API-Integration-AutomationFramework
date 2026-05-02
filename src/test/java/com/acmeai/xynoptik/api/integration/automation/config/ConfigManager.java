package com.acmeai.xynoptik.api.integration.automation.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("Config/config.properties not found in resources");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    // Generic getter (future-proof)
    public static String get(String key) {
        return properties.getProperty(key);
    }

    // Specific getter (clean & readable)
    public static String getBaseUrl() {
        return properties.getProperty("base.url");
    }
}