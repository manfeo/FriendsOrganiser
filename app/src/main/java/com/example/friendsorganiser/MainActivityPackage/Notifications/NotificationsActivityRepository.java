package com.example.friendsorganiser.MainActivityPackage.Notifications;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsActivityRepository {
    private static NotificationsActivityRepository instance;
    private DatabaseReference databaseReference;
    private PreferenceManager preferenceManager;
    private String currentUserId;

    public static NotificationsActivityRepository getInstance(){
        if (instance == null)
            instance = new NotificationsActivityRepository();
        return instance;
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
    }

    public void loadNotifications(List<UserInfo> notifications, OnNotificationsLoadedCallback onNotificationsLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_NOTIFICATIONS).child(currentUserId).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectNotifications(notifications, snapshot);
                onNotificationsLoadedCallback.onNotificationsLoadedCallback(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("notifications", "Unable to load notifications");
            }
        });
    }

    private void collectNotifications(List<UserInfo> notifications, DataSnapshot snapshot){
        notifications.clear();
        for (DataSnapshot anotherSnapshot : snapshot.getChildren()){
            String newFriendName = anotherSnapshot.child(Constants.KEY_NAME).getValue().toString();
            String newFriendSurname = anotherSnapshot.child(Constants.KEY_SURNAME).getValue().toString();
            String newFriendId = anotherSnapshot.getKey();
            Uri newFriendImage = null;
            if (anotherSnapshot.hasChild(Constants.KEY_IMAGE))
                newFriendImage = Uri.parse(anotherSnapshot.child(Constants.KEY_IMAGE).getValue().toString());
            UserInfo anotherFriendRequest = new UserInfo(newFriendName, newFriendSurname, newFriendImage, newFriendId);
            notifications.add(anotherFriendRequest);
        }
    }

    public void acceptFriendship(UserInfo acceptedFriend){
        //Adding info of current user to accepted friend
        String myName = preferenceManager.getString(Constants.KEY_NAME);
        String mySurname = preferenceManager.getString(Constants.KEY_SURNAME);

        Map<String, Object> toFriendMap = new HashMap<>();
        toFriendMap.put(Constants.KEY_NAME, myName);
        toFriendMap.put(Constants.KEY_SURNAME, mySurname);
        if (preferenceManager.contains(Constants.KEY_IMAGE)) {
            String myPhoto = preferenceManager.getString(Constants.KEY_IMAGE);
            toFriendMap.put(Constants.KEY_IMAGE, myPhoto);
        }

        String acceptedFriendId = acceptedFriend.getId();

        databaseReference.child(Constants.KEY_DATABASE_USERS).child(acceptedFriendId).
                child(Constants.KEY_FRIENDS).child(currentUserId).setValue(toFriendMap);

        //Adding info of accepted friend to current user
        String friendName = acceptedFriend.getName();
        String friendSurname = acceptedFriend.getSurname();
        Map<String, Object> toMeMap = new HashMap<>();
        toMeMap.put(Constants.KEY_NAME, friendName);
        toMeMap.put(Constants.KEY_SURNAME, friendSurname);
        Uri friendPhoto = acceptedFriend.getPhoto();
        if (friendPhoto != null) {
            String rawFriendPhoto = friendPhoto.toString();
            toMeMap.put(Constants.KEY_IMAGE, rawFriendPhoto);
        }

        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).
                child(Constants.KEY_FRIENDS).child(acceptedFriendId).setValue(toMeMap);

        databaseReference.child(Constants.KEY_DATABASE_NOTIFICATIONS).child(currentUserId).child(acceptedFriendId).removeValue();
    }

    public void rejectFriendship(UserInfo rejectedFriend){
        String rejectedFriendId = rejectedFriend.getId();
        databaseReference.child(Constants.KEY_DATABASE_NOTIFICATIONS).child(currentUserId).child(rejectedFriendId).removeValue();
    }
}
