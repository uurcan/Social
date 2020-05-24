package com.example.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.social.R;
import com.example.social.adapter.MessagingPagerAdapter;
import com.example.social.constants.Constants;
import com.example.social.databinding.FragmentMessageBinding;
import com.example.social.listener.ContactsClickListener;
import com.example.social.model.messaging.Contact;
import com.example.social.ui.MessagingActivity;
import com.example.social.utils.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MessageFragment extends Fragment implements ContactsClickListener {
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
        FragmentMessageBinding fragmentMessageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (getActivity() != null) {
            MessagingPagerAdapter messagingPagerAdapter = new MessagingPagerAdapter(
                    getActivity().getSupportFragmentManager(), 1
            );
            messagingPagerAdapter.addFragment(new ChatsFragment(), "Chats");
            messagingPagerAdapter.addFragment(new ContactsFragment(), "Contacts");
            fragmentMessageBinding.viewPager.setAdapter(messagingPagerAdapter);
            fragmentMessageBinding.tabLayout.setupWithViewPager(fragmentMessageBinding.viewPager);
        }
        return fragmentMessageBinding.getRoot();
    }

    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(getActivity(), MessagingActivity.class);
        intent.putExtra(Constants.USER_ID,contact.getId());
        startActivity(intent);
    }

}
