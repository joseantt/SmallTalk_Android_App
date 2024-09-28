package com.example.smalltalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.adapter.OpenChatsAdapter;
import com.example.smalltalk.listeners.OpenChatListener;
import com.example.smalltalk.models.OpenChatsModel;
import com.example.smalltalk.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser == null){
            changeActivity();
            return;
        }

        openChatsRecyclerView = findViewById(R.id.openChatsRecyclerView);
        db = FirebaseFirestore.getInstance();

        getOpenChats();
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
}