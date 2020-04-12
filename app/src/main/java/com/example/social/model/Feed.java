package com.example.social.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.social.utils.ApplicationUtils;

import java.util.List;

public class Feed implements Parcelable {

    private transient long id;
    private String status;
    private long totalResults;
    private List<Article> articles;

    private Feed(Parcel in) {
        id = ApplicationUtils.getRandomNumber();
        status = in.readString();
        totalResults = in.readLong();
        articles = in.createTypedArrayList(Article.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(status);
        dest.writeLong(totalResults);
        dest.writeTypedList(articles);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }
}
