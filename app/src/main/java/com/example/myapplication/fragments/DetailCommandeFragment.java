package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.ValidationDeCommande;

public class DetailCommandeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // image de croix pour fermer la page et mène a la page du plat en détail
        View view = inflater.inflate(R.layout.fragment_detail_commande, container, false);

        ImageView closeButton = view.findViewById(R.id.logo_croix);
        closeButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new PlatEnDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    // Bouton checkout emmène à l'activity Validation de commande --- Voir pour ajouter une page de payement ?
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button checkoutButton = view.findViewById(R.id.go_checkout_button);

        checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ValidationDeCommande.class);
            startActivity(intent);
        });
    }
}