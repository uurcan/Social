package com.example.social.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.social.R;
import com.example.social.databinding.ProfileFragmentBinding;
import com.example.social.model.messaging.Contact;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private ProfileFragmentBinding profileFragmentBinding;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        this.profileFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                if (getContext() != null) {
                    if (contact != null) {
                    profileFragmentBinding.profileUserName.setText(contact.getUsername());
                    if (contact.getImageURL().equals("default"))
                        profileFragmentBinding.imageViewProfile.setImageResource(R.drawable.application_logo_black);
                     else {
                        Glide.with(getContext()).load(contact.getImageURL())
                                .placeholder(R.drawable.application_logo_black)
                                .into(profileFragmentBinding.imageViewProfile);
                            }
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //empty method
            }
        });
        return this.profileFragmentBinding.getRoot();
    }
}
