package com.example.friendsorganiser;

import android.app.Application;
import com.example.friendsorganiser.Utilities.PreferenceManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.getInstance().init(getApplicationContext());
    }
}
