package com.example.social.listener;

import com.example.social.model.feed.Article;

public interface OnSavedItemClickListener {
    void onSavedItemClick(Article article);
    void onSavedItemLongClick(Article article);
}
