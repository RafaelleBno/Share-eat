package com.example.myapplication.models;

import com.google.firebase.Timestamp;
import java.util.List;

public class Plat {

    public String nom;
    public String prix;
    public String imageUrl;
    public String regime;
    public String retrait;
    public String userId;
    public String userPrenom;
    public String userAppartement;
    public String description;
    public String horaire;
    public String portion;
    public List<String> allergenes;
    public List<String> regimes;
    public Timestamp timestamp;
    public String documentId;
    public boolean isLiked = false;

    // Constructeur vide requis par Firestore
    public Plat() {}

    // ✅ Constructeur simple pour ajouter un plat en mémoire (commande)
    public Plat(String nom, String prix, String imageUrl) {
        this.nom = nom;
        this.prix = prix;
        this.imageUrl = imageUrl;
    }

    // ✅ Constructeur complet (Firestore / chargement complet)
    public Plat(String nom, String prix, String imageUrl, String regime, String retrait, String userId,
                String userPrenom, String userAppartement, String description,
                String horaire, String portion, List<String> allergenes,
                List<String> regimes) {

        this.nom = nom;
        this.prix = prix;
        this.imageUrl = imageUrl;
        this.regime = regime;
        this.retrait = retrait;
        this.userId = userId;
        this.userPrenom = userPrenom;
        this.userAppartement = userAppartement;
        this.description = description;
        this.horaire = horaire;
        this.portion = portion;
        this.allergenes = allergenes;
        this.regimes = regimes;
        this.timestamp = Timestamp.now();
    }

    // Getters (si besoin pour les adapters)
    public String getNom() { return nom; }
    public String getPrix() { return prix; }
    public String getImageUrl() { return imageUrl; }
}

