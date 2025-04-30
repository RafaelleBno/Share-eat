package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.fragments.AjouterPlatFragment;
import com.example.myapplication.fragments.MenuFragment;
import com.example.myapplication.fragments.MessageFragment;
import com.example.myapplication.fragments.ProfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        /* ---- fragment de départ : vient du Login/Subscribe ou, à défaut, Home ---- */
        int startItem = getIntent().getIntExtra("nav_item", R.id.menu_home);

        if (savedInstanceState == null) {
            selectFragment(startItem);               // charge le fragment voulu
            bottomNavigationView.setSelectedItemId(startItem);
        }

        /* ---- listener BottomNavigationView ---- */
        bottomNavigationView.setOnItemSelectedListener(item -> {
            selectFragment(item.getItemId());
            return true;
        });
    }

    /** Affiche le fragment correspondant à l’id de menu donné */
    private void selectFragment(int id) {
        Fragment fragment = null;

        if (id == R.id.menu_home)         fragment = new MenuFragment();
        else if (id == R.id.menu_add)     fragment = new AjouterPlatFragment();
        else if (id == R.id.menu_message) fragment = new MessageFragment();
        else if (id == R.id.menu_profile) fragment = new ProfilFragment();

        if (fragment != null) {
            loadFragment(fragment);
        }
    }

    /** Remplace le fragment courant par celui passé en paramètre */
    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
