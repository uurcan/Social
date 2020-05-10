package com.example.social.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.social.R;
import com.example.social.adapter.MessagingPagerAdapter;
import com.example.social.databinding.FragmentMessageBinding;
import com.example.social.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment {
    private FragmentMessageBinding fragmentMessageBinding;
    private CircleImageView circleImageView;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        this.fragmentMessageBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_message, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("Messaging");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MessagingPagerAdapter messagingPagerAdapter = new MessagingPagerAdapter(
                        getActivity().getSupportFragmentManager(),1
                );
                messagingPagerAdapter.addFragment(new ChatsFragment(), "Chats");
                messagingPagerAdapter.addFragment(new ContactsFragment(),"Contacts");
                fragmentMessageBinding.viewPager.setAdapter(messagingPagerAdapter);
                fragmentMessageBinding.tabLayout.setupWithViewPager(fragmentMessageBinding.viewPager);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return this.fragmentMessageBinding.getRoot();
    }
}
