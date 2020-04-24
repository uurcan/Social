package com.example.social.datasource;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;


public class FeedDataFactory {
    private MutableLiveData<FeedDataSource> mutableLiveData;
    private Application application;

    FeedDataFactory(Application application) {
        this.application = application;
        mutableLiveData = new MutableLiveData<>();
    }

    MutableLiveData<FeedDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
