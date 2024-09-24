package com.example.smalltalk.models;

public class User {
    private String id, email;

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public User () {}

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
}
