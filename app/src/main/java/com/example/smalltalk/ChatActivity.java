package com.example.smalltalk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {

    private OpenChatsModel openChat;
    private FirebaseUser currentUser;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private TextView tvReceiverUserEmail;
    private ImageView ivBackArrow;
    private RecyclerView messagesRecyclerView;
    private TextView tvChatInput;
    private ImageView ivSendMessage;
    private ImageView ivSendImage;
    private ProgressBar progressBar;

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };

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
        ivSendImage = findViewById(R.id.sendImage);
        progressBar = findViewById(R.id.progressBar);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages, currentUser.getEmail());
        messagesRecyclerView.setAdapter(chatAdapter);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

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
        msg.put("image_url", "");
        db.collection("chat_message").add(msg);
        updateChatLastMessage(tvChatInput.getText().toString());
        sendNotification(tvChatInput.getText().toString(), "message");
        tvChatInput.setText(null);
    }

    private void updateChatLastMessage(String message) {
        db.collection("open_chat")
                .document(openChat.getId())
                .update("last_message", message);
    }

    private void sendNotification(String message, String type) {
        String receiverEmail = currentUser.getEmail().equals(openChat.getUserEmailOne())
                ? openChat.getUserEmailTwo()
                : openChat.getUserEmailOne();

        db.collection("user")
                .whereEqualTo("email", receiverEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        return;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String receiverToken = document.getString("token");
                        if (receiverToken == null)
                            return;

                        try {
                            String json = type.equals("message")
                                    ? getJsonStringMessage(message, receiverToken)
                                    : getJsonStringImage(message, receiverToken);
                            callApi(json);
                        } catch (Exception e) {
                            Log.e("ChatActivity", "Error sending notification: " + e.getMessage());
                        }
                    }
                });
    }

    private String getJsonStringMessage(String message, String receiverToken) {
        return "{\n" +
                "  \"message\": {\n" +
                "    \"token\": \"" + receiverToken + "\",\n" +
                "    \"notification\": {\n" +
                "      \"title\": \"" + currentUser.getEmail() + "\",\n" +
                "      \"body\": \"" + message + "\"\n" +
                "    },\n" +
                "    \"data\": {\n" +
                "      \"openChatId\": \"" + openChat.getId() + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private String getJsonStringImage(String imageUrl, String receiverToken) {
        return "{\n" +
                "  \"message\": {\n" +
                "    \"token\": \"" + receiverToken + "\",\n" +
                "    \"notification\": {\n" +
                "      \"title\": \"" + currentUser.getEmail() + "\",\n" +
                "    },\n" +
                "    \"android\": {\n" +
                "      \"notification\": {\n" +
                "        \"image\": \"" + imageUrl + "\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"data\": {\n" +
                "      \"openChatId\": \"" + openChat.getId() + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private void callApi(String json) {
        Thread thread = new Thread(() -> {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            String url = "https://fcm.googleapis.com/v1/projects/small-talk-android/messages:send";
            RequestBody body = RequestBody.create(JSON, json);
            okhttp3.Request req;
            try {
                req = new okhttp3.Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + getAccessToken())
                        .addHeader("Content-Type", "application/json; UTF-8")
                        .build();
                client.newCall(req).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public String getAccessToken() throws IOException {
        InputStream serviceAccountStream = ChatActivity.class.getClassLoader().getResourceAsStream("service-account.json");
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(serviceAccountStream)
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
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

        ivSendImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
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

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK && o.getData() != null){
                        Uri imageUri = o.getData().getData();
                        if (imageUri != null) {
                            uploadImage(imageUri);
                        }
                    }
                }
            });

    private void uploadImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);

        StorageReference reference = storageReference.child("images/" + UUID.randomUUID().toString());
        reference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressBar.setVisibility(View.GONE);

                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("chat_id", openChat.getId());
                        msg.put("sender_email", currentUser.getEmail());
                        msg.put("message", "");
                        msg.put("sended_at", new Date());
                        msg.put("image_url", uri.toString());
                        db.collection("chat_message").add(msg);
                        sendNotification(uri.toString(), "image");
                    });

                    // Update last message with an emoji
                    updateChatLastMessage("Image \uD83D\uDCF7");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al subir imagen, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
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
                        documentChange.getDocument().getDate("sended_at"),
                        documentChange.getDocument().getString("image_url")
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