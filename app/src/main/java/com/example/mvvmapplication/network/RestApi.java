package com.example.mvvmapplication.network;

import com.example.mvvmapplication.model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApi {
    @GET("/v2/everything")
    Call<Feed> fetchFeed(@Query("q") String q,
                         @Query("apiKey") String apiKey,
                         @Query("page") long page,
                         @Query("pageSize") int pageSize);
}