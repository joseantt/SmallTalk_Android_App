package com.example.smalltalk.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.ChatActivity;
import com.example.smalltalk.R;
import com.example.smalltalk.models.OpenChatsModel;
import com.example.smalltalk.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SearchAdapter extends FirestoreRecyclerAdapter<User, SearchAdapter.UserViewHolder> {

    private final Context ctx;
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;

    public SearchAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context ctx) {
        super(options);
        this.ctx = ctx;
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.userEmail.setText(model.getEmail());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.search_recycler_row, parent, false);

        view.setOnClickListener(this::searchChatInDb);

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

    // Methods
    private void searchChatInDb(View view) {

        CollectionReference chats = db.collection("open_chat");
        TextView email = view.findViewById(R.id.tv_email_row);

        chats.whereEqualTo("user_email_one", email.getText().toString())
             .whereEqualTo("user_email_two", currentUser.getEmail())
                .get()
                .addOnCompleteListener(t -> {
                    if (!t.isSuccessful()) {
                        return;
                    }
                    if (!t.getResult().isEmpty()) {
                        t.getResult().getDocuments().get(0).getReference().get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Log.d("SearchAdapter", documentSnapshot.toString());
                                    OpenChatsModel openChat = new OpenChatsModel(
                                            documentSnapshot.getId(),
                                            "",
                                            email.getText().toString(),
                                            currentUser.getEmail(),
                                            documentSnapshot.getDate("created_at")
                                    );
                                    goToChatActivity(openChat);
                                });
                        return;
                    }
                    chats.whereEqualTo("user_email_two", email.getText().toString())
                            .whereEqualTo("user_email_one", currentUser.getEmail())
                            .get()
                            .addOnCompleteListener(t2 -> {
                                if (!t2.isSuccessful()) {
                                    return;
                                }
                                if (!t2.getResult().isEmpty()) {
                                    // Open chat
                                    t2.getResult().getDocuments().get(0).getReference().get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                Log.d("SearchAdapter", documentSnapshot.toString());
                                                OpenChatsModel openChat = new OpenChatsModel(
                                                        documentSnapshot.getId(),
                                                        "",
                                                        email.getText().toString(),
                                                        currentUser.getEmail(),
                                                        documentSnapshot.getDate("created_at")
                                                );
                                                goToChatActivity(openChat);
                                            });
                                    return;
                                }

                                createNewChat(email.getText().toString());
                            });
                });
    }

    private void createNewChat(String email) {
        Map<String, Object> chat = new HashMap<>();
        chat.put("user_email_one", email);
        chat.put("user_email_two", currentUser.getEmail());


        db.collection("open_chat").add(chat)
                .addOnSuccessListener(documentReference -> {
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        Log.d("SearchAdapter", documentSnapshot.toString());
                        OpenChatsModel openChat = new OpenChatsModel(
                                documentSnapshot.getId(),
                                "",
                                email,
                                currentUser.getEmail(),
                                documentSnapshot.getDate("created_at")
                        );
                        goToChatActivity(openChat);
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ctx, "Error creando el chat", Toast.LENGTH_SHORT).show();
                });
    }

    private void goToChatActivity(OpenChatsModel openChat) {
        Intent intent = new Intent(ctx, ChatActivity.class);
        intent.putExtra("openChat", openChat);
        ctx.startActivity(intent);
    }
}
