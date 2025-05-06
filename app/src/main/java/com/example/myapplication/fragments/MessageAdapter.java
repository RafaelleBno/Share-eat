package com.example.myapplication.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;  // Utiliser la classe Message définie dans le fragment

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);  // Assurez-vous que la classe Message est utilisée ici
        holder.userNameTextView.setText(message.getUserName());
        holder.statusTextView.setText(message.getStatus());
        holder.timeTextView.setText(message.getTime());
        holder.avatarImageView.setImageResource(message.getAvatarResId());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder pour chaque élément de la liste
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView statusTextView;
        TextView timeTextView;
        ImageView avatarImageView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
        }
    }
}

