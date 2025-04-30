package com.example.myapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Plat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.*;

import java.util.UUID;

public class AjouterPlatFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView platImageView;
    private EditText nomPlat, prixPlat;
    private Button addDishButton;
    private Uri imageUri;

    private FirebaseFirestore firestore;
    private StorageReference storageRef;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_ajouter_plat, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        platImageView = view.findViewById(R.id.platImageView);
        nomPlat = view.findViewById(R.id.nomPlat);
        prixPlat = view.findViewById(R.id.prixPlat);
        addDishButton = view.findViewById(R.id.addDishButton);

        firestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("plats");
        auth = FirebaseAuth.getInstance();

        // Ouvrir la galerie quand on clique sur l'image
        platImageView.setOnClickListener(v -> openGallery());

        // Ajouter plat quand on clique sur "ADD"
        addDishButton.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebase();
            } else {
                Toast.makeText(getContext(), "Choisissez une image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(platImageView);
        }
    }

    private void uploadImageToFirebase() {
        String imageName = UUID.randomUUID().toString(); // nom unique
        StorageReference imageRef = storageRef.child(imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                savePlatToFirestore(downloadUrl);
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Échec de l'upload : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void savePlatToFirestore(String imageUrl) {
        String nom = nomPlat.getText().toString().trim();
        String prix = prixPlat.getText().toString().trim();

        if (nom.isEmpty() || prix.isEmpty()) {
            Toast.makeText(getContext(), "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Plat plat = new Plat(nom, prix, imageUrl);

        firestore.collection("plats")
                .add(plat)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Plat ajouté avec succès", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
