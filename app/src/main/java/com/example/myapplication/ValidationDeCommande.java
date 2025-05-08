package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.models.Plat;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class ValidationDeCommande extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_de_commande);

        // Récupérer les vues
        ImageView imagePlat = findViewById(R.id.logo_commande);
        TextView titleText = findViewById(R.id.enjoy_your_order);
        Button helpButton = findViewById(R.id.help_button);
        Button closeButton = findViewById(R.id.close_button);

        Intent intent = getIntent();
        String nom = intent.getStringExtra("nom");
        String prix = intent.getStringExtra("prix");
        String imageUrl = intent.getStringExtra("imageUrl");
        String retrait = intent.getStringExtra("retrait");
        String horaire = intent.getStringExtra("horaire");
        String portion = intent.getStringExtra("portion");
        String userId = intent.getStringExtra("userId");

        // Affichage de l'image du plat
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imagePlat);
        } else {
            imagePlat.setImageResource(R.drawable.ic_launcher_background);
        }

        // Enregistrer la commande dans Firestore
        if (userId != null && nom != null && prix != null && imageUrl != null) {
            Plat platCommande = new Plat();
            platCommande.nom = nom;
            platCommande.prix = prix;
            platCommande.imageUrl = imageUrl;
            platCommande.retrait = retrait;
            platCommande.horaire = horaire;
            platCommande.portion = portion;
            platCommande.timestamp = Timestamp.now();

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("commandes")
                    .add(platCommande)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Commande enregistrée ✅", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur enregistrement commande ❌", Toast.LENGTH_SHORT).show();
                    });
        }

        // Afficher prénom et appart utilisateur
        if (userId != null && !userId.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String prenom = snapshot.getString("firstName");
                            String appart = snapshot.getString("apartment");
                            // Tu peux afficher ça si tu as des TextView prévus pour
                        }
                    });
        }

        helpButton.setOnClickListener(v ->
                Toast.makeText(this, "Support bientôt dispo 🛠️", Toast.LENGTH_SHORT).show());

        closeButton.setOnClickListener(v -> {
            Intent intentHome = new Intent(this, MainActivity.class);
            intentHome.putExtra("nav_item", R.id.menu_home);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentHome);
            finish();
        });
    }
}
