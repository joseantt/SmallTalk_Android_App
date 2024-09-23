package com.example.smalltalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.R;
import com.example.smalltalk.models.OpenChatsModel;

import java.util.ArrayList;

public class OpenChatsAdapter extends RecyclerView.Adapter<OpenChatsAdapter.OpenChatsViewHolder> {
    Context ctx;
    ArrayList<OpenChatsModel> openChatsModels;

    public  OpenChatsAdapter(Context ctx, ArrayList<OpenChatsModel> openChatsModels) {
        this.ctx = ctx;
        this.openChatsModels = openChatsModels;
    }

    @NonNull
    @Override
    public OpenChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.open_chats_container, parent, false);

        return new OpenChatsAdapter.OpenChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenChatsViewHolder holder, int position) {
        holder.tvEmail.setText(openChatsModels.get(position).getEmail());
        holder.tvLastMessage.setText(openChatsModels.get(position).getLastMessage());
    }

    @Override
    public int getItemCount() {
        return openChatsModels.size();
    }

    public static class OpenChatsViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail;
        TextView tvLastMessage;

        public OpenChatsViewHolder (View itemView) {
            super(itemView);

            tvEmail = itemView.findViewById(R.id.email);
            tvLastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
