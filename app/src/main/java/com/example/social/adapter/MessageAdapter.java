package com.example.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social.R;
import com.example.social.model.messaging.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MESSAGE_TYPE_LEFT = 0;
    private static final int MESSAGE_TYPE_RIGHT = 1;
    private List<Messages> messagesList;
    private Context context;

    public MessageAdapter(List<Messages> messagesList,Context context){
        this.messagesList = messagesList;
        this.context = context;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.messaging_item_left, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.messaging_item_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Messages message = messagesList.get(position);
        holder.textMessage.setText(String.valueOf(message.getMessage()));
        if (position == messagesList.size()-1){
            if (message.isSeen()){
                holder.txtIsSeen.setText(R.string.textSeen);
            } else {
                holder.txtIsSeen.setText(R.string.textDelivered);
            }
        } else {
            holder.txtIsSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView txtIsSeen;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.txtMessage);
            txtIsSeen = itemView.findViewById(R.id.textIsSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (messagesList.get(position).getSender().equals(firebaseUser.getUid())) {
                return MESSAGE_TYPE_RIGHT;
            } else {
                return MESSAGE_TYPE_LEFT;
            }
        } return 0;
    }
}
