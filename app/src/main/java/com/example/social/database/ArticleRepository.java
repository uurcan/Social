package com.example.social.database;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.social.model.feed.Article;
import com.example.social.model.feed.SavedArticle;
import com.example.social.model.feed.Specification;
import com.example.social.network.RestApiFactory;
import com.example.social.services.AppExecutor;

import java.util.List;

public class ArticleRepository {
    private final ArticleDao articlesDao;
    private static final Object LOCK = new Object();
    private static ArticleRepository articleRepository;
    private final SavedArticleDao savedArticleDao;
    private final MutableLiveData<List<Article>> networkLiveData;
    private final RestApiFactory restApiFactory;
    private AppExecutor appExecutor;

    private ArticleRepository(Context context){
        articlesDao = ArticleDatabase.getInstance(context).articleDao();
        savedArticleDao =ArticleDatabase.getInstance(context).savedArticleDao();
        restApiFactory = RestApiFactory.getInstance(context);
        networkLiveData = new MutableLiveData<>();
        appExecutor = AppExecutor.getInstance();
        networkLiveData.observeForever(articles -> {
            if (articles != null){
                appExecutor.getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        articlesDao.bulkInsert(articles);
                    }
                });
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

    public LiveData<List<Article>> getSavedArticles(){
        return savedArticleDao.getAllSaved();
    }
    public LiveData<Boolean> isSaved(int articleID){
        return savedArticleDao.isFavourite(articleID);
    }
    public void removeSavedArticle(final int articleID){
        appExecutor.getDiskIO().execute(() -> savedArticleDao.removeSaved(articleID));
    }
    public void saveArticle(final int articleID){
        appExecutor.getDiskIO().execute(() -> {
            SavedArticle article = new SavedArticle(articleID);
            savedArticleDao.insert(article);
            System.out.println("Saved in database for reference :  %s " + articleID );
        });
    }
}
