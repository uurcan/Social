package com.example.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social.R;
import com.example.social.listener.ContactsClickListener;
import com.example.social.model.messaging.Contact;
import com.example.social.model.messaging.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>
                             implements Filterable {
    private Context context;
    private List<Contact> contactList;
    private List<Contact> contactListFilter;
    private ContactsClickListener contactsClickListener;
    private boolean isOnChatScreen;
    private String lastMessage;

    public ContactsAdapter(Context context, List<Contact> contactList,ContactsClickListener contactsClickListener,boolean isOnChatScreen) {
        this.context = context;
        this.contactList = contactList;
        this.isOnChatScreen = isOnChatScreen;
        this.contactsClickListener = contactsClickListener;
        this.contactListFilter = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contactListFilter.get(position);
        holder.textUsername.setText(contact.getUsername());

        if (contact.getImageURL().equals("default")){
            holder.userProfileImage.setImageResource(R.drawable.default_user);
        } else {
            Glide.with(context).load(contact.getImageURL()).into(holder.userProfileImage);
        }
        if (isOnChatScreen){
            holder.textLastMessage.setVisibility(View.VISIBLE);
            holder.textStatus.setVisibility(View.GONE);
            getLastMessage(contact.getId(),holder.textLastMessage);
        }else {
            holder.textLastMessage.setVisibility(View.GONE);
            holder.textStatus.setVisibility(View.VISIBLE);
            holder.textStatus.setText(contact.getDescription());
        }
        if (isOnChatScreen){
            if (contact.getStatus().equals("online")){
                holder.imageUserOnline.setVisibility(View.VISIBLE);
                holder.imageUserOffline.setVisibility(View.GONE);
            } else {
                holder.imageUserOffline.setVisibility(View.VISIBLE);
                holder.imageUserOnline.setVisibility(View.GONE);
            }
        }
        holder.itemView.setOnClickListener(v -> contactsClickListener.onContactClick(contactListFilter.get(position)));
    }

    @Override
    public int getItemCount() {
        return contactListFilter.size();
    }

    private void getLastMessage(String userID,TextView tvLastMessage){
        lastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Messages messages = snapshot.getValue(Messages.class);
                    if (messages != null){
                        if (firebaseUser != null){
                            if (messages.getReceiver().equals(firebaseUser.getUid()) && messages.getSender().equals(userID)
                                    || messages.getReceiver().equals(userID) && messages.getSender().equals(firebaseUser.getUid())){
                                    lastMessage = messages.getMessage();
                            }
                        }
                    }
                }
                if ("default".equals(lastMessage)) {
                    tvLastMessage.setText(context.getString(R.string.text_socialize));
                } else {
                    tvLastMessage.setText(lastMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint.toString().isEmpty()){
                contactListFilter = contactList;
            } else {
                List<Contact> filteredList = new ArrayList<>();
                for (Contact contact: contactList){
                    if (contact.getUsername().contains(constraint.toString().toLowerCase())){
                        filteredList.add(contact);
                    }
                }
                contactListFilter = filteredList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = contactListFilter;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactListFilter = (List<Contact>) results.values;
            notifyDataSetChanged();
        }
    };
    @Override
    public Filter getFilter() {
        return filter;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textUsername,textStatus,textLastMessage;
        private ImageView userProfileImage;
        private ImageView imageUserOnline,imageUserOffline;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
            textUsername = itemView.findViewById(R.id.textUsernameMsg);
            textStatus = itemView.findViewById(R.id.userStatus);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            imageUserOnline = itemView.findViewById(R.id.imgUserStatusOnline);
            imageUserOffline = itemView.findViewById(R.id.imgUserStatusOffline);
        }
    }
}
