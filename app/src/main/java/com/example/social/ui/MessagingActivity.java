package com.example.social.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.social.R;
import com.example.social.constants.Constants;
import com.example.social.databinding.ActivityMessagingBinding;
import com.example.social.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessagingActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    TextView toolbarUsername;
    ImageView toolbarUserProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this,R.layout.activity_messaging);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbarUsername = findViewById(R.id.username);
        toolbarUserProfile = findViewById(R.id.profile_image);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String userID = bundle.getString(Constants.USER_ID, "");
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Contact contact = dataSnapshot.getValue(Contact.class);
                    if (contact != null) {
                        toolbarUsername.setText(contact.getUsername());
                        if (contact.getImageURL().equals("default")) {
                            toolbarUserProfile.setImageResource(R.drawable.application_logo_white);
                        } else {
                            Glide.with(getApplicationContext()).load(contact.getImageURL())
                                    .into(toolbarUserProfile);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
