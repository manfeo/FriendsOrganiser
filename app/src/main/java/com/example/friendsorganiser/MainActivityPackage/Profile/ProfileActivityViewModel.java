package com.example.friendsorganiser.MainActivityPackage.Profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.UserInfo;

public class ProfileActivityViewModel extends ViewModel implements OnUserLoadedCallback {

    private ProfileActivityRepository profileActivityRepository;

    private MutableLiveData<UserInfo> userInfo;


    public LiveData<UserInfo> getUserInfo(){
        return userInfo;
    }

    public void init(){
        profileActivityRepository = ProfileActivityRepository.getInstance();
        userInfo = new MutableLiveData<>();
    }

    public void loadUser(String userId){
        profileActivityRepository.loadUser(userId, this);
    }

    @Override
    public void onUserLoadedCallback(UserInfo loadedUserInfo) {
        userInfo.setValue(loadedUserInfo);
    }
}
