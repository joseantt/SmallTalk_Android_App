package com.example.smalltalk.models;

public class OpenChatsModel {
    private String email;
    private String lastMessage;

    public OpenChatsModel(String email, String lastMessage) {
        this.email = email;
        this.lastMessage = lastMessage;
    }

    public OpenChatsModel() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
