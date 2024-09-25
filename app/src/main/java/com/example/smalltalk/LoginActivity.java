package com.example.smalltalk;

import static com.example.smalltalk.utils.GeneralUtils.showAlert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smalltalk.utils.GeneralUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button mLoginButton;
    private Button mRegisterButton;
    private TextInputLayout mEmailEditText;
    private TextInputLayout mPasswordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mLoginButton = findViewById(R.id.btn_signin);
        mRegisterButton = findViewById(R.id.register_btn);
        mEmailEditText = findViewById(R.id.et_email);
        mPasswordEditText = findViewById(R.id.et_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fieldsAreEmpty()){
                    findViewById(R.id.tv_error).setVisibility(View.VISIBLE);
                    return;
                }

                findViewById(R.id.tv_error).setVisibility(View.GONE);
                login(view);
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity(RegisterActivity.class);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            changeActivity(MainActivity.class);
        }
    }

    public void login(View view) {
        String email = mEmailEditText.getEditText().getText().toString();
        String password = mPasswordEditText.getEditText().getText().toString();

        mAuth.signInWithEmailAndPassword(email.toLowerCase(), password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        changeActivity(MainActivity.class);
                    } else {
                        showAlert("Error", "Correo o contraseña incorrectos", this);
                    }
                });
    }

    private <T> void changeActivity(Class<T> activity){
        Intent intent = new Intent(LoginActivity.this, activity);
        startActivity(intent);
        finish();
    }

    public boolean fieldsAreEmpty() {
        return mEmailEditText.getEditText().getText().toString().isBlank()
                || mPasswordEditText.getEditText().getText().toString().isBlank();
    }

}