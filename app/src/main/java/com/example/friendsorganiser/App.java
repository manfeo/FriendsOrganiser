package com.example.friendsorganiser;

import android.app.Application;

import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.yandex.mapkit.MapKitFactory;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.getInstance().init(getApplicationContext());
        MapKitFactory.setApiKey(Constants.KEY_API_MAPS);
    }
}
