package com.example.mvvmapplication;

import android.content.Context;

import com.example.mvvmapplication.network.RestApi;
import com.example.mvvmapplication.network.RestApiFactory;

import io.reactivex.Scheduler;

public class Application extends android.app.Application {
    private RestApi restApi;
    private Scheduler scheduler;
    private static Application getInstance(Context context){
        return (Application) context.getApplicationContext();
    }
    public static Application get(Context context){
        return  (Application) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Application create(Context context) {
        return Application.get(context);
    }
    public RestApi getRestApi() {
        if (restApi == null){
            restApi = RestApiFactory.create();
        }
        return restApi;
    }
}
