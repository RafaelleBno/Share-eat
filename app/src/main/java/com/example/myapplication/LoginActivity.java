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

public class LoginActivity extends AppCompatActivity {

    private EditText loginPhoneInput, loginPasswordInput;
    private ImageView loginEyeIcon;
    private Button loginBtn;
    private TextView goToSignup;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPhoneInput = findViewById(R.id.loginPhoneInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        loginEyeIcon = findViewById(R.id.loginEyeIcon);
        loginBtn = findViewById(R.id.loginBtn);
        goToSignup = findViewById(R.id.goToSignup);

        // Affichage / masquage du mot de passe
        loginEyeIcon.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                loginPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                loginPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            loginPasswordInput.setSelection(loginPasswordInput.getText().length());
        });

        // Bouton Login
        loginBtn.setOnClickListener(v -> {
            String phone = loginPhoneInput.getText().toString().trim();
            String password = loginPasswordInput.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔐 Simulation de login
            Toast.makeText(this, "Login success (mock)", Toast.LENGTH_SHORT).show();
            // TODO: ici tu peux ajouter Firebase Auth ou rediriger vers HomeActivity
        });

        // Lien vers l'inscription
        goToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class); // vers inscription
            startActivity(intent);
        });
    }
}
