package com.example.friendsorganiser.MainActivityPackage.Notifications;

import com.example.friendsorganiser.Models.UserInfo;

public interface OnNotificationAnswer {
    void onNotificationAnswer(UserInfo notificationUser, String answer);
}
