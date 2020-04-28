package com.example.social.listener;

import android.view.View;

import com.example.social.model.Article;

public interface OnFeedClickListener {
    void onFeedClick(Article article);
    void onCategoryClick(View view,int position);
}
