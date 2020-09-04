package com.example.social.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.social.model.feed.Article;
import com.example.social.model.feed.SavedArticle;
import com.example.social.utils.DatabaseConverters;

@Database(entities = {Article.class,SavedArticle.class}, version = 8,exportSchema = false)
@TypeConverters(DatabaseConverters.class)
public abstract class ArticleDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "articles";
    private static ArticleDatabase instance;

    public static ArticleDatabase getInstance(Context context){
        if (instance == null){
            synchronized (LOCK){
                instance =  Room.databaseBuilder(
                        context.getApplicationContext(),
                        ArticleDatabase.class,
                        DATABASE_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return instance;
    }
    public abstract ArticleDao articleDao();
    public abstract SavedArticleDao savedArticleDao();

}
