package com.acmeai.xynoptik.api.integration.automation.testdata;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.UUID;

public final class RandomDataUtil {

    // Thread-safe Faker (industry standard for parallel execution)
    private static final ThreadLocal<Faker> FAKER =
            ThreadLocal.withInitial(() -> new Faker(Locale.ENGLISH));

    private RandomDataUtil() {
        // prevent instantiation
    }

    // =========================
    // TEXT DATA
    // =========================

    public static String randomSentence() {
        return FAKER.get().lorem().sentence();
    }

    public static String randomParagraph() {
        return FAKER.get().lorem().paragraph();
    }

    public static String randomWord() {
        return FAKER.get().lorem().word();
    }

    public static String randomTitle() {
        return "AutoTitle-" + UUID.randomUUID();
    }

    public static String randomShortText() {
        return FAKER.get().lorem().characters(10, 20);
    }

    // =========================
    // IDENTIFIERS
    // =========================

    public static int randomId() {
        return FAKER.get().number().numberBetween(1, 1_000_000);
    }

    public static long randomLongId() {
        return Math.abs(FAKER.get().number().randomNumber());
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    // =========================
    // NUMBERS
    // =========================

    public static int randomInt(int min, int max) {
        return FAKER.get().number().numberBetween(min, max);
    }

    public static double randomDouble(double min, double max) {
        return min + (max - min) * Math.random();
    }

    // =========================
    // BOOLEAN
    // =========================

    public static boolean randomBoolean() {
        return FAKER.get().bool().bool();
    }

    // =========================
    // SAFE GENERIC DATA
    // =========================

    public static String safeString(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

    public static String safeEmail() {
        return FAKER.get().internet().emailAddress();
    }

    public static String safeName() {
        return FAKER.get().name().fullName();
    }

    // =========================
    // ADDITIONAL HELPERS FOR TEST DATA
    // =========================

    public static String randomString(int length) {
        return FAKER.get().lorem().characters(length);
    }

    public static String randomEmail() {
        return FAKER.get().internet().emailAddress();
    }

    public static String randomNumberString() {
        return String.valueOf(FAKER.get().number().randomNumber());
    }
}