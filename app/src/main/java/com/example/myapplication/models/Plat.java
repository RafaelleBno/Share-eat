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
    public Timestamp timestamp;
    public String documentId;

    public boolean isLiked = false;

    public Plat() {}

    public Plat(String nom, String prix, String imageUrl, String regime, String retrait, String userId,
                String userPrenom, String userAppartement,
                String description, String horaire, String portion, List<String> allergenes) {
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
        this.timestamp = Timestamp.now();

    }
}
