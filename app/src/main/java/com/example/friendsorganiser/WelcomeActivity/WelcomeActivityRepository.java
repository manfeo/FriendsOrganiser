package com.example.friendsorganiser.WelcomeActivity;

import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;

public class WelcomeActivityRepository {

    public static WelcomeActivityRepository welcomeActivityRepository;

    public static WelcomeActivityRepository getInstance(){
        if (welcomeActivityRepository == null)
            welcomeActivityRepository = new WelcomeActivityRepository();
        return welcomeActivityRepository;
    }

    public Boolean getIsSignedIn(){
        return PreferenceManager.getInstance().getBoolean(Constants.KEY_IS_SIGNED_IN);
    }

}
