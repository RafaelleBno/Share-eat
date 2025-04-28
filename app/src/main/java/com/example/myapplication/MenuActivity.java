package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                // On reste sur MenuActivity
                return true;
            } else if (id == R.id.menu_add) {
                startActivity(new Intent(this, AddDishActivity.class));
                return true;
            } else if (id == R.id.menu_message) {
                // Clique sur Message mais pour l’instant on ne fait rien
                return true;
            } else if (id == R.id.menu_profile) {
                // Clique sur Profile mais pour l’instant on ne fait rien
                return true;
            }
            return false;
        });
    }
}
