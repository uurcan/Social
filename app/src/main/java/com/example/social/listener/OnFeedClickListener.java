package com.example.social.listener;

import com.example.social.model.feed.Article;
import com.example.social.model.feed.Category;

public interface OnFeedClickListener {
    void onFeedClick(Article article);
    void onCategoryClick(Category category);
}
