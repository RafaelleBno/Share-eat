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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Subscribe extends AppCompatActivity {

    private EditText emailInput, phoneInput, passwordInput;
    private EditText firstNameInput, lastNameInput, apartmentInput;
    private Button nextButton;
    private FirebaseAuth auth;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        auth            = FirebaseAuth.getInstance();
        emailInput      = findViewById(R.id.emailInput);
        phoneInput      = findViewById(R.id.phoneInput);
        passwordInput   = findViewById(R.id.passwordInput);
        firstNameInput  = findViewById(R.id.firstNameInput);
        lastNameInput   = findViewById(R.id.lastNameInput);
        apartmentInput  = findViewById(R.id.apartmentInput);
        ImageView eyeIcon = findViewById(R.id.eyeIcon);
        nextButton      = findViewById(R.id.nextButton);

        TextView goToLogin = findViewById(R.id.goToLogin);
        goToLogin.setOnClickListener(v ->
                startActivity(new Intent(Subscribe.this, Login.class)));

        eyeIcon.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                passwordInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });

        nextButton.setOnClickListener(v -> {
            String email     = emailInput.getText().toString().trim();
            String password  = passwordInput.getText().toString().trim();
            String firstName = firstNameInput.getText().toString().trim();
            String lastName  = lastNameInput.getText().toString().trim();
            String apartment = apartmentInput.getText().toString().trim();
            String phone     = phoneInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()
                    || lastName.isEmpty() || apartment.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                            // enregistrement des infos dans Firestore
                            String uid = auth.getCurrentUser().getUid();
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(uid)
                                    .set(new HashMap<String, Object>() {{
                                        put("firstName", firstName);
                                        put("lastName", lastName);
                                        put("apartment", apartment);
                                        put("email", email);
                                        put("phone", phoneInput.getText().toString().trim());
                                    }});

                            // redirection vers MainActivity + onglet Home
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("nav_item", R.id.menu_home);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(this,
                                    "Signup failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
