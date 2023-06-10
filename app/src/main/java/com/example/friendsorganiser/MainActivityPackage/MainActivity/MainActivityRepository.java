package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    public void getCurrentUserPhoto(OnUserPhotoLoadedCallback onUserPhotoLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).
                child(Constants.KEY_IMAGE).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Uri userPhoto = null;
                        if (snapshot.exists()) {
                            userPhoto = Uri.parse(snapshot.getValue().toString());
                        }
                        onUserPhotoLoadedCallback.onUserPhotoLoadedCallback(userPhoto);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("userPhoto", "Unable to load user photo");
                    }
                });
    }

    public void getNumberOfNotifications(OnNotificationsNumberLoadedCallback onNotificationsNumberLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_NOTIFICATIONS).child(currentUserId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isNotifications = snapshot.exists();
                        onNotificationsNumberLoadedCallback.onNotificationsNumberLoadedCallback(isNotifications);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("userNotifications", "Unable to load number of notifications");
                    }
                });
    }
}
