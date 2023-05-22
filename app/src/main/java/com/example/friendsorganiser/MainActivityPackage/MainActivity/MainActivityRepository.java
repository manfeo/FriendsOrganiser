package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import android.util.Log;

import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivityRepository {

    private static MainActivityRepository instance;
    private DatabaseReference databaseReference;
    private String currentUserId;

    public static MainActivityRepository getRepoInstance(){
        if (instance == null)
            instance = new MainActivityRepository();

        return instance;
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = PreferenceManager.getInstance().getString(Constants.KEY_USER_ID);
    }

    public void setToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    public String getCurrentUserId(){
        return currentUserId;
    }

    private void updateToken(String token){
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).
                child(Constants.KEY_FCM_TOKEN).setValue(token).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("token", "token updated");
                    } else {
                        String errorMessage = task.getException().toString();
                        Log.d("token", "failed to update token");
                    }
                });
    }
}
