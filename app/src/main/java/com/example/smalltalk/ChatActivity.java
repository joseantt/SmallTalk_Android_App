package com.example.smalltalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smalltalk.models.OpenChatsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatActivity extends AppCompatActivity {

    private OpenChatsModel openChat;
    private FirebaseUser currentUser;

    private TextView receiverUserEmail;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        receiverUserEmail = findViewById(R.id.receiverUserEmail);
        backArrow = findViewById(R.id.backArrow);

        loadOpenChat();
        setListeners();
    }

    private void loadOpenChat () {
        openChat = (OpenChatsModel) getIntent().getSerializableExtra("openChat");
        receiverUserEmail.setText(
                currentUser.getEmail().equalsIgnoreCase(openChat.getUserEmailOne())
                        ? openChat.getUserEmailTwo()
                        : openChat.getUserEmailOne()
        );
    }

    private void setListeners () {
        backArrow.setOnClickListener(v -> finish());
    }
}