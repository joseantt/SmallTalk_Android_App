package com.example.smalltalk.models;

import java.util.Date;

public class ChatMessage {
    private String id, chatId, senderEmail, message, sendedAt;
    private Date dateObject;

    public ChatMessage(String id, String chatId, String senderEmail, String message, String sendedAt, Date dateObject) {
        this.id = id;
        this.chatId = chatId;
        this.senderEmail = senderEmail;
        this.message = message;
        this.sendedAt = sendedAt;
        this.dateObject = dateObject;
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

    public String getSendedAt() {
        return sendedAt;
    }

    public void setSendedAt(String sendedAt) {
        this.sendedAt = sendedAt;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }
}
