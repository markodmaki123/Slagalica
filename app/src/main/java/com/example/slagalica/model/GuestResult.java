package com.example.slagalica.model;

public class GuestResult {
    private String nickname;
    private int bodovi;

    public GuestResult() {};

    public GuestResult(String nickname, int bodovi) {
        this.nickname = nickname;
        this.bodovi = bodovi;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getBodovi() {
        return bodovi;
    }

    public void setBodovi(int bodovi) {
        this.bodovi = bodovi;
    }
}
