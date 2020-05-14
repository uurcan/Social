package com.example.social.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.social.constants.Constants;
import com.example.social.model.feed.Article;
import com.example.social.model.feed.Feed;
import com.example.social.model.feed.Specification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiFactory {
    private static final String BASE_URL = "https://newsapi.org";
    private static final Object LOCK = new Object();
    private static RestApi restApi;
    private static RestApiFactory restApiFactory;

    private RestApiFactory(){}

    public static RestApiFactory getInstance(Context context) {
        if (restApi == null || restApiFactory == null) {
            synchronized (LOCK) {
                //Cache for 5mb
                Cache cache = new Cache(context.getApplicationContext().getCacheDir(), 5 * 1024 * 1024);

                Interceptor networkInterceptor = chain -> {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(1, TimeUnit.HOURS)
                            .maxStale(3, TimeUnit.DAYS)
                            .build();
                    return chain.proceed(chain.request())
                            .newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", cacheControl.toString())
                            .build();
                };
                //Logging
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                //OkHttp client builder
                OkHttpClient client = new OkHttpClient.Builder()
                        .cache(cache)
                        .addNetworkInterceptor(networkInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .build();
                Gson gson = new GsonBuilder().create();

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson));
                restApi = builder.build().create(RestApi.class);
                restApiFactory = new RestApiFactory();
            }
        }
        return restApiFactory;
    }
    public LiveData<List<Article>> getArticles(final Specification specification){
        final MutableLiveData<List<Article>> networkLiveData = new MutableLiveData<>();

        Call<Feed> networkCall = restApi.fetchFeedByLanguage
                (specification.getCountry(),specification.getCategory(), Constants.API_KEY, specification.getCurrentPage(),10);

        networkCall.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(@NonNull Call<Feed> call,@NonNull retrofit2.Response<Feed> response) {
                if (response.body() != null){
                    List<Article> articles = response.body().getArticles();
                    for (Article article : articles){
                         article.setCategory(specification.getCategory());
                    }
                    networkLiveData.setValue(articles);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Feed> call,@NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        });
        return networkLiveData;
    }
}
