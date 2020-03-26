package com.example.mvvmapplication.datasource;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.mvvmapplication.Application;

public class FeedDataFactory extends DataSource.Factory {
    private MutableLiveData<FeedDataSource> mutableLiveData;
    private Application application;

    FeedDataFactory(Application application) {
        this.application = application;
        mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        FeedDataSource feedDataSource = new FeedDataSource(application);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }

    MutableLiveData<FeedDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
