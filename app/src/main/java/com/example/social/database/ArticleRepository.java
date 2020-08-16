package com.example.social.database;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.social.model.feed.Article;
import com.example.social.model.feed.Specification;
import com.example.social.network.RestApiFactory;

import java.util.List;

public class ArticleRepository {
    private final ArticleDao articlesDao;
    private static final Object LOCK = new Object();
    private static ArticleRepository articleRepository;
    private final MutableLiveData<List<Article>> networkLiveData;
    private final RestApiFactory restApiFactory;

    private ArticleRepository(Context context){
        articlesDao = ArticleDatabase.getInstance(context).articleDao();
        restApiFactory = RestApiFactory.getInstance(context);
        networkLiveData = new MutableLiveData<>();
        networkLiveData.observeForever(articles -> {
            if (articles != null){
                articlesDao.insert(articles);
            }
        });
    }
    public synchronized static ArticleRepository getInstance(Context context){
        if (articleRepository == null){
            synchronized (LOCK){
                articleRepository = new ArticleRepository(context);
            }
        }
        return articleRepository;
    }
    public LiveData<List<Article>> getArticles(final Specification specification){
        final LiveData<List<Article>> networkData = restApiFactory.getArticles(specification);
        networkData.observeForever(new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null){
                    networkLiveData.setValue(articles);
                    networkData.removeObserver(this);
                }
            }
        });
        return networkData;
    }
}
