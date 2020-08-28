package com.example.social.database;

import androidx.annotation.NonNull;
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
    void bulkInsert(List<Article> articles);

    @Query("SELECT * FROM articles order by id")
    LiveData<List<Article>> getArticles();

    @Query("SELECT COUNT(article_id) > 0 FROM SAVED_ARTICLE where article_id = :articleID")
    LiveData<Boolean> isSavedArticle(int articleID);
}
