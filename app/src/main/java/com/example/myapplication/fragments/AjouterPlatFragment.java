package com.example.myapplication.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.util.*;

public class AjouterPlatFragment extends Fragment {

    private ImageView platImageView;
    private EditText nomPlat, prixPlat, descriptionEditText, horaireEditText, nbPortionsEditText;
    private Button addDishButton;
    private MaterialButtonToggleGroup allergenGroup;

    private MaterialButton btnPickup, btnHome, btnKasher, btnHalal, btnVegetarien, btnVegan;

    private Uri imageUri;
    private FirebaseFirestore firestore;
    private StorageReference storageRef;
    private FirebaseAuth auth;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ajouter_plat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        platImageView = view.findViewById(R.id.platImageView);
        nomPlat = view.findViewById(R.id.nomPlat);
        prixPlat = view.findViewById(R.id.prixPlat);
        descriptionEditText = view.findViewById(R.id.descriptionPlat);
        horaireEditText = view.findViewById(R.id.horaireRetrait);
        nbPortionsEditText = view.findViewById(R.id.nbPortionsEditText);
        addDishButton = view.findViewById(R.id.addDishButton);
        allergenGroup = view.findViewById(R.id.allergenGroup);

        btnPickup = view.findViewById(R.id.btnPickup);
        btnHome = view.findViewById(R.id.btnHome);
        btnKasher = view.findViewById(R.id.btnKasher);
        btnHalal = view.findViewById(R.id.btnHalal);
        btnVegetarien = view.findViewById(R.id.btnVegetarien);
        btnVegan = view.findViewById(R.id.btnVegan);

        // Assure-toi que les tags sont bien définis dans le XML aussi
        btnPickup.setTag("Pickup");
        btnHome.setTag("Home");
        btnVegan.setTag("Vegan");
        btnVegetarien.setTag("Vegetarien");
        btnHalal.setTag("Halal");
        btnKasher.setTag("Kasher");

        firestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("plats");
        auth = FirebaseAuth.getInstance();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                res -> {
                    if (res.getResultCode() == requireActivity().RESULT_OK && res.getData() != null) {
                        imageUri = res.getData().getData();
                        Glide.with(this).load(imageUri).into(platImageView);
                    }
                });

        platImageView.setOnClickListener(v -> openGallery());

        addDishButton.setOnClickListener(v -> {
            addDishButton.setEnabled(false);
            if (imageUri != null) uploadImage();
            else {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Choisis une image", Toast.LENGTH_SHORT).show();
                }
                addDishButton.setEnabled(true);
            }
        });
    }

    private void openGallery() {
        Intent pick = new Intent(Intent.ACTION_PICK).setType("image/*");
        imagePickerLauncher.launch(pick);
    }

    private List<String> getAllergenesChecked() {
        List<String> allergenes = new ArrayList<>();
        for (int i = 0; i < allergenGroup.getChildCount(); i++) {
            MaterialButton btn = (MaterialButton) allergenGroup.getChildAt(i);
            if (btn.isChecked()) {
                allergenes.add(btn.getText().toString());
            }
        }
        return allergenes;
    }

    private List<String> getRegimesChecked() {
        List<String> regimes = new ArrayList<>();
        if (btnPickup.isChecked()) regimes.add(btnPickup.getTag().toString());
        if (btnHome.isChecked()) regimes.add(btnHome.getTag().toString());
        if (btnVegan.isChecked()) regimes.add(btnVegan.getTag().toString());
        if (btnVegetarien.isChecked()) regimes.add(btnVegetarien.getTag().toString());
        if (btnHalal.isChecked()) regimes.add(btnHalal.getTag().toString());
        if (btnKasher.isChecked()) regimes.add(btnKasher.getTag().toString());
        return regimes;
    }

    private String getCheckedRetrait() {
        if (btnPickup.isChecked()) return btnPickup.getTag().toString();
        if (btnHome.isChecked()) return btnHome.getTag().toString();
        return "";
    }

    private void uploadImage() {
        StorageReference ref = storageRef.child(UUID.randomUUID() + ".jpg");
        ref.putFile(imageUri)
                .addOnSuccessListener(s -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> fetchUserAndSavePlat(uri.toString()))
                        .addOnFailureListener(e -> {
                            err("Récupération URL", e);
                            addDishButton.setEnabled(true);
                        }))
                .addOnFailureListener(e -> {
                    err("Upload", e);
                    addDishButton.setEnabled(true);
                });
    }

    private void fetchUserAndSavePlat(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Profil utilisateur introuvable", Toast.LENGTH_SHORT).show();
                        }
                        addDishButton.setEnabled(true);
                        return;
                    }

                    String userPrenom = snapshot.getString("firstName");
                    String userAppartement = snapshot.getString("apartment");

                    savePlat(imageUrl, userPrenom, userAppartement);
                })
                .addOnFailureListener(e -> {
                    err("Fetch user", e);
                    addDishButton.setEnabled(true);
                });
    }

    private void savePlat(String url, String userPrenom, String userAppartement) {
        String nom = nomPlat.getText().toString().trim();
        String prix = prixPlat.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String horaire = horaireEditText.getText().toString().trim();
        String portion = nbPortionsEditText.getText().toString().trim();
        List<String> allergenes = getAllergenesChecked();
        List<String> regimes = getRegimesChecked();
        String retrait = getCheckedRetrait();

        if (nom.isEmpty() || prix.isEmpty() || portion.isEmpty()) {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Remplis nom, prix et portions", Toast.LENGTH_SHORT).show();
            }
            addDishButton.setEnabled(true);
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> platMap = new HashMap<>();
        platMap.put("nom", nom);
        platMap.put("prix", prix);
        platMap.put("imageUrl", url);
        platMap.put("regimes", regimes);
        platMap.put("retrait", retrait);
        platMap.put("userId", userId);
        platMap.put("userPrenom", userPrenom);
        platMap.put("userAppartement", userAppartement);
        platMap.put("description", description);
        platMap.put("horaire", horaire);
        platMap.put("portion", portion); // ← nombre de portions
        platMap.put("allergenes", allergenes);
        platMap.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("plats")
                .add(platMap)
                .addOnSuccessListener(documentReference -> {
                    if (!isAdded() || getActivity() == null) return;

                    Toast.makeText(getActivity(), "Plat ajouté avec succès !", Toast.LENGTH_SHORT).show();
                    clear();
                    addDishButton.setEnabled(true);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new MenuFragment())
                            .commitAllowingStateLoss();
                })
                .addOnFailureListener(e -> {
                    err("Firestore", e);
                    addDishButton.setEnabled(true);
                });
    }

    private void clear() {
        nomPlat.setText("");
        prixPlat.setText("");
        descriptionEditText.setText("");
        horaireEditText.setText("");
        nbPortionsEditText.setText("");
        platImageView.setImageResource(R.drawable.ic_launcher_background);
        allergenGroup.clearChecked();
        btnPickup.setChecked(false);
        btnHome.setChecked(false);
        btnKasher.setChecked(false);
        btnHalal.setChecked(false);
        btnVegetarien.setChecked(false);
        btnVegan.setChecked(false);
        imageUri = null;
    }

    private void err(String step, Exception e) {
        Log.e("AjouterPlat", step + "❌", e);
        if (isAdded()) {
            Toast.makeText(requireContext(), step + " : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
