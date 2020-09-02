package com.example.social.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.social.model.feed.Article;
import com.example.social.model.feed.SavedArticle;

import java.util.List;

@Dao
public interface SavedArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedArticle article);

    @Query("SELECT COUNT(article_id) > 0 FROM saved_article WHERE article_id = :articleId")
    LiveData<Boolean> isFavourite(int articleId);

    @Query("DELETE FROM saved_article WHERE article_id=:articleId")
    void removeSaved(int articleId);

    @Query("SELECT articles.* FROM articles, saved_article " +
            "WHERE articles.id == saved_article.article_id " )
    LiveData<List<Article>> getAllSaved();
}
