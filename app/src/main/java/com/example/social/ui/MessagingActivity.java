package com.example.social.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.social.utils.DateUtils;
import com.example.social.utils.ImageViewUtils;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessagingBinding = DataBindingUtil.setContentView(this,R.layout.activity_messaging);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        toolbarUsername = findViewById(R.id.username);
        toolbarUserProfile = findViewById(R.id.profile_image);
        toolbarUserProfile.setOnClickListener(this);
        toolbarUserStatus = findViewById(R.id.status);
        activityMessagingBinding.imgSendMessage.setOnClickListener(this);
        activityMessagingBinding.recyclerMessagingField.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        activityMessagingBinding.recyclerMessagingField.setLayoutManager(linearLayoutManager);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            userID = bundle.getString(Constants.USER_ID, "");
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Contact contact = dataSnapshot.getValue(Contact.class);
                    if (contact != null) {
                        toolbarUserProfile.setVisibility(View.VISIBLE);
                        toolbarUserStatus.setVisibility(View.VISIBLE);
                        toolbarUsername.setTextSize(15);
                        toolbarUsername.setText(contact.getUsername());
                        toolbarUserStatus.setText(contact.getStatus());
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
    }
    private void sendMessage(String sender,String receiver,String message){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(Constants.SENDER,sender);
        hashMap.put(Constants.RECEIVER,receiver);
        hashMap.put(Constants.MESSAGE,message);
        databaseReference.child("Chats").push().setValue(hashMap);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList")
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgSendMessage:
                sendFirebaseMessage();
                break;
            case R.id.profile_image:
                ImageViewUtils.enablePopUpOnClick(this,toolbarUserProfile);
                break;
        }
    }
    private void sendFirebaseMessage(){
        String message = Objects.requireNonNull(activityMessagingBinding.edtMessageInput.getText()).toString();
        if (!message.equals("")){
            sendMessage(firebaseUser.getUid(),bundle.getString(Constants.USER_ID),message);
            activityMessagingBinding.edtMessageInput.setText("");
        } else {
            Toast.makeText(this, "No message defined !", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        application.setUserStatus("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        application.setUserStatus(DateUtils.getLocalTime(getApplicationContext()));
    }
}
