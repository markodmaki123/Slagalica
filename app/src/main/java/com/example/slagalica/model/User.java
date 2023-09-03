package com.example.slagalica.model;

public class User {
    public String username;
    public String email;
    public int brojPobjeda;
    private String profilePictureUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumberOfWins() {
        return brojPobjeda;
    }

    public void setNumberOfWins(int numberOfWins) {
        this.brojPobjeda = numberOfWins;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public User() {
    }
}
