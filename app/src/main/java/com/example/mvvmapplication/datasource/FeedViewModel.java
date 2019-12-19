package com.example.mvvmapplication.datasource;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;


import com.example.mvvmapplication.model.Article;
import com.example.mvvmapplication.Utils.NetworkState;
import com.example.mvvmapplication.view.AppController;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FeedViewModel extends ViewModel {
    private AppController appController;
    private LiveData<PagedList<Article>> articleLiveData;
    private LiveData<NetworkState> networkState;

    public FeedViewModel(@NonNull AppController appController) {
        this.appController = appController;
        initializeViewModel();
    }

    private void initializeViewModel() {
        FeedDataFactory feedDataFactory = new FeedDataFactory(appController);
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
