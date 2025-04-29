package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Bienvenu extends AppCompatActivity {

    private ImageView splashLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenu);

        splashLogo = findViewById(R.id.splashLogo);

        // Animation fade-out (ou tu peux créer ta propre anim dans res/anim/)
        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fadeOut.setDuration(800); // durée de l'animation

        new Handler().postDelayed(() -> {
            splashLogo.startAnimation(fadeOut);

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Bienvenu.this, Subscribe.class);
                startActivity(intent);
                finish();
            }, 800); // attendre la fin de l'anim

        }, 1700); // temps d'affichage avant anim
    }
}

