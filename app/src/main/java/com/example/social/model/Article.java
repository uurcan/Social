package com.example.social.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;

/**
 * A News Article content and it's details
 */
public class Article implements Parcelable {
    @Expose(serialize = false, deserialize = false)
    public int id;
    private final String author;
    private final String title;
    private final String description;
    private final String url;
    private final String publishedAt;
    private final String urlToImage;
    private final Sources source;
    private final String content;
    @Expose(serialize = false, deserialize = false)
    private String category;
    @Expose(serialize = false, deserialize = false)
    private Timestamp saveDate = new Timestamp(System.currentTimeMillis());

    /**
     * @param author      author of the article
     * @param title       headline or title of the article.
     * @param description description or snippet from the article.
     * @param url         The direct URL to the article
     * @param publishedAt The date and time that the article was published, in UTC (+000)
     * @param urlToImage  The URL to a relevant image for the article.
     * @param source      The identifier id and a display name name for the source this article came from.
     * @param content     The unformatted content of the article, where available. This is truncated to
     *                    260 chars for Developer plan users.
     */
    public Article(String author, String title, String description, String url, String publishedAt, String urlToImage, Sources source, String content) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
        this.urlToImage = urlToImage;
        this.source = source;
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public Timestamp getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(Timestamp saveDate) {
        this.saveDate = saveDate;
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
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", publishedAt=" + publishedAt +
                ", urlToImage='" + urlToImage + '\'' +
                ", source=" + source +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", saveDate=" + saveDate +
                '}';
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
        dest.writeSerializable(this.saveDate);
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
        this.saveDate = (Timestamp) in.readSerializable();
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
