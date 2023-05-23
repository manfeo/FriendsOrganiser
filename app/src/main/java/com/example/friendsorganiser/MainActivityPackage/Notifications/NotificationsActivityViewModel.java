package com.example.friendsorganiser.MainActivityPackage.Notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationsActivityViewModel extends ViewModel implements OnNotificationsLoadedCallback{
    private NotificationsActivityRepository notificationsActivityRepository;
    private MutableLiveData<List<UserInfo>> notifications;
    private List<UserInfo> allNotifications;

    public void init(){
        notificationsActivityRepository = NotificationsActivityRepository.getInstance();
        notificationsActivityRepository.init();

        notifications = new MutableLiveData<>();
        allNotifications = new ArrayList<>();
        notifications.setValue(allNotifications);

        loadNotifications();
    }

    public LiveData<List<UserInfo>> getNotifications(){
        return notifications;
    }

    public void loadNotifications(){
        notificationsActivityRepository.loadNotifications(allNotifications, this);
    }

    public void onNotificationAnswer(UserInfo notification, String userAction){
        if (userAction.equals(Constants.KEY_ACCEPT_FRIENDSHIP)) {
            notificationsActivityRepository.acceptFriendship(notification);
        } else if (userAction.equals(Constants.KEY_REJECT_FRIENDSHIP)){
            notificationsActivityRepository.rejectFriendship(notification);
        }
    }

    @Override
    public void onNotificationsLoadedCallback(List<UserInfo> loadedNotifications) {
        notifications.setValue(loadedNotifications);
    }
}
