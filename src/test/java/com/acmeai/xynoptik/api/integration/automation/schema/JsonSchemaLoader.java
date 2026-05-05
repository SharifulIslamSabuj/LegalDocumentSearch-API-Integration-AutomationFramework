package com.acmeai.xynoptik.api.integration.automation.schema;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class JsonSchemaLoader {

    private static final String SCHEMA_DIRECTORY = "schemas/";

    private JsonSchemaLoader() {}

    /**
     * Load schema from src/test/resources/schemas/ directory
     * @param schemaFileName the schema file name (e.g., "generate-schema.json")
     * @return the schema content as String
     */
    public static String loadSchema(String schemaFileName) {

        String schemaPath = SCHEMA_DIRECTORY + schemaFileName;

        try (InputStream is = ClassLoader.getSystemResourceAsStream(schemaPath)) {

            if (is == null) {
                throw new RuntimeException("Schema not found at: " + schemaPath + 
                        " (location: src/test/resources/" + schemaPath + ")");
            }

            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            if (content.isEmpty()) {
                throw new RuntimeException("Schema file is empty: " + schemaPath);
            }
            
            return content;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load schema: " + schemaPath, e);
        }
    }
}