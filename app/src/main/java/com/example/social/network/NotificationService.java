package com.example.social.network;

import com.example.social.model.notification.Status;
import com.example.social.model.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA0XS64GU:APA91bEpHnCQIlU7XV__FC0glMBcwtH4T18lYqx2OZVMRq5Oh8qKC6m46dhOK8OcT5GokffWL33UXbtN2vEqCKIA8kcQ8aiTwO5DM4yELepfS_BAbzSGoQSZG5SwhRU76UHa1nipJzD7"
    })
    @POST("fcm/send")
    Call<Status> sendNotification(@Body Sender body);
}
