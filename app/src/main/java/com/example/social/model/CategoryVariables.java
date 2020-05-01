package com.example.social.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryVariables {
    private List<Category> categoryList = new ArrayList<>();
    public List<Category> getCategories(){
        if (categoryList != null) {
            categoryList.clear();
            categoryList.add(new Category(1, "GENERAL"));
            categoryList.add(new Category(2, "ENTERTAINMENT"));
            categoryList.add(new Category(3, "BUSINESS"));
            categoryList.add(new Category(4, "HEALTH"));
            categoryList.add(new Category(5, "SCIENCE"));
            categoryList.add(new Category(6, "SPORTS"));
            categoryList.add(new Category(7, "TECHNOLOGY"));
        }
        return categoryList;
    }
}
