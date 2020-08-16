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
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Article> article);

    @Query("SELECT * FROM saved_article order by article_id desc")
    LiveData<List<Article>> getSavedArticles();

    @Query("DELETE FROM saved_article where article_id=:articleId")
    void removeSavedArticle(int articleId);

    @Query("SELECT * FROM articles order by id")
    LiveData<List<Article>> getArticles();

}
