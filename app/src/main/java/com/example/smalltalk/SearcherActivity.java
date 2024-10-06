package com.example.smalltalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.ImageButton;

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
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_searcher);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rv_users);
        etSearch = findViewById(R.id.et_search);
        btnBack = findViewById(R.id.btn_backwards);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerView("");

        btnBack.setOnClickListener(v -> {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        etSearch.setEndIconOnClickListener(v -> {
            String searchTerm = etSearch.getEditText().getText().toString();
            setupRecyclerView(searchTerm);
        });

        etSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                etSearch.getEditText().setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                        String searchTerm = s.toString();
                        setupRecyclerView(searchTerm);
                        return true;
                    }
                    return false;
                });
            }
        });
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
}