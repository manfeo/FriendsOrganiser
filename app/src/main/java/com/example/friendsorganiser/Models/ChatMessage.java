package com.example.friendsorganiser.Models;

import java.time.LocalDateTime;
import java.util.List;

public class ChatMessage {
    private String senderId, message, createdAt, fullname;
    private LocalDateTime createdAtDate;
    private List<String> receiversIds;

    public ChatMessage(String senderId, String message, String createdAt,
                       LocalDateTime createdAtDate, String fullname) {
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
        this.createdAtDate = createdAtDate;
        this.fullname = fullname;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<String> getReceiversIds() {
        return receiversIds;
    }

    public String getFullname() {
        return fullname;
    }

    public LocalDateTime getCreatedAtDate() {
        return createdAtDate;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setReceiversIds(List<String> receiversIds) {
        this.receiversIds = receiversIds;
    }

    public void setCreatedAtDate(LocalDateTime createdAtDate) {
        this.createdAtDate = createdAtDate;
    }
}
