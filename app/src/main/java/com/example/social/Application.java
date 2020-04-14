package com.example.social;

import android.content.Context;

import com.example.social.network.RestApi;
import com.example.social.network.RestApiFactory;

import io.reactivex.Scheduler;

public class Application extends android.app.Application {
    private static RestApi restApi;
    private Scheduler scheduler;
    /** private static Application getInstance(Context context){
        return (Application) context.getApplicationContext();
    }*/
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
    public static RestApi getRestApi() {
        if (restApi == null){
            restApi = RestApiFactory.create();
        }
        return restApi;
    }
}
