package com.example.smalltalk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smalltalk.R;
import com.example.smalltalk.models.ChatMessage;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context ctx;
    private final ArrayList<ChatMessage> chatMessages;
    private final String senderEmail;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(Context ctx, ArrayList<ChatMessage> chatMessages, String senderEmail) {
        this.ctx = ctx;
        this.chatMessages = chatMessages;
        this.senderEmail = senderEmail;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View vItemContainerSenderMessage = inflater.inflate(R.layout.item_container_sender_message, parent, false);
        View vItemContainerReceiverMessage = inflater.inflate(R.layout.item_container_receiver_message, parent, false);

        return viewType == VIEW_TYPE_SENT
                ? new SentMessageViewHolder(vItemContainerSenderMessage)
                : new ReceivedMessageViewHolder(vItemContainerReceiverMessage);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).tvTextMessage.setText(chatMessages.get(position).getMessage());
            ((SentMessageViewHolder) holder).tvMessageDateTime.setText(chatMessages.get(position).getSendedAt());
            ((SentMessageViewHolder) holder).tvUserEmail.setText(chatMessages.get(position).getSenderEmail());

            Log.d("ChatAdapter", "onBindViewHolder: " + chatMessages.get(position).getImageUrl());
            if (!chatMessages.get(position).getImageUrl().isEmpty()) {
                Glide.with(ctx)
                        .load(chatMessages.get(position).getImageUrl())
                        .into(((SentMessageViewHolder) holder).ivImageMessage);
                ((SentMessageViewHolder) holder).ivImageMessage.setVisibility(View.VISIBLE);
                ((SentMessageViewHolder) holder).tvTextMessage.setVisibility(View.GONE);
            }
        } else {
            ((ReceivedMessageViewHolder) holder).tvTextMessage.setText(chatMessages.get(position).getMessage());
            ((ReceivedMessageViewHolder) holder).tvMessageDateTime.setText(chatMessages.get(position).getSendedAt());
            ((ReceivedMessageViewHolder) holder).tvUserEmail.setText(chatMessages.get(position).getSenderEmail());

            if (!chatMessages.get(position).getImageUrl().isEmpty()) {
                Glide.with(ctx)
                        .load(chatMessages.get(position).getImageUrl())
                        .into(((ReceivedMessageViewHolder) holder).ivImageMessage);
                ((ReceivedMessageViewHolder) holder).ivImageMessage.setVisibility(View.VISIBLE);
                ((ReceivedMessageViewHolder) holder).tvTextMessage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).getSenderEmail().equals(senderEmail)
                ? VIEW_TYPE_SENT
                : VIEW_TYPE_RECEIVED;
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvTextMessage;
        TextView tvMessageDateTime;
        TextView tvUserEmail;
        ImageView ivImageMessage;

        public SentMessageViewHolder (View itemView) {
            super(itemView);

            tvTextMessage = itemView.findViewById(R.id.textMessage);
            tvMessageDateTime = itemView.findViewById(R.id.messageDateTime);
            tvUserEmail = itemView.findViewById(R.id.userEmail);
            ivImageMessage = itemView.findViewById(R.id.iv_sender_image);
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvTextMessage;
        TextView tvMessageDateTime;
        TextView tvUserEmail;
        ImageView ivImageMessage;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);

            tvTextMessage = itemView.findViewById(R.id.textMessage);
            tvMessageDateTime = itemView.findViewById((R.id.messageDateTime));
            tvUserEmail = itemView.findViewById(R.id.userEmail);
            ivImageMessage = itemView.findViewById(R.id.iv_receiver_image);
        }
    }

}
