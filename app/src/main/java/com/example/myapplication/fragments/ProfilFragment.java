package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapters.PlatAdapter;
import com.example.myapplication.models.Plat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ProfilFragment extends Fragment {

    /* ---------- vues ---------- */
    private TextView tvPrenomNom, tvFirstName, tvName, tvMail, tvTel, tvApt;
    private ImageView ivPhotoProfil;
    private LinearLayout btnWallet, btnOrder, btnFavoris;
    private RecyclerView rvPosts;

    /* ---------- firebase ---------- */
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    /* ---------- listes / adapters ---------- */
    private final List<Plat> userPlats = new ArrayList<>();
    private PlatAdapter platAdapter;
    private ListenerRegistration platsListener;

    /* ---------- cycle de vie ---------- */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);   // ← nom du XML mis à jour
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ----- init vues ----- */
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

        /* ----- firebase ----- */
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        /* ----- recyclerView “POSTS” ----- */
        platAdapter = new PlatAdapter(requireContext(), userPlats, plat ->
                Toast.makeText(getContext(), "Clique sur : " + plat.nom, Toast.LENGTH_SHORT).show());
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvPosts.setAdapter(platAdapter);

        /* ----- listeners boutons ----- */
        btnWallet.setOnClickListener(v ->
                Toast.makeText(getContext(), "Wallet bientôt dispo 💳", Toast.LENGTH_SHORT).show());

        btnOrder.setOnClickListener(v ->
                Toast.makeText(getContext(), "Historique de commandes à venir 📦", Toast.LENGTH_SHORT).show());

        btnFavoris.setOnClickListener(v ->
                Toast.makeText(getContext(), "Tes favoris arrivent bientôt ❤️", Toast.LENGTH_SHORT).show());

        /* ----- données ----- */
        loadUserInfo();
        listenToUserPlats();
    }

    /* ---------- récupère les infos du document “users / uid” ---------- */
    private void loadUserInfo() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) return;

        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this::fillUserFields)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erreur profil : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fillUserFields(DocumentSnapshot snap) {
        if (!snap.exists()) return;

        String prenom   = snap.getString("firstName");
        String nom      = snap.getString("lastName");
        String email    = snap.getString("email");
        String tel      = snap.getString("phone");       // ⚠️ Pense à enregistrer “phone” dans Subscribe.java
        String apt      = snap.getString("apartment");
        String photoUrl = snap.getString("photoUrl");    // optionnel, si tu stockes l’URL de la photo

        tvPrenomNom.setText(prenom + " " + nom);
        tvFirstName.setText("First name :   " + prenom);
        tvName.setText("Name :   " + nom);
        tvMail.setText("Mail :   " + email);
        tvTel.setText("Tel :   " + (tel != null ? tel : "—"));
        tvApt.setText("Apartement :  " + apt);

        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this).load(photoUrl).into(ivPhotoProfil);
        }
    }

    /* ---------- écoute en temps réel les plats postés par l’utilisateur ---------- */
    private void listenToUserPlats() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) return;

        Query q = firestore.collection("plats")
                .whereEqualTo("userId", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        platsListener = q.addSnapshotListener((snap, err) -> {
            if (err != null || snap == null) return;

            userPlats.clear();
            for (DocumentSnapshot d : snap.getDocuments()) {
                Plat p = d.toObject(Plat.class);
                if (p != null) p.documentId = d.getId();
                userPlats.add(p);
            }
            platAdapter.updateData(new ArrayList<>(userPlats));
        });
    }

    /* ---------- stop l’écoute quand le fragment est détruit ---------- */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (platsListener != null) platsListener.remove();
    }
}
