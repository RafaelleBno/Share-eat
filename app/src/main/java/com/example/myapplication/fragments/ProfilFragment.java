package com.example.myapplication.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapters.PlatAdapter;
import com.example.myapplication.models.Plat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class ProfilFragment extends Fragment {

    private TextView tvPrenomNom, tvFirstName, tvName, tvMail, tvTel, tvApt;
    private ImageView ivPhotoProfil;
    private LinearLayout btnWallet, btnOrder, btnFavoris;
    private RecyclerView rvPosts;
    private PlatAdapter adapter;
    private List<Plat> platList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPrenomNom  = view.findViewById(R.id.prenomprofil);
        ivPhotoProfil = view.findViewById(R.id.photo_profil);
        tvFirstName  = view.findViewById(R.id.firstname);
        tvName       = view.findViewById(R.id.name);
        tvMail       = view.findViewById(R.id.mail);
        tvTel        = view.findViewById(R.id.tel);
        tvApt        = view.findViewById(R.id.apt);

        btnWallet   = view.findViewById(R.id.wallet);
        btnOrder    = view.findViewById(R.id.order);
        btnFavoris  = view.findViewById(R.id.heart);

        rvPosts = view.findViewById(R.id.postsRecycler);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlatAdapter(requireContext(), platList, plat -> {});
        rvPosts.setAdapter(adapter);

        btnWallet.setOnClickListener(v ->
                Toast.makeText(getContext(), "Wallet bientôt dispo", Toast.LENGTH_SHORT).show());

        btnOrder.setOnClickListener(v ->
                Toast.makeText(getContext(), "Commandes à venir", Toast.LENGTH_SHORT).show());

        btnFavoris.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FavorisFragment())
                    .addToBackStack(null)
                    .commit();
        });

        loadUserInfo();
        loadUserPlats();
    }

    private void loadUserInfo() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String firstName = snapshot.getString("firstName");
                        String lastName = snapshot.getString("lastName");
                        String email = snapshot.getString("email");
                        String phone = snapshot.getString("phone");
                        String apt = snapshot.getString("apartment");

                        tvPrenomNom.setText(firstName + " " + lastName);
                        tvFirstName.setText("First name : " + firstName);
                        tvName.setText("Name : " + lastName);
                        tvMail.setText("Mail : " + email);
                        tvTel.setText("Tel : " + phone);
                        tvApt.setText("Apartement : " + apt);
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfilFragment", "Erreur Firestore: ", e));
    }

    private void loadUserPlats() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("plats")
                .whereEqualTo("userId", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) return;
                    platList.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Plat plat = doc.toObject(Plat.class);
                        if (plat != null) {
                            plat.documentId = doc.getId();
                            platList.add(plat);
                        }
                    }
                    adapter.updateData(platList);
                });
    }

    private void loadUserFavoris() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("plats")
                .whereArrayContains("likedBy", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) return;
                    platList.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Plat plat = doc.toObject(Plat.class);
                        if (plat != null) {
                            plat.documentId = doc.getId();
                            platList.add(plat);
                        }
                    }
                    adapter.updateData(platList);
                });
    }
}
