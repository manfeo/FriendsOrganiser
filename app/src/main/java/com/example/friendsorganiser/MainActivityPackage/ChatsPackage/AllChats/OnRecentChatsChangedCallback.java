package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.AllChats;

import com.example.friendsorganiser.Models.RecentChat;

import java.util.List;

public interface OnRecentChatsChangedCallback {
    void onRecentChatsChangedCallback(List<RecentChat> recentChatList);

}
