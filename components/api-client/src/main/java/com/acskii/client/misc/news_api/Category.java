package com.acskii.client.misc.news_api;

/*
    Possible options:
        - business
        - entertainment
        - general
        - health
        - science
        - sports
        - technology
*/

public enum Category {
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
