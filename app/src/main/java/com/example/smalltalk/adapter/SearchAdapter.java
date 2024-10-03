package com.example.smalltalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.R;
import com.example.smalltalk.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchAdapter extends FirestoreRecyclerAdapter<User, SearchAdapter.UserViewHolder> {

    Context ctx;

    public SearchAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context ctx) {
        super(options);
        this.ctx = ctx;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.userEmail.setText(model.getEmail());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.search_recycler_row, parent, false);
        return new UserViewHolder(view);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.iv_user_icon);
            userEmail = itemView.findViewById(R.id.tv_email_row);
        }
    }
}
