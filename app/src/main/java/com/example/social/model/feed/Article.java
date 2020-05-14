package com.example.social.model.feed;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    public int id;
    private final String author;
    private final String title;
    private final String description;
    private final String url;
    private final String publishedAt;
    private final String urlToImage;
    private final Sources source;
    private final String content;
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
