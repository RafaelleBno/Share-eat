package com.example.myApplication; 

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet); // Ton fichier XML

        // Ciblage de l'image flèche
        ImageView fleche = findViewById(R.id.fleche);

        // Click listener pour retourner à Profil
        fleche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletActivity.this, Profil.class);
                startActivity(intent);
                finish(); // Facultatif : ferme WalletActivity
            }
        });
    }
}
