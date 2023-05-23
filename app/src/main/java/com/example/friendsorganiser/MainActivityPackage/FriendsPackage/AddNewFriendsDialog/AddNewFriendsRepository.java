package com.example.friendsorganiser.MainActivityPackage.FriendsPackage.AddNewFriendsDialog;

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

public class AddNewFriendsRepository {

    private static AddNewFriendsRepository instance;
    private PreferenceManager preferenceManager;
    private DatabaseReference databaseReference;
    private String currentUserId;

    public static AddNewFriendsRepository getInstance(){
        if (instance == null)
            instance = new AddNewFriendsRepository();
        return instance;
    }

    public void init(){
        preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void getUsers(List<UserInfo> listOfUsers, OnUsersLoadedCallback onUsersLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadUsers(snapshot, listOfUsers);
                onUsersLoadedCallback.onUsersLoadedCallback(listOfUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("addNewFriends", "Unable to load users");
            }
        });
    }

    private void loadUsers(DataSnapshot snapshot, List<UserInfo> users){
        users.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
            if (currentUserId.equals(dataSnapshot.getKey()) || areFriendsAlready(dataSnapshot))
                continue;
            String name = dataSnapshot.child(Constants.KEY_NAME).getValue().toString();
            String surname = dataSnapshot.child(Constants.KEY_SURNAME).getValue().toString();
            String image = "";
            if (dataSnapshot.hasChild(Constants.KEY_IMAGE)) {
                image = dataSnapshot.child(Constants.KEY_IMAGE).getValue().toString();
            }
            String token = "";
            if (dataSnapshot.hasChild(Constants.KEY_FCM_TOKEN)) {
                token = dataSnapshot.child(Constants.KEY_FCM_TOKEN).getValue().toString();
            }
            String id = dataSnapshot.getKey();
            UserInfo anotherUser = new UserInfo(name, surname, image, token, id);
            users.add(anotherUser);
        }
    }

    private boolean areFriendsAlready(DataSnapshot snapshot){
        if (snapshot.hasChild(Constants.KEY_FRIENDS)) {
            for (DataSnapshot dataSnapshot : snapshot.child(Constants.KEY_FRIENDS).getChildren()) {
                if (dataSnapshot.getKey().equals(currentUserId))
                    return true;
            }
        }
        return false;
    }

    public void sendRequests(List<UserInfo> users){
        for (UserInfo user : users){
            if (user.getIsChecked()){
                String anotherFriendUserId = user.getId();

                Map<String, Object> mapForFriend = new HashMap<>();

                String myName = preferenceManager.getString(Constants.KEY_NAME);
                String mySurname = preferenceManager.getString(Constants.KEY_SURNAME);
                mapForFriend.put(Constants.KEY_NAME, myName);
                mapForFriend.put(Constants.KEY_SURNAME, mySurname);

                databaseReference.child(Constants.KEY_DATABASE_NOTIFICATIONS).child(anotherFriendUserId).
                        child(currentUserId).setValue(mapForFriend);
            }
        }
    }
}
