package com.example.friendsorganiser.MainActivityPackage.Profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.UserProfileInfo;

import java.io.File;

public class ProfileActivityViewModel extends ViewModel implements OnUserLoadedCallback{
    private ProfileActivityRepository profileActivityRepository;
    private MutableLiveData<UserProfileInfo> userInfo;
    private MutableLiveData<Uri> profileImage;
    public LiveData<UserProfileInfo> getUserInfo(){
        return userInfo;
    }
    public MutableLiveData<Boolean> profileStatus;

    public void init(String userId){
        profileActivityRepository = ProfileActivityRepository.getInstance();
        boolean myProfile = profileActivityRepository.init(userId);

        profileImage = new MutableLiveData<>();
        userInfo = new MutableLiveData<>();
        profileStatus = new MutableLiveData<>();
        profileStatus.setValue(myProfile);
    }

    public void uploadProfileImage(Uri newImage){
        profileActivityRepository.uploadImage(newImage);
    }

    public LiveData<Boolean> isMyProfile(){
        return profileStatus;
    }

    public void loadUser(){
        profileActivityRepository.loadUser(this);
    }

    @Override
    public void onUserLoadedCallback(UserProfileInfo loadedUserInfo) {
        userInfo.setValue(loadedUserInfo);
    }
}
