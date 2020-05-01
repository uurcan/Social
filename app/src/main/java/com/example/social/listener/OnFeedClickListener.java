package com.example.social.listener;

import com.example.social.model.Article;
import com.example.social.model.Category;

public interface OnFeedClickListener {
    void onFeedClick(Article article);
    void onCategoryClick(Category category);
}
