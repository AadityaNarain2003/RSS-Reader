package com.sismics.reader.core.constant;

/**
 * Application constants as an enum.
 *
 * @author jtremeaux
 */
public enum Constants {
    // Default settings
    DEFAULT_LOCALE_ID("en"),
    DEFAULT_TIMEZONE_ID("Europe/London"),
    DEFAULT_THEME_ID("default.less"),
    DEFAULT_ADMIN_PASSWORD("$2a$05$6Ny3TjrW3aVAL1or2SlcR.fhuDgPKp5jp.P9fBXwVNePgeLqb4i3C"),
    DEFAULT_USER_ROLE("user"),

    // Lucene storage options
    LUCENE_DIRECTORY_STORAGE_RAM("RAM"),
    LUCENE_DIRECTORY_STORAGE_FILE("FILE"),

    // Import job constants
    JOB_IMPORT("import"),
    JOB_EVENT_FEED_COUNT("import.feed_count"),
    JOB_EVENT_STARRED_ARTICLED_COUNT("import.starred_article_count"),
    JOB_EVENT_FEED_IMPORT_SUCCESS("import.feed_import_success"),
    JOB_EVENT_FEED_IMPORT_FAILURE("import.feed_import_failure"),
    JOB_EVENT_STARRED_ARTICLE_IMPORT_SUCCESS("import.starred_article_import_success"),
    JOB_EVENT_STARRED_ARTICLE_IMPORT_FAILURE("import.starred_article_import_failure");

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}