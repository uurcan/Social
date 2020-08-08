package com.example.social.fragment;

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
import com.example.social.databinding.FragmentMessageBinding;


public class MessageFragment extends Fragment  {
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
        if (getActivity() != null) {
            MessagingPagerAdapter messagingPagerAdapter = new MessagingPagerAdapter(
                    getChildFragmentManager(), 1
            );
            messagingPagerAdapter.addFragment(new ChatsFragment(), "Chats");
            messagingPagerAdapter.addFragment(new ContactsFragment(), "Contacts");
            fragmentMessageBinding.viewPager.setAdapter(messagingPagerAdapter);
            fragmentMessageBinding.tabLayout.setupWithViewPager(fragmentMessageBinding.viewPager);
        }
        return fragmentMessageBinding.getRoot();
    }

}
