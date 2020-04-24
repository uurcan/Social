package com.example.social.utils;

import com.example.social.model.CategoryVariables;

import java.util.Locale;

public class Specification {
    private String category;
    private String country = Locale.getDefault().getCountry().toLowerCase();
    private String language = null;

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public void setCategory(CategoryVariables category) {
        this.category = category.name();
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
