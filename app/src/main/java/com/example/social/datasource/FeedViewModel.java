package com.example.social.datasource;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;


import com.example.social.model.Article;
import com.example.social.utils.NetworkState;
import com.example.social.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FeedViewModel extends ViewModel {
    private Application application;
    private LiveData<PagedList<Article>> articleLiveData;
    private LiveData<NetworkState> networkState;

    public FeedViewModel(@NonNull Application application) {
        this.application = application;
        initializeViewModel();
    }

    private void initializeViewModel() {
        FeedDataFactory feedDataFactory = new FeedDataFactory(application);
        Executor executor = Executors.newFixedThreadPool(5);
        networkState = Transformations.switchMap(feedDataFactory.getMutableLiveData(),
                FeedDataSource::getNetWorkState);
        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPageSize(20)
                .setPrefetchDistance(4)
                .build();
        articleLiveData = (new LivePagedListBuilder(feedDataFactory,config))
                .setFetchExecutor(executor)
                .build();
    }
    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<Article>> getPagedListLiveData() {
        return articleLiveData;
    }
}
