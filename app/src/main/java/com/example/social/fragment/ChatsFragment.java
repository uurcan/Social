package com.example.social.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.social.R;
import com.example.social.databinding.ChatsFragmentBinding;
import com.example.social.model.ChatList;
import com.example.social.model.Contact;
import com.example.social.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private ChatsFragmentBinding chatsFragmentBinding;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private List<Contact> contactList;
    private List<ChatList> chatLists;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatsFragmentBinding =  DataBindingUtil.inflate(inflater,R.layout.fragment_chats,container,false);
        chatsFragmentBinding.recyclerChatList.setHasFixedSize(true);
        chatsFragmentBinding.recyclerChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        contactList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Messages messages = snapshot.getValue(Messages.class);
                    if (messages.getSender().equals(firebaseUser.getUid())){
                       //todo : chatList implementation !
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return chatsFragmentBinding.getRoot();
    }
}
