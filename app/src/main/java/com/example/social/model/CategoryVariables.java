package com.example.social.model;

public enum CategoryVariables {
    business("BUSINESS"),
    entertainment("ENTERTAINMENT"),
    general("GENERAL"),
    health("HEALTH"),
    science("SCIENCE"),
    sports("SPORTS"),
    technology("TECHNOLOGY");

    public final String title;

    CategoryVariables(String title) {
        this.title = title;
    }
}
