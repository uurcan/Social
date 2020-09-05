package com.example.social.services;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class AppExecutor {
    private static final Object LOCK = new Object();
    private static AppExecutor instance;
    private final Executor diskIO;
    private final Executor mainThread;

    private AppExecutor(Executor diskIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    public static AppExecutor getInstance() {
        if (instance == null){
            synchronized (LOCK){
                instance = new AppExecutor(Executors.newSingleThreadExecutor(),
                        new MainThreadExecutor());
            }
        } return instance;
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor{
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}

