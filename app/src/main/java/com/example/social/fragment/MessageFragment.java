package com.example.social.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.social.R;
import com.example.social.databinding.FragmentMessageBinding;
import com.example.social.databinding.FragmentProfileBinding;

public class MessageFragment extends Fragment {
    private FragmentMessageBinding fragmentMessageBinding;
    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        this.fragmentMessageBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_message, container, false);

        return this.fragmentMessageBinding.getRoot();
    }
}
