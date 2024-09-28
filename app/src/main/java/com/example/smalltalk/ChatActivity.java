package com.example.smalltalk;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.adapter.ChatAdapter;
import com.example.smalltalk.models.ChatMessage;
import com.example.smalltalk.models.OpenChatsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private OpenChatsModel openChat;
    private FirebaseUser currentUser;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;

    private TextView tvReceiverUserEmail;
    private ImageView ivBackArrow;
    private RecyclerView rvMessagesRecyclerView;
    private TextView tvChatInput;
    private ImageView ivSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        tvReceiverUserEmail = findViewById(R.id.receiverUserEmail);
        ivBackArrow = findViewById(R.id.backArrow);
        rvMessagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        tvChatInput = findViewById(R.id.chatInput);
        ivSendMessage = findViewById(R.id.sendMessage);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages, currentUser.getEmail());
        rvMessagesRecyclerView.setAdapter(chatAdapter);
        db = FirebaseFirestore.getInstance();

        loadOpenChat();
        setListeners();
    }

    private void sendMessage() {
        Map<String, Object> msg = new HashMap<>();
        msg.put("chat_id", openChat.getId());
        msg.put("sender_email", currentUser.getEmail());
        msg.put("message", tvChatInput.getText().toString());
        msg.put("sended_at", new Date());
        db.collection("chat_message").add(msg);
        tvChatInput.setText(null);
    }

    private void loadOpenChat () {
        openChat = (OpenChatsModel) getIntent().getSerializableExtra("openChat");
        tvReceiverUserEmail.setText(
                currentUser.getEmail().equals(openChat.getUserEmailOne())
                        ? openChat.getUserEmailTwo()
                        : openChat.getUserEmailOne()
        );
    }

    private void setListeners () {
        ivBackArrow.setOnClickListener(v -> finish());
        ivSendMessage.setOnClickListener(v -> sendMessage());
    }
}