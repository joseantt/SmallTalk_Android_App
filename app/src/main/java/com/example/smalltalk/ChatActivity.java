package com.example.smalltalk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.adapter.ChatAdapter;
import com.example.smalltalk.models.ChatMessage;
import com.example.smalltalk.models.OpenChatsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private OpenChatsModel openChat;
    private FirebaseUser currentUser;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;

    private TextView tvReceiverUserEmail;
    private ImageView ivBackArrow;
    private RecyclerView messagesRecyclerView;
    private TextView tvChatInput;
    private ImageView ivSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        tvReceiverUserEmail = findViewById(R.id.receiverUserEmail);
        ivBackArrow = findViewById(R.id.backArrow);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        tvChatInput = findViewById(R.id.chatInput);
        ivSendMessage = findViewById(R.id.sendMessage);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages, currentUser.getEmail());
        messagesRecyclerView.setAdapter(chatAdapter);
        db = FirebaseFirestore.getInstance();

        loadOpenChat();
        setListeners();
        listenMessages();
    }

    private void sendMessage() {
        if (tvChatInput.getText() == null || tvChatInput.getText().toString().trim().isEmpty())
            return;

        Map<String, Object> msg = new HashMap<>();
        msg.put("chat_id", openChat.getId());
        msg.put("sender_email", currentUser.getEmail());
        msg.put("message", tvChatInput.getText().toString());
        msg.put("sended_at", new Date());
        db.collection("chat_message").add(msg);
        tvChatInput.setText(null);
    }

    private void loadOpenChat() {
        openChat = (OpenChatsModel) getIntent().getSerializableExtra("openChat");
        tvReceiverUserEmail.setText(
                currentUser.getEmail().equals(openChat.getUserEmailOne())
                        ? openChat.getUserEmailTwo()
                        : openChat.getUserEmailOne()
        );
    }

    private void setListeners() {
        ivBackArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            intent.putExtra("openChat", openChat);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
        ivSendMessage.setOnClickListener(v -> sendMessage());
        tvChatInput.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.getDefault()).format(date);
    }

    private void listenMessages() {
        db.collection("chat_message")
                .whereEqualTo("chat_id", openChat.getId())
                .addSnapshotListener(eventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null || value == null)
            return;

        int count = chatMessages.size();
        for (DocumentChange documentChange : value.getDocumentChanges()) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                ChatMessage chatMessage = new ChatMessage(
                        documentChange.getDocument().getId(),
                        documentChange.getDocument().getString("chat_id"),
                        documentChange.getDocument().getString("sender_email"),
                        documentChange.getDocument().getString("message"),
                        getReadableDateTime(documentChange.getDocument().getDate("sended_at")),
                        documentChange.getDocument().getDate("sended_at")
                );
                chatMessages.add(chatMessage);
            }
        }
        chatMessages.sort(Comparator.comparing(ChatMessage::getDateObject));
        if (count == 0)
            chatAdapter.notifyDataSetChanged();
        else {
            chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
            messagesRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }
        messagesRecyclerView.setVisibility(View.VISIBLE);
    };
}