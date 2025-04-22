package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText phoneInput, passwordInput;
    private ImageView eyeIcon;
    private Button nextButton;
    private boolean passwordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        eyeIcon = findViewById(R.id.eyeIcon);
        nextButton = findViewById(R.id.nextButton);

        // Lien vers LoginActivity
        TextView goToLogin = findViewById(R.id.goToLogin);
        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });


        // Affiche/masque le mot de passe
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
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
            // Ici tu peux faire startActivity(new Intent(...)) vers l'app réelle
        });
    }
}