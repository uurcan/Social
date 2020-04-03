package com.example.mvvmapplication.datasource;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.example.mvvmapplication.model.Article;
import com.example.mvvmapplication.constants.Constants;
import com.example.mvvmapplication.model.Feed;
import com.example.mvvmapplication.utils.NetworkState;
import com.example.mvvmapplication.Application;

public class FeedDataSource extends PageKeyedDataSource<Long, Article> implements Constants {

    private MutableLiveData<NetworkState> netWorkState;
    private MutableLiveData<NetworkState> initialLoading;
    private Application application;
    private static final String TAG = FeedDataSource.class.getSimpleName();

    FeedDataSource(Application application){
        this.application = application;
        netWorkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Long, Article> callback) {
        netWorkState.postValue(NetworkState.LOADING);
        initialLoading.postValue(NetworkState.LOADING);

        application.getRestApi().fetchFeedByLanguage("tr",API_KEY,1,params.requestedLoadSize).enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(@NonNull  Call<Feed> call,@NonNull Response<Feed> response) {
               if (response.isSuccessful()){
                   if (response.body() != null) {
                       callback.onResult(response.body().getArticles(),null,2L);
                       initialLoading.postValue(NetworkState.LOADED);
                       netWorkState.postValue(NetworkState.LOADED);
                   }
               } else {
                   initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED,response.message()));
                   netWorkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
               }
            }
            @Override
            public void onFailure(@NonNull  Call<Feed> call,@NonNull Throwable t) {
                String errorMessage = t.getMessage();
                netWorkState.postValue(new NetworkState(NetworkState.Status.FAILED,errorMessage));
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Article> callback) {
        //empty method
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Long> params, @NonNull final LoadCallback<Long, Article> callback) {
        Log.i(TAG,"Loading Range "+ params.key+ "Count "+params.requestedLoadSize);
        netWorkState.postValue(NetworkState.LOADING);
        application.getRestApi().fetchFeedByLanguage("tr",API_KEY,params.key,params.requestedLoadSize).enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(@NonNull Call<Feed> call,@NonNull Response<Feed> response) {
                if (response.isSuccessful()){
                    long nextKey;
                    if (response.body() != null) {
                        nextKey = (params.key.equals(response.body().getTotalResults())) ? 0 : params.key + 1;
                        callback.onResult(response.body().getArticles(),nextKey);
                    }
                    netWorkState.postValue(NetworkState.LOADING);
                }else {
                    netWorkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Feed> call,@NonNull Throwable t) {
                String errorMessage = t.getMessage();
                netWorkState.postValue(new NetworkState(NetworkState.Status.FAILED,errorMessage));
            }
        });
    }
    MutableLiveData<NetworkState> getNetWorkState() {
        return netWorkState;
    }

}
