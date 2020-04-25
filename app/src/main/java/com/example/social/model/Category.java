package com.example.social.model;

public class Category  {
    private int icon;
    private String name;

    public Category(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getId() {
        return icon;
    }

    public void setId(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
