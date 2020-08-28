package com.example.social.model.feed;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(foreignKeys =  @ForeignKey(
        entity = Article.class,
        parentColumns = "id",
        childColumns = "article_id"),
        indices = {@Index(value = "article_id",unique = true)},
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
