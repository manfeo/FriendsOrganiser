package com.example.friendsorganiser.MainActivityPackage.Settings;

import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ConcurrentModificationException;

public class SettingsActivityRepository {
    private static SettingsActivityRepository instance;
    private PreferenceManager preferenceManager;
    private DatabaseReference databaseReference;

    public static SettingsActivityRepository getInstance(){
        if (instance == null)
            instance = new SettingsActivityRepository();
        return instance;
    }

    public void init(){
        preferenceManager = PreferenceManager.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void signOut(){
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).
                child(Constants.KEY_FCM_TOKEN).getRef().removeValue();

        preferenceManager.clear();
    }

    public String getCurrentUserId(){
        return preferenceManager.getString(Constants.KEY_USER_ID);
    }
}
