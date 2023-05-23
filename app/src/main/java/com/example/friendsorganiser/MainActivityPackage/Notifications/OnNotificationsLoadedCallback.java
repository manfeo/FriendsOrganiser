package com.example.friendsorganiser.MainActivityPackage.Notifications;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.List;

public interface OnNotificationsLoadedCallback {
    void onNotificationsLoadedCallback(List<UserInfo> loadedNotifications);
}
