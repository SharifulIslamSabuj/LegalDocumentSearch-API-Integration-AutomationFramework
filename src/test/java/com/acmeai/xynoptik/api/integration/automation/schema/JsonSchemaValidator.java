package com.acmeai.xynoptik.api.integration.automation.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.Set;

public final class JsonSchemaValidator {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaFactory factory =
            JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    private JsonSchemaValidator() {}

    /**
     * Validate JSON response against schema
     * @param jsonResponse the JSON response body as String
     * @param schemaContent the schema content as String
     * @param schemaFileName optional schema file name for error reporting
     * @throws AssertionError if validation fails
     */
    public static void validate(String jsonResponse, String schemaContent, String schemaFileName) {

        try {
            JsonSchema schema = factory.getSchema(schemaContent);
            JsonNode node = mapper.readTree(jsonResponse);

            Set<ValidationMessage> errors = schema.validate(node);

            if (!errors.isEmpty()) {
                StringBuilder sb = new StringBuilder("\n");

                if (schemaFileName != null && !schemaFileName.isEmpty()) {
                    sb.append("JSON Schema Validation Failed for: ").append(schemaFileName).append("\n");
                } else {
                    sb.append("JSON Schema Validation Failed:\n");
                }

                sb.append("=".repeat(60)).append("\n");

                for (ValidationMessage msg : errors) {
                    sb.append("- ").append(msg.getMessage()).append("\n");
                }

                sb.append("=".repeat(60));

                throw new AssertionError(sb.toString());
            }

        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Schema validation error", e);
        }
    }

    /**
     * Validate JSON response against schema (overloaded for backward compatibility)
     */
    public static void validate(String jsonResponse, String schemaContent) {
        validate(jsonResponse, schemaContent, null);
    }
}