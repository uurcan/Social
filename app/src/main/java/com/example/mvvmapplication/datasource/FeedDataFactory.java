package com.example.mvvmapplication.datasource;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.mvvmapplication.view.AppController;

public class FeedDataFactory extends DataSource.Factory {
    private MutableLiveData<FeedDataSource> mutableLiveData;
    private AppController appController;

    FeedDataFactory(AppController appController) {
        this.appController = appController;
        mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        FeedDataSource feedDataSource = new FeedDataSource(appController);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }

    MutableLiveData<FeedDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
