package com.example.smalltalk.models;

import java.util.Date;

public class ChatMessage {
    private String id, chatId, senderEmail, message, sentAt, imageUrl;
    private Date dateObject;

    public ChatMessage(String id, String chatId, String senderEmail, String message, String sentAt, Date dateObject, String imageUrl) {
        this.id = id;
        this.chatId = chatId;
        this.senderEmail = senderEmail;
        this.message = message;
        this.sentAt = sentAt;
        this.dateObject = dateObject;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
