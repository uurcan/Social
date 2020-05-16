package com.example.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social.R;
import com.example.social.listener.ContactsClickListener;
import com.example.social.model.messaging.Contact;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private Context context;
    private List<Contact> contactList;
    private ContactsClickListener contactsClickListener;
    //private boolean isOnline;

    public ContactsAdapter(Context context, List<Contact> contactList,ContactsClickListener contactsClickListener) {
        this.context = context;
        this.contactList = contactList;
        //this.isOnline = isOnline;
        this.contactsClickListener = contactsClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.textUsername.setText(contact.getUsername());
        holder.textStatus.setText(contact.getDescription());
        if (contact.getImageURL().equals("default")){
            holder.userProfileImage.setImageResource(R.drawable.application_logo_black);
        } else {
            Glide.with(context).load(contact.getImageURL()).into(holder.userProfileImage);
        }
        /*if (isOnline){
            if (contact.getStatus().equals("online")){
                holder.imageUserOnline.setVisibility(View.VISIBLE);
                holder.imageUserOffline.setVisibility(View.GONE);
            }else {
                holder.imageUserOffline.setVisibility(View.VISIBLE);
                holder.imageUserOnline.setVisibility(View.GONE);
            }
        }*/
        holder.itemView.setOnClickListener(v -> contactsClickListener.onContactClick(contactList.get(position)));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textUsername,textStatus;
        private ImageView userProfileImage;
        private ImageView imageUserOnline,imageUserOffline;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsernameMsg);
            textStatus = itemView.findViewById(R.id.userStatus);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            //imageUserOnline = itemView.findViewById(R.id.imgUserStatusOnline);
            //imageUserOffline = itemView.findViewById(R.id.imgUserStatusOffline);
        }
    }
}
