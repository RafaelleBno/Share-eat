package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlatEnDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plat_en_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;

        String nom = args.getString("nom", "");
        String prix = args.getString("prix", "");
        String imageUrl = args.getString("imageUrl", "");
        String retrait = args.getString("retrait", "");
        String description = args.getString("description", "");
        String horaire = args.getString("horaire", "");
        String portion = args.getString("portion", "");
        String allergeneString = args.getString("allergenes", "");
        String userId = args.getString("userId", "");

        List<String> allergenes = allergeneString.isEmpty()
                ? new ArrayList<>()
                : Arrays.asList(allergeneString.split(", "));

        TextView nomPlat = view.findViewById(R.id.title_pates_bolognaise);
        TextView prixPlat = view.findViewById(R.id.prix_pates_bolognaises);
        ImageView imagePlat = view.findViewById(R.id.pates_bolognaise_detail);
        TextView desc = view.findViewById(R.id.text_descrpition);
        Button retraitButton = view.findViewById(R.id.pickup_button);
        Button homeButton = view.findViewById(R.id.home_button);
        Button horaireBtn = view.findViewById(R.id.horaires);
        Button glutenBtn = view.findViewById(R.id.gluten_button);
        Button lactoseBtn = view.findViewById(R.id.lactose_button);
        TextView portionText = view.findViewById(R.id.nombre_portions);
        Button payerButton = view.findViewById(R.id.payerButton);


        // On récupère les deux TextView
        TextView nomUser = view.findViewById(R.id.nomUser);
        TextView appartementUser = view.findViewById(R.id.appartementUser);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        nomPlat.setText(nom);
        prixPlat.setText(prix + " €");
        desc.setText(description);
        horaireBtn.setText(horaire);
        portionText.setText("x " + portion);
        Glide.with(requireContext()).load(imageUrl).into(imagePlat);

        retraitButton.setVisibility("Home".equalsIgnoreCase(retrait) ? View.GONE : View.VISIBLE);
        homeButton.setVisibility("Home".equalsIgnoreCase(retrait) ? View.VISIBLE : View.GONE);

        glutenBtn.setVisibility(allergenes.contains("Gluten") ? View.VISIBLE : View.GONE);
        lactoseBtn.setVisibility(
                allergenes.contains("Lait") || allergenes.contains("Lactose") ? View.VISIBLE : View.GONE);

        // spinner en blanc
        Spinner spinner = view.findViewById(R.id.portion_spinner);

        List<String> portions = Arrays.asList("1", "2", "3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item_white,
                portions
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //----


        // On récupère les infos de l'utilisateur depuis Firestore
        if (!userId.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.exists()) {
                            Toast.makeText(getContext(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String prenom = snapshot.getString("firstName");
                        String appart = snapshot.getString("apartment");

                        nomUser.setText(prenom != null ? prenom : "Utilisateur");
                        appartementUser.setText(appart != null ? " | " + appart : "");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erreur Firestore", Toast.LENGTH_SHORT).show();
                        nomUser.setText("Utilisateur");
                        appartementUser.setText("");
                    });
        } else {
            nomUser.setText("Utilisateur");
            appartementUser.setText("");
        }

        ratingBar.setRating(0); // par défaut, ou récupère une note pour la stocker
        payerButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nom", nom);
            bundle.putString("prix", prix);
            bundle.putString("imageUrl", imageUrl);
            bundle.putString("retrait", retrait);
            bundle.putString("horaire", horaire);
            bundle.putString("portion", portion);
            bundle.putString("userId", userId);

            Fragment detailCommandeFragment = new DetailCommandeFragment();
            detailCommandeFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailCommandeFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // flèche retour mène à la page précédente si non au menu.
        ImageView flecheRetour = view.findViewById(R.id.fleche_retour);

        flecheRetour.setOnClickListener(v -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Fragment menuFragment = new MenuFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, menuFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
