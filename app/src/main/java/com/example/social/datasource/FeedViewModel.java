package com.example.social.datasource;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.social.model.feed.Article;
import com.example.social.network.ArticleRepository;
import com.example.social.utils.NetworkState;
import com.example.social.model.feed.Specification;

import java.util.List;

public class FeedViewModel extends AndroidViewModel {
    private LiveData<NetworkState> networkState;
    private final ArticleRepository articleRepository;

    public FeedViewModel(@NonNull Application application) {
        super(application);
        articleRepository = ArticleRepository.getInstance(application.getApplicationContext());
        initializeViewModel(application);
    }

    private void initializeViewModel(Application application) {
        FeedDataFactory feedDataFactory = new FeedDataFactory(application);
        /*networkState = Transformations.switchMap(feedDataFactory.getMutableLiveData(),
                FeedDataSource::getNetWorkState);*/
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<List<Article>> getPagedListLiveData(Specification specification) {
        return articleRepository.getArticles(specification);
    }
}
