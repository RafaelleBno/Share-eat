package com.example.myapplication.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.PlatAdapter;
import com.example.myapplication.models.Plat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlatAdapter adapter;
    private final List<Plat> platList = new ArrayList<>();

    private EditText searchBar;
    private Button pickupButton, homeButton, refreshButton;
    private TextView veganButton, vegetarianButton, halalButton, kasherButton;

    private String selectedRetrait = "";
    private String selectedRegime = "";
    private ListenerRegistration platListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.dishRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PlatAdapter(requireContext(), platList, this::openPlatDetail);
        recyclerView.setAdapter(adapter);

        searchBar        = view.findViewById(R.id.searchBar);
        pickupButton     = view.findViewById(R.id.pickupButton);
        homeButton       = view.findViewById(R.id.homeButton);
        veganButton      = view.findViewById(R.id.veganButton);
        vegetarianButton = view.findViewById(R.id.vegetarianButton);
        halalButton      = view.findViewById(R.id.halalButton);
        kasherButton     = view.findViewById(R.id.karcherButton);


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBySearch(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        pickupButton.setOnClickListener(v -> toggleRetraitFilter("Pick-up"));
        homeButton.setOnClickListener(v -> toggleRetraitFilter("Home"));

        veganButton.setOnClickListener(v -> toggleRegimeFilter("Vegan"));
        vegetarianButton.setOnClickListener(v -> toggleRegimeFilter("Végétarien"));
        halalButton.setOnClickListener(v -> toggleRegimeFilter("Halal"));
        kasherButton.setOnClickListener(v -> toggleRegimeFilter("Kasher"));

        ImageView refreshButton = view.findViewById(R.id.refreshImage);

        refreshButton.setOnClickListener(v -> {
            refreshButton.setEnabled(false);

            // Changer l’image si besoin
            refreshButton.setImageResource(R.drawable.ic_loading);

            // Lancer l’animation de rotation
            Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
            refreshButton.startAnimation(rotation);

            startListeningToPlats();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                refreshButton.setEnabled(true);

                // Stop l’animation
                refreshButton.clearAnimation();

                // Remet l’image normale
                refreshButton.setImageResource(R.drawable.ic_loading);
            }, 1500);
        });




        startListeningToPlats();
    }

    private void toggleRetraitFilter(String value) {
        selectedRetrait = selectedRetrait.equals(value) ? "" : value;
        startListeningToPlats();
        pickupButton.setSelected(selectedRetrait.equals("Pick-up"));
        homeButton.setSelected(selectedRetrait.equals("Home"));
    }

    private void toggleRegimeFilter(String value) {
        selectedRegime = selectedRegime.equals(value) ? "" : value;
        startListeningToPlats();

        veganButton.setSelected(selectedRegime.equals("Vegan"));
        vegetarianButton.setSelected(selectedRegime.equals("Végétarien"));
        halalButton.setSelected(selectedRegime.equals("Halal"));
        kasherButton.setSelected(selectedRegime.equals("Kasher"));
    }

    private void startListeningToPlats() {
        if (platListener != null) platListener.remove();

        Query query = FirebaseFirestore.getInstance().collection("plats");

        if (!selectedRetrait.isEmpty()) {
            query = query.whereEqualTo("retrait", selectedRetrait);
        }
        if (!selectedRegime.isEmpty()) {
            query = query.whereEqualTo("regime", selectedRegime);
        }

        query = query.orderBy("timestamp", Query.Direction.DESCENDING);

        platListener = query.addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null) {
                Toast.makeText(getContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance()
                    .collection("users").document(userId)
                    .collection("favoris")
                    .get()
                    .addOnSuccessListener(favorisSnapshot -> {
                        Set<String> favorisIds = new HashSet<>();
                        for (DocumentSnapshot favDoc : favorisSnapshot) {
                            favorisIds.add(favDoc.getId());
                        }

                        platList.clear();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Plat plat = doc.toObject(Plat.class);
                            if (plat != null) {
                                plat.documentId = doc.getId();
                                plat.isLiked = favorisIds.contains(plat.documentId); // ❤️
                                platList.add(plat);
                            }
                        }

                        filterBySearch(searchBar.getText().toString());
                    });
        });
    }


    private void filterBySearch(String queryText) {
        List<Plat> filtered = new ArrayList<>();
        for (Plat plat : platList) {
            if (plat.nom != null && plat.nom.toLowerCase().contains(queryText.toLowerCase())) {
                filtered.add(plat);
            }
        }
        adapter.updateData(filtered);
    }

    private void openPlatDetail(Plat plat) {
        Bundle args = new Bundle();
        args.putString("nom", plat.nom);
        args.putString("prix", plat.prix);
        args.putString("imageUrl", plat.imageUrl);
        args.putString("regime", plat.regime);
        args.putString("retrait", plat.retrait);
        args.putString("description", plat.description != null ? plat.description : "");
        args.putString("horaire", plat.horaire != null ? plat.horaire : "");
        args.putString("portion", plat.portion != null ? plat.portion : "");
        args.putString("allergenes", plat.allergenes != null ? String.join(", ", plat.allergenes) : "");

        // ✅ Pour afficher nom + appart du cuisinier
        args.putString("userId", plat.userId);

        // ✅ Pour supprimer le plat après achat
        args.putString("documentId", plat.documentId);

        PlatEnDetailFragment fragment = new PlatEnDetailFragment();
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    } // 👈 cette accolade manquait chez toi !

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (platListener != null) platListener.remove();
    }
}

