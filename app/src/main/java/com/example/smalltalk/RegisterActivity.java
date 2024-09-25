package com.example.smalltalk;

import static com.example.smalltalk.utils.GeneralUtils.showAlert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smalltalk.utils.GeneralUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public ImageButton mBtnBackwards;
    public Button mBtnRegister;
    public TextInputLayout mEmailEditText;
    public TextInputLayout mPasswordEditText;
    public TextInputLayout mConfirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mBtnBackwards = findViewById(R.id.btn_backwards);
        mBtnRegister = findViewById(R.id.register_btn);
        mEmailEditText = findViewById(R.id.et_email);
        mPasswordEditText = findViewById(R.id.et_password);
        mConfirmPasswordEditText = findViewById(R.id.et_confirm_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mBtnBackwards.setOnClickListener(view -> {
            returnLoginActivity();
        });

        mBtnRegister.setOnClickListener(view -> {
            if(fieldsAreInvalid()){
                return;
            }
            /* TODO: Check if user is already created,
            *  check if its truly an email */
            register();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // if it's already logged in, go to main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void register(){
        String email = mEmailEditText.getEditText().getText().toString();
        String password = mPasswordEditText.getEditText().getText().toString();

        mAuth.createUserWithEmailAndPassword(email.toLowerCase(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Go to main activity
                            // TODO:
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthWeakPasswordException) {
                                showAlert("Error", "La contraseña debe tener al menos 6 caracteres", RegisterActivity.this);
                            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                showAlert("Error", "Correo inválido", RegisterActivity.this);
                            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                showAlert("Error", "El correo ya está registrado", RegisterActivity.this);
                            } else {
                                showAlert("Error", "Error al registrar", RegisterActivity.this);
                            }

                            mEmailEditText.getEditText().setText("");
                            mPasswordEditText.getEditText().setText("");
                            mConfirmPasswordEditText.getEditText().setText("");
                        }
                    }
                });
    }

    private boolean fieldsAreInvalid() {
        boolean hasErrors = false;

        findViewById(R.id.tv_error).setVisibility(View.GONE);
        findViewById(R.id.tv_password).setVisibility(View.GONE);

        if(fieldsAreEmpty()) {
            findViewById(R.id.tv_error).setVisibility(View.VISIBLE);
            hasErrors = true;
        } else if (!passwordAndConfirmMatch()) {
            findViewById(R.id.tv_password).setVisibility(View.VISIBLE);
            hasErrors = true;
        }

        return hasErrors;
    }

    private boolean fieldsAreEmpty(){
        return mEmailEditText.getEditText().getText().toString().isBlank() ||
                mPasswordEditText.getEditText().getText().toString().isBlank() ||
                mConfirmPasswordEditText.getEditText().getText().toString().isBlank();
    }

    private boolean passwordAndConfirmMatch(){
        String password = mPasswordEditText.getEditText().getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getEditText().getText().toString();

        return password.equals(confirmPassword);
    }

    private void returnLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}