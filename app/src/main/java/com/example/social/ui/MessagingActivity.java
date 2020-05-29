package com.example.social.ui;

import android.content.Intent;
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
    private HashMap<String,Object> hashMap = new HashMap<>();
    private ValueEventListener eventListener;

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
        checkEditTextInput();
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
            seenMessage(userID);
        }
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
    private void sendMessage(String sender,String receiver,String message){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        hashMap.put(Constants.SENDER,sender);
        hashMap.put(Constants.RECEIVER,receiver);
        hashMap.put(Constants.MESSAGE,message);
        hashMap.put(Constants.IS_SEEN,false);

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
    private void seenMessage(String userID) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Messages messages = snapshot.getValue(Messages.class);
                    if (messages != null && messages.getReceiver().equals(firebaseUser.getUid())
                            && messages.getSender().equals(userID)) {
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

    private void checkTypingStatus(String aDefault) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        hashMap.put("typingStatus",aDefault);
        databaseReference.updateChildren(hashMap);
    }

    private void readFirebaseMessage(String userID,String companyID){
        messagesList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
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
        databaseReference.removeEventListener(eventListener);
        application.setUserStatus(DateUtils.getLocalTime(getApplicationContext()));
        checkTypingStatus("default");
    }
}
