package com.example.mvvmapplication.view;

import android.app.Application;
import android.content.Context;

import com.example.mvvmapplication.network.RestApi;
import com.example.mvvmapplication.network.RestApiFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import io.reactivex.Scheduler;

public class AppController extends Application {
    private RestApi restApi;
    private Scheduler scheduler;
    private static AppController getInstance(Context context){
        return (AppController) context.getApplicationContext();
    }
    public static AppController get(Context context){
        return  (AppController) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static AppController create(Context context) {
        return AppController.get(context);
    }
    public RestApi getRestApi() {
        if (restApi == null){
            restApi = RestApiFactory.create();
        }
        return restApi;
    }
}
