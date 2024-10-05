package com.example.smalltalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.adapter.OpenChatsAdapter;
import com.example.smalltalk.listeners.OpenChatListener;
import com.example.smalltalk.models.OpenChatsModel;
import com.example.smalltalk.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OpenChatListener {

    private RecyclerView openChatsRecyclerView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ArrayList<OpenChatsModel> openChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        if (getIntent().getExtras() != null) {
            String openChatId = getIntent().getExtras().getString("openChatId");

            if (openChatId == null)
                return;

            db.collection("open_chat")
                    .document(openChatId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful())
                            return;

                        DocumentSnapshot document = task.getResult();
                        if (document == null)
                            return;

                        OpenChatsModel openChat = new OpenChatsModel(
                                document.getId(),
                                document.getString("last_message"),
                                document.getString("user_email_one"),
                                document.getString("user_email_two"),
                                document.getDate("created_at")
                        );

                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("openChat", openChat);
                        startActivity(intent);
                    });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            changeActivity();
            return;
        }

        openChatsRecyclerView = findViewById(R.id.openChatsRecyclerView);
        ImageButton logoutBtn = findViewById(R.id.btn_logout);

        logoutBtn.setOnClickListener(v -> logout());
        getOpenChats();
        getToken();
    }

    private void changeActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getOpenChats() {
        db.collection("open_chat")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() && task.getResult() == null) {
                        Toast.makeText(this, "Error buscando los datos.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    openChats = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        String userEmailOne = qds.getString("user_email_one");
                        String userEmailTwo = qds.getString("user_email_two");

                        if (!currentUser.getEmail().equals(userEmailOne)
                                && !currentUser.getEmail().equals(userEmailTwo))
                            continue;

                        OpenChatsModel openChat = new OpenChatsModel(
                                qds.getId(),
                                qds.getString("last_message"),
                                userEmailOne,
                                userEmailTwo,
                                qds.getDate("created_at")
                        );

                        openChats.add(openChat);
                    }

                    if (!openChats.isEmpty()) {
                        OpenChatsAdapter openChatsAdapter = new OpenChatsAdapter(this, openChats, this);
                        this.openChatsRecyclerView.setAdapter(openChatsAdapter);
                        this.openChatsRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onOpenChatClicked(int position) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("openChat", openChats.get(position));
        startActivity(intent);
    }

    private void logout(){
        deleteUserPreferences();
        FirebaseAuth.getInstance().signOut();
        deleteUserToken();
        changeActivity();
    }

    private void deleteUserToken() {
        db.collection("user")
                .whereEqualTo("email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("FirestoreQuery", "Error getting documents: ", task.getException());
                        return;
                    }

                    for (QueryDocumentSnapshot document : task.getResult())
                        document.getReference().update("token", FieldValue.delete());
                });
    }

    private void deleteUserPreferences(){
        getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        db.collection("user")
                .whereEqualTo("email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("FirestoreQuery", "Error getting documents: ", task.getException());
                        return;
                    }

                    for (QueryDocumentSnapshot document : task.getResult())
                        document.getReference().update("token", token);
                });
    }
}