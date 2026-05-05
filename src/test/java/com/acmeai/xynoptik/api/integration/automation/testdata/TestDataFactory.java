package com.acmeai.xynoptik.api.integration.automation.testdata;

import com.acmeai.xynoptik.api.integration.automation.models.request.GenerateRequest;
import com.acmeai.xynoptik.api.integration.automation.models.request.UploadDocumentRequest;


/**
 * Industry-level Test Data Factory
 * Fully generic and reusable across API automation projects
 * Enhanced with VALID, BOUNDARY, INVALID, and EDGE CASE data generation
 */
public final class TestDataFactory {

    private TestDataFactory() {}

    // =========================================================
    // 🔥 VALID DATA (PRESERVED EXISTING BEHAVIOR)
    // =========================================================

    public static GenerateRequest generateValidGenerateRequest() {
        return new GenerateRequest(RandomDataUtil.randomSentence());
    }

    public static UploadDocumentRequest generateValidUploadRequest() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                RandomDataUtil.safeString("TITLE"),
                RandomDataUtil.randomParagraph()
        );
    }

    // =========================================================
    // 🔥 BOUNDARY CASES
    // =========================================================

    public static GenerateRequest generateEmptyStringInput() {
        return new GenerateRequest("");
    }

    public static GenerateRequest generateMaxLengthInput() {
        return new GenerateRequest(RandomDataUtil.randomString(10000)); // Assuming max length
    }

    public static GenerateRequest generateMinLengthInput() {
        return new GenerateRequest("a");
    }

    public static UploadDocumentRequest generateVeryLargePayload() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                RandomDataUtil.safeString("LARGE"),
                RandomDataUtil.randomString(50000) // Large content
        );
    }

    // =========================================================
    // 🔥 INVALID INPUTS
    // =========================================================

    public static GenerateRequest generateNullRequest() {
        return new GenerateRequest(null);
    }

    public static GenerateRequest generateMissingRequiredFieldsRequest() {
        return new GenerateRequest(null); // Query is required
    }

    public static GenerateRequest generateMalformedJsonRequest() {
        return new GenerateRequest("{\"invalid\": json}"); // Not a valid string for query
    }

    public static GenerateRequest generateSpecialCharacterInput() {
        return new GenerateRequest("!@#$%^&*()_+{}|:<>?[]\\;',./`~");
    }

    public static GenerateRequest generateInvalidDataTypeRequest() {
        return new GenerateRequest(RandomDataUtil.randomNumberString()); // Numeric instead of text
    }

    public static UploadDocumentRequest generateNullUploadRequest() {
        return new UploadDocumentRequest(0, null, null);
    }

    public static UploadDocumentRequest generateMissingRequiredFieldsUploadRequest() {
        return new UploadDocumentRequest(0, null, null); // Missing title and content
    }

    public static UploadDocumentRequest generateMalformedJsonUploadRequest() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                "{\"invalid\": json}",
                "{\"also\": invalid}"
        );
    }

    public static UploadDocumentRequest generateSpecialCharacterUploadRequest() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                "!@#$%^&*()_+{}|:<>?[]\\;',./`~",
                "!@#$%^&*()_+{}|:<>?[]\\;',./`~"
        );
    }

    public static UploadDocumentRequest generateInvalidDataTypeUploadRequest() {
        return new UploadDocumentRequest(
                Integer.parseInt(RandomDataUtil.randomNumberString()), // Invalid id type
                RandomDataUtil.randomNumberString(), // Numeric title
                RandomDataUtil.randomNumberString()  // Numeric content
        );
    }

    // =========================================================
    // 🔥 EDGE CASES
    // =========================================================

    public static GenerateRequest generateUnicodeInput() {
        return new GenerateRequest("测试查询 🚀 中文 émojis ñoños");
    }

    public static GenerateRequest generateRandomLongString() {
        return new GenerateRequest(RandomDataUtil.randomString(5000));
    }

    public static GenerateRequest generateNumericInsteadOfStringFields() {
        return new GenerateRequest("123456789");
    }

    public static GenerateRequest generateMixedTypePayload() {
        return new GenerateRequest("Query with numbers 123 and symbols !@#");
    }

    public static UploadDocumentRequest generateUnicodeUploadRequest() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                "测试标题 🚀",
                "中文内容 émojis ñoños"
        );
    }

    public static UploadDocumentRequest generateRandomLongStringUploadRequest() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                RandomDataUtil.randomString(1000),
                RandomDataUtil.randomString(10000)
        );
    }

    public static UploadDocumentRequest generateNumericInsteadOfStringFieldsUploadRequest() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                "123456",
                "789012345"
        );
    }

    public static UploadDocumentRequest generateMixedTypePayloadUploadRequest() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                "Title with 123 numbers",
                "Content with !@# symbols and 456 numbers"
        );
    }

    // =========================================================
    // 🔥 LEGACY METHODS (PRESERVED FOR COMPATIBILITY)
    // =========================================================

    public static GenerateRequest validGenerateQuery() {
        return generateValidGenerateRequest();
    }

    public static GenerateRequest emptyGenerateQuery() {
        return generateEmptyStringInput();
    }

    public static GenerateRequest customGenerateQuery(String query) {
        return new GenerateRequest(
                query != null ? query : RandomDataUtil.randomSentence()
        );
    }

    public static UploadDocumentRequest validUploadDocument() {
        return generateValidUploadRequest();
    }

    public static UploadDocumentRequest invalidUploadMissingContent() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                RandomDataUtil.safeString("TITLE"),
                null
        );
    }

    public static UploadDocumentRequest invalidUploadMissingTitle() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                null,
                RandomDataUtil.randomParagraph()
        );
    }

    public static UploadDocumentRequest customUploadDocument(int id, String title, String content) {
        return new UploadDocumentRequest(
                id > 0 ? id : RandomDataUtil.randomId(),
                title,
                content
        );
    }


    public static UploadDocumentRequest randomUploadDocument() {
        return new UploadDocumentRequest(
                RandomDataUtil.randomId(),
                RandomDataUtil.safeString("DOC"),
                RandomDataUtil.randomSentence()
        );
    }

    public static GenerateRequest randomGenerateRequest() {
        return new GenerateRequest(
                RandomDataUtil.randomSentence()
        );
    }
}