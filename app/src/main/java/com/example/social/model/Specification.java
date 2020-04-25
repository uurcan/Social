package com.example.social.model;

import java.util.Locale;

public class Specification {
    private String category;
    private String country = Locale.getDefault().getCountry().toLowerCase();
    private String language = null;
    //todo: paging director
    private int currentPage;

    public int getCurrentPage() {
        return 1;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
