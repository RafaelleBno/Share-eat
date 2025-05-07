package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class WalletActivity extends AppCompatActivity {

    private TextView walletTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet); // Ton fichier XML existant

        walletTextView = findViewById(R.id.prixwallet); // Le TextView qui affiche le montant

        // Gérer le bouton retour (fleche_retour)
        ImageView flecheRetour = findViewById(R.id.fleche);
        flecheRetour.setOnClickListener(v -> finish());

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double wallet = documentSnapshot.getDouble("wallet");
                        if (wallet != null) {
                            walletTextView.setText(String.format("%.2f€", wallet));
                        } else {
                            walletTextView.setText("0.00€");
                        }
                    } else {
                        Toast.makeText(this, "Utilisateur introuvable", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur de récupération du solde", Toast.LENGTH_SHORT).show();
                });
    }
}
