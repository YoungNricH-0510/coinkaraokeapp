package com.holy.coinkaraoke.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private final String id;
    private final String password;
    private final String name;
    private final int balance;
    private final String phone;
    private final List<Integer> favoriteSongs;

    public User(String id, String password, String name, int balance, String phone, List<Integer> favoriteSongs) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.balance = balance;
        this.phone = phone;
        if (favoriteSongs != null) {
            this.favoriteSongs = favoriteSongs;
        } else {
            this.favoriteSongs = new ArrayList<>();
        }
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public String getPhone() {
        return phone;
    }

    public List<Integer> getFavoriteSongs() {
        return favoriteSongs;
    }
}
