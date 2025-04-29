package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Subscribe extends AppCompatActivity {

    private EditText emailInput, phoneInput, passwordInput;
    private Button nextButton;
    private FirebaseAuth auth;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        ImageView eyeIcon = findViewById(R.id.eyeIcon);
        nextButton = findViewById(R.id.nextButton);

        TextView goToLogin = findViewById(R.id.goToLogin);
        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(Subscribe.this, Login.class));
        });

        eyeIcon.setOnClickListener(view -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });

        nextButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            // 🚀 Ouvre maintenant MainActivity qui gère MenuFragment
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}

