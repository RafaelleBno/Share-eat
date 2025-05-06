package com.example.myapplication.fragments;

public class Message {
    private String userName;
    private String status;
    private String time;
    private int avatarResId;

    // Constructeur
    public Message(String userName, String status, String time, int avatarResId) {
        this.userName = userName;
        this.status = status;
        this.time = time;
        this.avatarResId = avatarResId;
    }

    // Getters et Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }
}

