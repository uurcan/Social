package com.example.social;

import com.example.social.listener.ConnectivityReceiverListener;
import com.example.social.receivers.ConnectivityReceiver;

public class App extends android.app.Application{
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static synchronized App getInstance(){
        return instance;
    }
    public void setConnectivityListener(ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
