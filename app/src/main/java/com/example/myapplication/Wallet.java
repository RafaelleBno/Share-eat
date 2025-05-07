package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.fragments.ProfilFragment;

public class Wallet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        ImageView fleche = findViewById(R.id.fleche);

        fleche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment profilFragment = new ProfilFragment(); // ou new Profil(), selon ton nom

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, profilFragment);
                transaction.addToBackStack(null); // pour permettre le retour arrière
                transaction.commit();
            }
        });
    }
}
