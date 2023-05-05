package com.example.friendsorganiser.MainActivityPackage.Profile;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ConcurrentModificationException;

public class ProfileActivityRepository {

    private static ProfileActivityRepository instance;

    private DatabaseReference databaseReference;

    public static ProfileActivityRepository getInstance(){
        if (instance == null)
            instance = new ProfileActivityRepository();
        return instance;
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadUser(String userId, OnUserLoadedCallback onUserLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.child("name").getValue().toString();
                        String userSurname = snapshot.child("surname").getValue().toString();
                        String userBirthDate = snapshot.child("dateOfBirth").getValue().toString();

                        UserInfo userInfo = new UserInfo(userName, userSurname, userBirthDate, userId);

                        onUserLoadedCallback.onUserLoadedCallback(userInfo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("profile", error.toString());
                    }
                });
    }
}
