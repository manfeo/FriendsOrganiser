package com.example.friendsorganiser.WelcomeActivity;

import android.text.NoCopySpan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;

public class WelcomeActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> isSignedIn;
    private WelcomeActivityRepository welcomeActivityRepository;

    public LiveData<Boolean> getIsSignedIn(){
        return isSignedIn;
    }

    public void init(){
        isSignedIn = new MutableLiveData<>();
        welcomeActivityRepository = WelcomeActivityRepository.getInstance();
        isSignedIn.setValue(welcomeActivityRepository.getIsSignedIn());
    }
}
