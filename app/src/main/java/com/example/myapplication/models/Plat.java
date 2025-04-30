package com.example.myapplication.models;

public class Plat {
    public String nom;
    public String prix;
    public String imageUrl;

    public Plat() {} // Obligatoire pour Firebase

    public Plat(String nom, String prix, String imageUrl) {
        this.nom = nom;
        this.prix = prix;
        this.imageUrl = imageUrl;
    }
}
