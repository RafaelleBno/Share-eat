package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.ValidationDeCommande;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailCommandeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_commande, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupération des arguments
        Bundle args = getArguments();
        if (args == null) return;

        String nom = args.getString("nom", "");
        String prix = args.getString("prix", "");
        String imageUrl = args.getString("imageUrl", "");
        String retrait = args.getString("retrait", "");
        String horaire = args.getString("horaire", "");
        String portion = args.getString("portion", "");
        String userId = args.getString("userId", "");

        // Vues
        TextView nomPlat = view.findViewById(R.id.nomPlatMini);
        TextView prixPlat = view.findViewById(R.id.prixPlatMini);
        ImageView imagePlat = view.findViewById(R.id.imagePlatMini);
        TextView portionsText = view.findViewById(R.id.nombre_portions);
        TextView nomUser = view.findViewById(R.id.nomUser);
        TextView appartUser = view.findViewById(R.id.appartementUser);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        TextView horaireText = view.findViewById(R.id.horaires);
        TextView retraitText = view.findViewById(R.id.retraitText);
        Button checkoutButton = view.findViewById(R.id.checkoutButton);

        // Affichage
        nomPlat.setText(nom);
        prixPlat.setText(prix + " €");
        portionsText.setText(portion != null && !portion.isEmpty() ? portion : "x1");
        horaireText.setText(horaire);
        retraitText.setText(retrait);
        Glide.with(requireContext()).load(imageUrl).into(imagePlat);
        ratingBar.setRating(0);

        // Firestore : user
        if (userId != null && !userId.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String prenom = snapshot.getString("firstName");
                            String appart = snapshot.getString("apartment");
                            nomUser.setText(prenom != null ? prenom : "Utilisateur");
                            appartUser.setText(appart != null ? " | " + appart : "");
                        }
                    })
                    .addOnFailureListener(e -> {
                        nomUser.setText("Utilisateur");
                        appartUser.setText("");
                        Toast.makeText(getContext(), "Erreur Firestore", Toast.LENGTH_SHORT).show();
                    });
        }

        // ➡️ Lancer l’activité de confirmation avec données
        checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ValidationDeCommande.class);
            intent.putExtra("nom", nom);
            intent.putExtra("prix", prix);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("retrait", retrait);
            intent.putExtra("horaire", horaire);
            intent.putExtra("portion", portion);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}

