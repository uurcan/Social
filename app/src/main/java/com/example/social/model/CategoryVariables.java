package com.example.social.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryVariables {
    private final List<Category> categoryList = new ArrayList<>();
    public final List<Category> getCategories(){
        categoryList.add(new Category(1,"BUSINESS"));
        categoryList.add(new Category(2,"ENTERTAINMENT"));
        categoryList.add(new Category(3,"GENERAL"));
        categoryList.add(new Category(4,"HEALTH"));
        categoryList.add(new Category(5,"SCIENCE"));
        categoryList.add(new Category(6,"SPORTS"));
        categoryList.add(new Category(7,"TECHNOLOGY"));
        return categoryList;
    }
}
