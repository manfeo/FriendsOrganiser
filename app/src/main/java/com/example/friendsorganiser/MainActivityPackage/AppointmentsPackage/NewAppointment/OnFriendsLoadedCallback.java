package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.List;

public interface OnFriendsLoadedCallback {
    void onFriendsLoadedCallback(List<UserInfo> listOfFriends);
}
