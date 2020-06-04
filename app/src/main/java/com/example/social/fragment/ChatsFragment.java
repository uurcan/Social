package com.example.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.social.R;
import com.example.social.adapter.ContactsAdapter;
import com.example.social.constants.Constants;
import com.example.social.databinding.ChatsFragmentBinding;
import com.example.social.listener.ContactsClickListener;
import com.example.social.model.messaging.ChatList;
import com.example.social.model.messaging.Contact;

import com.example.social.model.notification.Token;
import com.example.social.ui.MessagingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment implements ContactsClickListener {
    private ChatsFragmentBinding chatsFragmentBinding;
    private DatabaseReference databaseReference;
    private List<Contact> contactList;
    private FirebaseUser firebaseUser;
    private List<ChatList> chatLists;
    private ContactsAdapter contactsAdapter;
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
        chatLists = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    chatLists.add(chatList);
                }
                readFirebaseChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return chatsFragmentBinding.getRoot();
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void readFirebaseChats() {
        contactList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        contactsAdapter = new ContactsAdapter(getContext(),contactList,this,true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Contact contact = snapshot.getValue(Contact.class);
                    for (ChatList chatList : chatLists){
                        if (contact != null) {
                            if (contact.getId().equals(chatList.getId())) {
                                contactList.add(contact);
                            }
                        }
                    }
                }
                chatsFragmentBinding.recyclerChatList.setAdapter(contactsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //empty method
            }
        });

    }

    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(getActivity(), MessagingActivity.class);
        intent.putExtra(Constants.USER_ID,contact.getId());
        startActivity(intent);
    }
}
