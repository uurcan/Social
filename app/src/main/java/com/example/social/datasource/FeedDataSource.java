package com.example.social.datasource;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.social.constants.Constants;
import com.example.social.utils.NetworkState;

public class FeedDataSource implements Constants {

    private MutableLiveData<NetworkState> netWorkState;
    private MutableLiveData<NetworkState> initialLoading;
    private Application application;
    private static final String TAG = FeedDataSource.class.getSimpleName();

    FeedDataSource(Application application){
        this.application = application;
        netWorkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();
    }

    MutableLiveData<NetworkState> getNetWorkState() {
        return netWorkState;
    }

}
