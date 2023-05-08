package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private MainActivityRepository mainActivityRepository;
    private MutableLiveData<String> currentUserId;

    public void init(){
        mainActivityRepository = MainActivityRepository.getRepoInstance();
        mainActivityRepository.init();
        currentUserId = new MutableLiveData<>();
        currentUserId.setValue(mainActivityRepository.getCurrentUserId());
    }

    public void setToken(){
        mainActivityRepository.setToken();
    }

    public LiveData<String> getCurrentUserId(){
        return currentUserId;
    }
}
