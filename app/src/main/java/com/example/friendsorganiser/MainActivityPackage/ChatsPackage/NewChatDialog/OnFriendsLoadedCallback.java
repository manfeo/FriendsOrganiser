package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.List;

public interface OnFriendsLoadedCallback {
    void onFriendsLoadedCallback(List<UserInfo> currentFriendsList);
}
