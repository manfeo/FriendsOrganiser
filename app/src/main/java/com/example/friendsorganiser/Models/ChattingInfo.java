package com.example.friendsorganiser.Models;

import android.net.Uri;

public class ChattingInfo {
    private String chatTitle;
    private Uri chatPhoto;

    public ChattingInfo(String chatTitle, Uri chatPhoto) {
        this.chatTitle = chatTitle;
        this.chatPhoto = chatPhoto;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public Uri getChatPhoto() {
        return chatPhoto;
    }
}
