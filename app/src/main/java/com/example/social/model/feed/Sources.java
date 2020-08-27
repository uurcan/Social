package com.example.social.model.feed;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;

public class Sources implements Parcelable {
    @ColumnInfo(name = "id")
    private final String id;
    @ColumnInfo(name = "name")
    private final String name;

    /**
     * @param id   id of the news source, example <b>cnn</b>
     * @param name display name of news source, example <b>CNN</b>
     */
    public Sources(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ArticleSource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected Sources(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Sources> CREATOR = new Parcelable.Creator<Sources>() {
        @Override
        public Sources createFromParcel(Parcel source) {
            return new Sources(source);
        }

        @Override
        public Sources[] newArray(int size) {
            return new Sources[size];
        }
    };
}
