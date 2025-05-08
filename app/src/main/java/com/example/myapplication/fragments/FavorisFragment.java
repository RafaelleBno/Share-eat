// FavorisFragment.java
package com.example.myapplication.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FavorisFragment extends Fragment {

    private RecyclerView rvFavoris;
    private PlatAdapter adapter;
    private final List<Plat> favorisList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favoris, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavoris = view.findViewById(R.id.rvFavoris);
        rvFavoris.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PlatAdapter(requireContext(), favorisList, plat -> {
            Toast.makeText(getContext(), "Plat : " + plat.nom, Toast.LENGTH_SHORT).show();
        });
        rvFavoris.setAdapter(adapter);

        loadFavoris();
    }

    private void loadFavoris() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("favoris")
                .get()
                .addOnSuccessListener(favorisSnapshot -> {
                    List<String> platIds = new ArrayList<>();
                    for (DocumentSnapshot doc : favorisSnapshot.getDocuments()) {
                        platIds.add(doc.getId()); // l’ID du plat liké
                    }

                    if (platIds.isEmpty()) {
                        favorisList.clear();
                        adapter.updateData(favorisList);
                        return;
                    }

                    FirebaseFirestore.getInstance()
                            .collection("plats")
                            .whereIn(FieldPath.documentId(), platIds)
                            .get()
                            .addOnSuccessListener(platsSnapshot -> {
                                favorisList.clear();
                                for (DocumentSnapshot doc : platsSnapshot.getDocuments()) {
                                    Plat plat = doc.toObject(Plat.class);
                                    if (plat != null) {
                                        plat.documentId = doc.getId();
                                        plat.isLiked = true;
                                        favorisList.add(plat);
                                    }
                                }
                                adapter.updateData(favorisList);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FavorisFragment", "Erreur Firestore", e);
                    Toast.makeText(getContext(), "Erreur chargement favoris", Toast.LENGTH_SHORT).show();
                });
    }

}

