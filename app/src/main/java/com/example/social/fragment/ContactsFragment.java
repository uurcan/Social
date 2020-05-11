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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.social.R;
import com.example.social.adapter.ContactsAdapter;
import com.example.social.constants.Constants;
import com.example.social.databinding.UserFragmentBinding;
import com.example.social.listener.ContactsClickListener;
import com.example.social.model.Contact;
import com.example.social.ui.MessagingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements ContactsClickListener {
    private ContactsAdapter contactsAdapter;
    private List<Contact> contactList;
    private UserFragmentBinding userFragmentBinding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_users,container,false);
        userFragmentBinding.recyclerUserList.setHasFixedSize(true);
        userFragmentBinding.recyclerUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        contactList = new ArrayList<>();
        readUserListData();
        contactsAdapter = new ContactsAdapter(getContext(),contactList);
        contactsAdapter.setContactsClickListener(this);
        return userFragmentBinding.getRoot();
    }

    private void readUserListData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Contact contact = snapshot.getValue(Contact.class);
                    if (firebaseUser != null && contact != null && !contact.getId().equals(firebaseUser.getUid())) {
                        contactList.add(contact);
                    }
                }
                userFragmentBinding.recyclerUserList.setAdapter(contactsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
