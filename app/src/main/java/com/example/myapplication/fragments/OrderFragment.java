package com.example.myapplication.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.myapplication.R;
import com.example.myapplication.adapters.PlatAdapter;
import com.example.myapplication.models.Plat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class OrderFragment extends Fragment {

    private RecyclerView rvOrders;
    private PlatAdapter adapter;
    private final List<Plat> orderList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false); // à créer
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PlatAdapter(requireContext(), orderList, plat ->
                Toast.makeText(getContext(), "Commande : " + plat.nom, Toast.LENGTH_SHORT).show());

        rvOrders.setAdapter(adapter);

        loadCommandes();
    }

    private void loadCommandes() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("commandes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    orderList.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Plat plat = doc.toObject(Plat.class);
                        if (plat != null) {
                            orderList.add(plat);
                        }
                    }
                    adapter.updateData(orderList);
                })
                .addOnFailureListener(e -> {
                    Log.e("OrderFragment", "Erreur chargement commandes", e);
                    Toast.makeText(getContext(), "Erreur chargement commandes", Toast.LENGTH_SHORT).show();
                });
    }
}
