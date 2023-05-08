package com.example.friendsorganiser.MainActivityPackage.Profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Models.UserProfileInfo;

public class ProfileActivityViewModel extends ViewModel implements OnUserLoadedCallback {

    private ProfileActivityRepository profileActivityRepository;

    private MutableLiveData<UserProfileInfo> userInfo;


    public LiveData<UserProfileInfo> getUserInfo(){
        return userInfo;
    }

    public void init(){
        profileActivityRepository = ProfileActivityRepository.getInstance();
        profileActivityRepository.init();
        userInfo = new MutableLiveData<>();
    }

    public void loadUser(String userId){
        profileActivityRepository.loadUser(userId, this);
    }

    @Override
    public void onUserLoadedCallback(UserProfileInfo loadedUserInfo) {
        userInfo.setValue(loadedUserInfo);
    }
}
