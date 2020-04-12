package com.example.social.utils;

import com.example.social.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryVariables {
    private static List<Category> categoryList = new ArrayList<>();
    public static List<Category> getCategories(){
        categoryList.add(new Category(1,"BUSINESS"));
        categoryList.add(new Category(2,"ENTERTAINMENT"));
        categoryList.add(new Category(3,"GENERAL"));
        categoryList.add(new Category(4,"HEALTH"));
        categoryList.add(new Category(5,"SCIENCE"));
        categoryList.add(new Category(6,"SPORTS"));
        categoryList.add(new Category(7,"MOVIES"));
        categoryList.add(new Category(8,"TECHNOLOGY"));
        return categoryList;
    }
}
