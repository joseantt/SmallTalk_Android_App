package com.example.smalltalk;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smalltalk.adapter.SearchAdapter;
import com.example.smalltalk.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class SearcherActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private TextInputLayout etSearch;
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_searcher);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rv_users);
        etSearch = findViewById(R.id.et_search);
        searchButton = findViewById(R.id.btn_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchButton.setOnClickListener(v -> {
            String searchTerm = etSearch.getEditText().getText().toString();
            setupRecyclerView(searchTerm);
        });

        setupRecyclerView("");
    }

    void setupRecyclerView(String searchTerm) {
        CollectionReference userCollection = db.collection("user");

        Query query = (searchTerm.isBlank())
                        ? userCollection
                        : userCollection.whereGreaterThanOrEqualTo("email", searchTerm)
                                        .whereLessThanOrEqualTo("email", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new SearchAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.startListening();
        }
    }
}