package com.example.friendsorganiser.MainActivityPackage.FriendsPackage.FriendsList;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.List;

public interface OnFriendsLoadedCallback {
    void onFriendsLoadedCallback(List<UserInfo> currentUserFriends);
}
