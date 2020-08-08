package com.example.social.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.social.App;
import com.example.social.R;
import com.example.social.adapter.MessageAdapter;
import com.example.social.constants.Constants;
import com.example.social.databinding.ActivityMessagingBinding;
import com.example.social.model.messaging.Contact;
import com.example.social.model.messaging.Messages;
import com.example.social.model.notification.Data;
import com.example.social.model.notification.Sender;
import com.example.social.model.notification.Status;
import com.example.social.model.notification.Token;
import com.example.social.network.NotificationFactory;
import com.example.social.network.NotificationService;
import com.example.social.utils.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends AppCompatActivity implements View.OnClickListener {
    private Bundle bundle;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private TextView toolbarUsername,toolbarUserStatus;
    private ImageView toolbarUserProfile;
    private ActivityMessagingBinding activityMessagingBinding;
    private MessageAdapter messageAdapter;
    private List<Messages> messagesList;
    private String userID;
    private App application = new App();
    private ValueEventListener eventListener;
    private NotificationService notificationService;
    private boolean isNotified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessagingBinding = DataBindingUtil.setContentView(this,R.layout.activity_messaging);
        notificationService = NotificationFactory.getClient("https://fcm.googleapis.com/").create(NotificationService.class);
        checkEditTextInput();
        initializeComponents();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            userID = bundle.getString(Constants.USER_ID, "");
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Contact contact = dataSnapshot.getValue(Contact.class);
                    if (contact != null) {
                        String typingStatus = Objects.requireNonNull(dataSnapshot.child("typingStatus").getValue()).toString();
                        toolbarUserProfile.setVisibility(View.VISIBLE);
                        toolbarUserStatus.setVisibility(View.VISIBLE);
                        toolbarUsername.setTextSize(15);
                        toolbarUsername.setText(contact.getUsername());

                        if (typingStatus.equals(firebaseUser.getUid())){
                            toolbarUserStatus.setText(R.string.text_Typing);
                        } else{
                            toolbarUserStatus.setText(contact.getStatus());
                        }

                        if (contact.getImageURL().equals("default")) {
                            toolbarUserProfile.setImageResource(R.drawable.default_profile_picture_white);
                        } else {
                            Glide.with(getApplicationContext()).load(contact.getImageURL())
                                    .into(toolbarUserProfile);
                        }
                        readFirebaseMessage(firebaseUser.getUid(),contact.getId());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        seenMessage(userID);
    }
    private void checkEditTextInput(){
        activityMessagingBinding.edtMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0){
                    checkTypingStatus("default");
                } else {
                    checkTypingStatus(userID);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void sendMessage(String sender,final String receiver,String message){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(Constants.SENDER,sender);
        hashMap.put(Constants.RECEIVER,receiver);
        hashMap.put(Constants.MESSAGE,message);
        hashMap.put(Constants.IS_SEEN,false);

        databaseReference.child("Chats").push().setValue(hashMap);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    reference.child("id").setValue(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userID)
                .child(firebaseUser.getUid());
        chatReference.child("id").setValue(firebaseUser.getUid());
        final String msg = message;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                if (isNotified){
                    if (contact != null) {
                        sendNotification(receiver,contact.getUsername(),msg);
                    }
                } isNotified = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username,final  String msg) {
        DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference("Tokens");
        tokenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.drawable.application_logo_black, username + " : " + msg,"New Message",userID);
                    if (token != null) {
                        Sender sender = new Sender(data,token.getToken());
                        notificationService.sendNotification(sender).enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@Nullable Call<Status> call,@Nullable Response<Status> response) {
                                if (response != null && response.code() == 200) {
                                    if (response.body() != null) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(getApplicationContext(), "Failed to push notification", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@Nullable Call<Status> call,@Nullable Throwable t) {
                                //empty method
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(String userID) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Messages messages = snapshot.getValue(Messages.class);
                    if (messages != null && messages.getReceiver().equals(firebaseUser.getUid())
                            && messages.getSender().equals(userID)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(Constants.IS_SEEN, true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgSendMessage:
                sendFirebaseMessage();
                break;
        }
    }
    private void sendFirebaseMessage(){
        String message = Objects.requireNonNull(activityMessagingBinding.edtMessageInput.getText()).toString();
        if (!message.equals("")){
            isNotified = true;
            sendMessage(firebaseUser.getUid(),bundle.getString(Constants.USER_ID),message);
            activityMessagingBinding.edtMessageInput.setText("");
        } else {
            Toast.makeText(this, "No message defined !", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkTypingStatus(String aDefault) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap <String,Object> hashMap = new HashMap<>();
        hashMap.put("typingStatus",aDefault);
        databaseReference.updateChildren(hashMap);
    }

    private void readFirebaseMessage(String userID,String companyID){
        messagesList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Messages messages = snapshot.getValue(Messages.class);
                    if (messages != null) {
                        if (messages.getReceiver().equals(userID) && messages.getSender().equals(companyID) ||
                            messages.getReceiver().equals(companyID) && messages.getSender().equals(userID)) {
                            messagesList.add(messages);
                        }
                    }
                    messageAdapter = new MessageAdapter(messagesList,getApplicationContext());
                    activityMessagingBinding.recyclerMessagingField.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initializeComponents(){
        initializeToolbar();
        toolbarUsername = findViewById(R.id.username);
        toolbarUserProfile = findViewById(R.id.profile_image);
        toolbarUserProfile.setOnClickListener(this);
        toolbarUserStatus = findViewById(R.id.status);
        activityMessagingBinding.imgSendMessage.setOnClickListener(this);
        activityMessagingBinding.recyclerMessagingField.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        activityMessagingBinding.recyclerMessagingField.setLayoutManager(linearLayoutManager);
    }
    private void initializeToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    @Override
    public void onResume() {
        super.onResume();
        application.setUserStatus("online");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener(eventListener);
        application.setUserStatus(DateUtils.getLocalTime(getApplicationContext()));
        checkTypingStatus("default");
    }
}
