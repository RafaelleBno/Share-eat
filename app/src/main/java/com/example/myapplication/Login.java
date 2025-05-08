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

public class Login extends AppCompatActivity {

    private EditText loginEmailInput, loginPasswordInput;
    private ImageView loginEyeIcon;
    private Button loginBtn;
    private TextView goToSignup;
    private boolean passwordVisible = false;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();
        loginEmailInput   = findViewById(R.id.loginEmailInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        loginEyeIcon      = findViewById(R.id.loginEyeIcon);
        loginBtn          = findViewById(R.id.loginBtn);
        goToSignup        = findViewById(R.id.goToSignup);


        loginEyeIcon.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                loginPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                loginPasswordInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            loginPasswordInput.setSelection(loginPasswordInput.getText().length());
        });

        loginBtn.setOnClickListener(v -> {
            String email    = loginEmailInput.getText().toString().trim();
            String password = loginPasswordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // ▶ redirection vers MainActivity + tab Home
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("nav_item", R.id.menu_home);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });


        goToSignup.setOnClickListener(v ->
                startActivity(new Intent(Login.this, Subscribe.class)));
    }
}