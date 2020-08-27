package com.example.social.model.feed;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.sql.Timestamp;

@Entity(foreignKeys =  @ForeignKey(
        entity = Article.class,
        parentColumns = "id",
        childColumns = "article_id"),
        indices = {@Index(value = "article_id")},
        tableName = "saved_article"
)
public class SavedArticle {
    @PrimaryKey
    @ColumnInfo(name = "article_id")
    private final int articleID;

    public SavedArticle(int articleID){
        this.articleID = articleID;
    }


    public int getArticleID() {
        return articleID;
    }
}
