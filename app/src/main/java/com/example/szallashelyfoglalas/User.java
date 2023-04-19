package com.example.szallashelyfoglalas;

public class User {
    String id;
    String email;
    String type;
    String username;

    public User() {
    }

    public User(String id, String email, String type, String username) {
        this.id = id;
        this.email = email;
        this.type = type;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
