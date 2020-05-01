package com.example.social.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import com.example.social.App;
import com.example.social.listener.ConnectivityReceiverListener;

public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;
    public boolean isConnected;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (connectivityManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                    isConnected = capabilities != null
                            && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
                }
            }
        }else {
            if (connectivityManager != null) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    isConnected = activeNetwork.isConnectedOrConnecting();
                }
            }
        }
        if (connectivityReceiverListener != null){
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            //todo: not working properly :(
        }
    }
    public static boolean isConnected(){
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) App
                                        .getInstance()
                                        .getApplicationContext()
                                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (connectivityManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                    isConnected = capabilities != null
                            && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
                }
            }
        }else {
            if (connectivityManager != null) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    isConnected = activeNetwork.isConnectedOrConnecting();
                }
            }
        }
        return isConnected;
    }
}
