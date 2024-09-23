package com.example.smalltalk;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.adapter.OpenChatsAdapter;
import com.example.smalltalk.models.OpenChatsModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView openChatsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        this.openChatsRecyclerView = findViewById(R.id.openChatsRecyclerView);

        getOpenChats();
    }

    private void getOpenChats() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("openChats")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() && task.getResult() == null) {
                        Toast.makeText(this, "Error buscando los datos.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<OpenChatsModel> openChats = new ArrayList<>();
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        OpenChatsModel openChat = new OpenChatsModel(
                                qds.getString("email"),
                                qds.getString("lastMessage")
                        );
                        openChats.add(openChat);
                    }

                    if (!openChats.isEmpty()) {
                        OpenChatsAdapter openChatsAdapter = new OpenChatsAdapter(this, openChats);
                        this.openChatsRecyclerView.setAdapter(openChatsAdapter);
                        this.openChatsRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

}