package com.example.social.model.feed;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(foreignKeys =  @ForeignKey(
        entity = Article.class,
        parentColumns = "id",
        childColumns = "article_id"),
        indices = {@Index(value = "news_id")},
        tableName = "saved_article"
)
public class SavedArticle {
    @ColumnInfo(name = "article_id")
    private final int articleID;
    @PrimaryKey
    @ColumnInfo(name = "time_saved")
    private Timestamp timestamp;

    public SavedArticle(int articleID){
        this.articleID = articleID;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getArticleID() {
        return articleID;
    }
}
