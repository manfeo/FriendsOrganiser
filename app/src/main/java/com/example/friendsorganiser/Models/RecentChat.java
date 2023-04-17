package com.example.friendsorganiser.Models;

import java.time.LocalDateTime;

public class RecentChat {
    private String chatId, chatName, lastMessage, messageSentTime;
    private LocalDateTime messageSentTimeDate;

    public RecentChat(String chatId, String chatName, String lastMessage, String messageSentTime, LocalDateTime messageSentTimeDate) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.lastMessage = lastMessage;
        this.messageSentTime = messageSentTime;
        this.messageSentTimeDate = messageSentTimeDate;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getMessageSentTime() {
        return messageSentTime;
    }

    public LocalDateTime getMessageSentTimeDate() {
        return messageSentTimeDate;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setMessageSentTime(String messageSentTime) {
        this.messageSentTime = messageSentTime;
    }

    public void setMessageSentTimeDate(LocalDateTime messageSentTimeDate) {
        this.messageSentTimeDate = messageSentTimeDate;
    }
}
