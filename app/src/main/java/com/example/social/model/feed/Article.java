package com.example.social.model.feed;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;

@Entity(tableName = "articles",indices = {@Index(value = "title",unique = true)})
public class Article implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @Expose(serialize = false,deserialize = false)
    public int id;
    @ColumnInfo(name = "author")
    private final String author;
    @ColumnInfo(name = "title")
    private final String title;
    @ColumnInfo(name = "description")
    private final String description;
    @ColumnInfo(name = "url")
    private final String url;
    @ColumnInfo(name = "publishedAt")
    private final String publishedAt;
    @ColumnInfo(name = "urlToImage")
    private final String urlToImage;
    @Embedded(prefix = "source")
    private final Sources source;
    @ColumnInfo(name = "content")
    private final String content;
    @ColumnInfo(name = "category")
    @Expose(serialize = false,deserialize = false)
    private String category;
    
    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public Sources getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }


    @Override
    public int describeContents() {
        return 0;
    }
    public Article(String author, String title, String description, String url, String publishedAt, String urlToImage, Sources source, String content,String category) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
        this.urlToImage = urlToImage;
        this.source = source;
        this.content = content;
        this.category = category;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.author);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeSerializable(this.publishedAt);
        dest.writeString(this.urlToImage);
        dest.writeParcelable(this.source, flags);
        dest.writeString(this.content);
        dest.writeString(this.category);
    }

    protected Article(Parcel in) {
        this.id = in.readInt();
        this.author = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.url = in.readString();
        this.publishedAt = in.readString();
        this.urlToImage = in.readString();
        this.source = in.readParcelable(Sources.class.getClassLoader());
        this.content = in.readString();
        this.category = in.readString();
    }

    public String getCategory() {
        return category;
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
