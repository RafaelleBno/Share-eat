package com.example.myapplication.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Plat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AjouterPlatFragment extends Fragment {

    private ImageView platImageView;
    private EditText nomPlat, prixPlat, descriptionEditText, horaireEditText, poidsEditText;
    private Spinner portionSpinner;
    private Button addDishButton;
    private MaterialButtonToggleGroup regimeGroup, retraitGroup, allergenGroup;

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

        // Initialisation des vues
        platImageView = view.findViewById(R.id.platImageView);
        nomPlat = view.findViewById(R.id.nomPlat);
        prixPlat = view.findViewById(R.id.prixPlat);
        descriptionEditText = view.findViewById(R.id.descriptionPlat);
        horaireEditText = view.findViewById(R.id.horaireRetrait);
        poidsEditText = view.findViewById(R.id.poidsEditText);
        portionSpinner = view.findViewById(R.id.portionSpinner);
        addDishButton = view.findViewById(R.id.addDishButton);
        regimeGroup = view.findViewById(R.id.regimeGroup);
        retraitGroup = view.findViewById(R.id.retraitGroup);
        allergenGroup = view.findViewById(R.id.allergenGroup);

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
                Toast.makeText(requireContext(), "Choisis une image", Toast.LENGTH_SHORT).show();
                addDishButton.setEnabled(true);
            }
        });
    }

    private void openGallery() {
        Intent pick = new Intent(Intent.ACTION_PICK).setType("image/*");
        imagePickerLauncher.launch(pick);
    }

    private String checkedText(MaterialButtonToggleGroup grp) {
        int id = grp.getCheckedButtonId();
        if (id == View.NO_ID) return "";
        return ((MaterialButton) grp.findViewById(id)).getText().toString();
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

    private void uploadImage() {
        StorageReference ref = storageRef.child(UUID.randomUUID() + ".jpg");
        ref.putFile(imageUri)
                .addOnSuccessListener(s -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> fetchUserAndSavePlat(uri.toString()))
                        .addOnFailureListener(e -> {
                            err("URL", e);
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
                        Toast.makeText(requireContext(), "Profil utilisateur introuvable", Toast.LENGTH_SHORT).show();
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
        String portion = poidsEditText.getText().toString().trim() + "g";
        List<String> allergenes = getAllergenesChecked();

        if (nom.isEmpty() || prix.isEmpty()) {
            Toast.makeText(requireContext(), "Remplis nom + prix", Toast.LENGTH_SHORT).show();
            addDishButton.setEnabled(true);
            return;
        }

        String regime = checkedText(regimeGroup);
        String retrait = checkedText(retraitGroup);
        String userId = auth.getCurrentUser().getUid();

        Plat plat = new Plat(nom, prix, url, regime, retrait, userId, userPrenom, userAppartement, description, horaire, portion, allergenes);

        firestore.collection("plats")
                .add(plat)
                .addOnSuccessListener(d -> {
                    Toast.makeText(requireContext(), "Plat ajouté", Toast.LENGTH_SHORT).show();
                    clear();
                    addDishButton.setEnabled(true);
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
        poidsEditText.setText("");
        platImageView.setImageResource(R.drawable.ic_launcher_background);
        regimeGroup.clearChecked();
        retraitGroup.clearChecked();
        allergenGroup.clearChecked();
        imageUri = null;
    }

    private void err(String step, Exception e) {
        Log.e("AjouterPlat", step + "❌", e);
        Toast.makeText(requireContext(), step + " : " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}

