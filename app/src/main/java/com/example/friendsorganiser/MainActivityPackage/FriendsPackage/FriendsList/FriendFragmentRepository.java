package com.example.friendsorganiser.MainActivityPackage.FriendsPackage.FriendsList;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendFragmentRepository {
    private static FriendFragmentRepository instance;
    private PreferenceManager preferenceManager;
    private DatabaseReference databaseReference;
    private String currentUserId;

    public static FriendFragmentRepository getInstance(){
        if (instance == null)
            instance = new FriendFragmentRepository();
        return instance;
    }

    public void init(){
        preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadFriends(List<UserInfo> friendsList, OnFriendsLoadedCallback onFriendsLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).child(Constants.KEY_FRIENDS).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadFriends(snapshot, friendsList);
                        onFriendsLoadedCallback.onFriendsLoadedCallback(friendsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("friends", "Unable to load friends");
                    }
                });
    }

    private void loadFriends(DataSnapshot friendsDataSnapshot, List<UserInfo> friendsList){
        friendsList.clear();
        //Looping through list of IDs of current user friends list
        for (DataSnapshot anotherSnapshot : friendsDataSnapshot.getChildren()) {

            String anotherFriendId = anotherSnapshot.getKey();

            String name = anotherSnapshot.child(Constants.KEY_NAME).getValue().toString();
            String surname = anotherSnapshot.child(Constants.KEY_SURNAME).getValue().toString();
            Uri friendPhoto;
            if (anotherSnapshot.hasChild(Constants.KEY_IMAGE)) {
                friendPhoto = Uri.parse(anotherSnapshot.child(Constants.KEY_IMAGE).getValue().toString());
            } else
                friendPhoto = null;
            UserInfo anotherUser = new UserInfo(name, surname, friendPhoto, anotherFriendId);
            friendsList.add(anotherUser);
        }
    }
}
