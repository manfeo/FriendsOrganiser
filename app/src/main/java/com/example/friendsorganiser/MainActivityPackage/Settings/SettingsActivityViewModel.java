package com.example.friendsorganiser.MainActivityPackage.Settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsActivityViewModel extends ViewModel {

    private SettingsActivityRepository settingsActivityRepository;
    private MutableLiveData<String> currentUserId;


    public void init(){
        settingsActivityRepository = SettingsActivityRepository.getInstance();
        settingsActivityRepository.init();
        currentUserId = new MutableLiveData<>();
        currentUserId.setValue(settingsActivityRepository.getCurrentUserId());
    }

    public LiveData<String> getCurrentUserId(){
        return currentUserId;
    }

    public void signOut(){
        settingsActivityRepository.signOut();
    }

}
