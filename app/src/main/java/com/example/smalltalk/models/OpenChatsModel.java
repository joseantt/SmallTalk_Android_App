package com.example.smalltalk.models;

import java.io.Serializable;
import java.util.Date;

public class OpenChatsModel implements Serializable {
    private String id, lastMessage, userEmailOne, userEmailTwo;
    private Date createdAt;

    public OpenChatsModel() {
    }

    public OpenChatsModel(String id, String lastMessage, String userEmailOne, String userEmailTwo, Date createdAt) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.userEmailOne = userEmailOne;
        this.userEmailTwo = userEmailTwo;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUserEmailOne() {
        return userEmailOne;
    }

    public void setUserEmailOne(String userEmailOne) {
        this.userEmailOne = userEmailOne;
    }

    public String getUserEmailTwo() {
        return userEmailTwo;
    }

    public void setUserEmailTwo(String userEmailTwo) {
        this.userEmailTwo = userEmailTwo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
