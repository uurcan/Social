package com.example.social.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.social.model.notification.Token;
import com.example.social.ui.MessagingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseNotification extends FirebaseMessagingService{
    private int requestCode,currentCode;
    private PendingIntent pendingIntent;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (user != null){
            updateToken(refreshedToken);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String toSender = remoteMessage.getData().get("toSender");
        String user = remoteMessage.getData().get("user");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = getSharedPreferences("Notifications",MODE_PRIVATE);
        String currentUser = preferences.getString("currentUser","");
        if (firebaseUser != null && toSender != null && user != null && toSender.equals(firebaseUser.getUid())) {
            if (!currentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
            }
        }
    }
    private void updateToken(String tokenRefreshed){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefreshed);
        if (user != null) {
            databaseReference.child(user.getUid()).setValue(token);
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        initializeVariables(remoteMessage);
        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getONotifications(remoteMessage,pendingIntent);
        if (requestCode > 0) currentCode = requestCode;
        if (oreoNotification.getManager() != null) {
            oreoNotification.getManager().notify(currentCode,builder.build());
        }
    }

    private void sendNotification(RemoteMessage remoteMessage){
        initializeVariables(remoteMessage);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("user"))))
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (requestCode > 0) currentCode = requestCode;
        if (manager != null) {
            manager.notify(currentCode,builder.build());
        }
    }

    private void initializeVariables(RemoteMessage remoteMessage){
        String userID =  remoteMessage.getData().get("user");
        requestCode = 0;
        if (userID != null) {
            requestCode = Integer.parseInt(userID.replaceAll("[\\D]",""));
        }
        currentCode = 0;
        Intent intent = new Intent(this, MessagingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID",userID);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this,requestCode,intent,PendingIntent.FLAG_ONE_SHOT);
    }
}
