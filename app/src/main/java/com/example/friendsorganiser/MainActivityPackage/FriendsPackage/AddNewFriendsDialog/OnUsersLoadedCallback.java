package com.example.friendsorganiser.MainActivityPackage.FriendsPackage.AddNewFriendsDialog;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.List;

public interface OnUsersLoadedCallback {
    void onUsersLoadedCallback(List<UserInfo> allUsersList);
}
