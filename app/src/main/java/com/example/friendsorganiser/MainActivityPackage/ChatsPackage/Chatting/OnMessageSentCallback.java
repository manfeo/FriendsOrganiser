package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting;

import com.example.friendsorganiser.Models.ChatMessage;

import java.util.List;

public interface OnMessageSentCallback {
    void onMessageSentCallback(List<ChatMessage> chatMessageList);

}
