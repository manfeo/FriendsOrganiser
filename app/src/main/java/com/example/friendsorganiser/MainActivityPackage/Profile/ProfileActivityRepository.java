package com.example.friendsorganiser.MainActivityPackage.Profile;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.UserProfileInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivityRepository {
    private static ProfileActivityRepository instance;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private PreferenceManager preferenceManager;
    private String userId;

    public static ProfileActivityRepository getInstance(){
        if (instance == null)
            instance = new ProfileActivityRepository();
        return instance;
    }

    public boolean init(String currentUserId){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        preferenceManager = PreferenceManager.getInstance();
        userId = currentUserId;
        return userId.equals(preferenceManager.getString(Constants.KEY_USER_ID));
    }

    public void uploadImage(Uri newImage){
        StorageReference ref = storageReference.child(Constants.KEY_FIRESTORE_PROFILE_IMAGES).child(userId);
        UploadTask uploadTask = ref.putFile(newImage);
        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                Log.d("image", "Unable to load profile image");
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String stringUri = downloadUri.toString();
                //Updating preference manager
                preferenceManager.putString(Constants.KEY_IMAGE, stringUri);
                //Updating current user image
                databaseReference.child(Constants.KEY_DATABASE_USERS).child(userId).child(Constants.KEY_IMAGE).setValue(stringUri);
                //Updating all friends' "image" field
                databaseReference.child(Constants.KEY_DATABASE_USERS).child(userId).child(Constants.KEY_FRIENDS).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    String friendId = dataSnapshot.getRef().getKey();
                                    databaseReference.child(Constants.KEY_DATABASE_USERS).child(friendId).child(Constants.KEY_FRIENDS).
                                            child(userId).child(Constants.KEY_IMAGE).setValue(stringUri);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            } else {
                Log.d("image", "Unable to get image URI");
            }
        });
    }

    public void loadUser(OnUserLoadedCallback onUserLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.child(Constants.KEY_NAME).getValue().toString();
                        String userSurname = snapshot.child(Constants.KEY_SURNAME).getValue().toString();
                        String userBirthDate = snapshot.child(Constants.KEY_DATE_OF_BIRTH).getValue().toString();
                        String userEmail = snapshot.child(Constants.KEY_EMAIL).getValue().toString();
                        Uri image;
                        if (snapshot.hasChild(Constants.KEY_IMAGE))
                            image = Uri.parse(snapshot.child(Constants.KEY_IMAGE).getValue().toString());
                        else
                            image = null;
                        UserProfileInfo userProfileInfo = new UserProfileInfo(userName, userSurname, userEmail,
                                userBirthDate, userId, image);
                        onUserLoadedCallback.onUserLoadedCallback(userProfileInfo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("profile", error.toString());
                    }
                });
    }
}
