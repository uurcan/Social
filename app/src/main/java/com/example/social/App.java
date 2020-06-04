package com.example.social;

import com.example.social.listener.ConnectivityReceiverListener;
import com.example.social.services.ConnectivityReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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

    public void setUserStatus (String status){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users").child(firebaseUser.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            databaseReference.updateChildren(hashMap);
        }
    }
}
