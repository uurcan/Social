package com.example.social.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.social.model.feed.Article;

import java.util.List;

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void bulkInsert(List<Article> articles);

    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getArticles();

    @Query("SELECT * FROM articles WHERE category=:category ORDER BY published_at DESC")
    LiveData<List<Article>> getArticleByCategory(String category);
}
