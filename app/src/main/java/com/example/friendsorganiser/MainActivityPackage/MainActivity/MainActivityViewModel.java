package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel implements OnUserPhotoLoadedCallback, OnNotificationsNumberLoadedCallback{

    private MainActivityRepository mainActivityRepository;
    private MutableLiveData<String> currentUserId;
    private MutableLiveData<Uri> currentUserPhoto;
    private MutableLiveData<Boolean> isNotifications;

    public void init(){
        mainActivityRepository = MainActivityRepository.getRepoInstance();
        mainActivityRepository.init();

        currentUserId = new MutableLiveData<>();
        currentUserId.setValue(mainActivityRepository.getCurrentUserId());

        currentUserPhoto = new MutableLiveData<>();
        mainActivityRepository.getCurrentUserPhoto(this);

        isNotifications = new MutableLiveData<>();
        mainActivityRepository.getNumberOfNotifications(this);
    }

    public void setToken(){
        mainActivityRepository.setToken();
    }

    public LiveData<String> getCurrentUserId(){
        return currentUserId;
    }

    public LiveData<Uri> getCurrentUserPhoto(){
        return currentUserPhoto;
    }

    public LiveData<Boolean> getIsNotifications(){
        return isNotifications;
    }

    @Override
    public void onNotificationsNumberLoadedCallback(boolean isNotificationsPresence) {
        isNotifications.setValue(isNotificationsPresence);
    }

    @Override
    public void onUserPhotoLoadedCallback(Uri loadedUserPhoto) {
        currentUserPhoto.setValue(loadedUserPhoto);
    }
}
