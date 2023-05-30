package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.UserInfo;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewChatDialogRepository {
    private static NewChatDialogRepository instance;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private PreferenceManager preferenceManager;
    private String currentUserId;

    public static NewChatDialogRepository getInstance(){
        if (instance == null)
            instance = new NewChatDialogRepository();
        return instance;
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        preferenceManager = PreferenceManager.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
    }

    public void createNewChat(List<UserInfo> friends, String chatName, Uri chatPhoto, OnChatCreatedCallback onChatCreatedCallback) {
        String chatId = databaseReference.child(Constants.KEY_DATABASE_CHATS).push().getKey();
        ArrayList<String> chatParticipants = new ArrayList<>();
        for (UserInfo friend : friends) {
            if (friend.getIsChecked())
                chatParticipants.add(friend.getId());
        }
        chatParticipants.add(currentUserId);

        HashMap<String, Object> newChatInfo = new HashMap<>();
        newChatInfo.put(Constants.KEY_CHAT_NAME, chatName);
        newChatInfo.put(Constants.KEY_CHAT_PARTICIPANTS, chatParticipants);

        HashMap<String, Object> newChatInfoForParticipants = new HashMap<>();
        newChatInfoForParticipants.put(Constants.KEY_CHAT_NAME, chatName);
        newChatInfoForParticipants.put(Constants.KEY_TIMESTAMP, 0);
        newChatInfoForParticipants.put(Constants.KEY_LAST_MESSAGE, "");

        if (chatPhoto != null) {
            StorageReference ref = storageReference.child(Constants.KEY_FIRESTORE_CHATS_IMAGES).child(chatId);
            UploadTask uploadTask = ref.putFile(chatPhoto);
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful())
                    Log.d("image", "Unable to load profile image");
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadedChatPhoto = task.getResult();
                    String chatString = downloadedChatPhoto.toString();

                    newChatInfo.put(Constants.KEY_IMAGE, chatString);
                    newChatInfoForParticipants.put(Constants.KEY_IMAGE, chatString);

                    chatParticipants.forEach((currentParticipantId) -> databaseReference.child(Constants.KEY_DATABASE_USERS).
                            child(currentParticipantId).child(Constants.KEY_RECENT_CHATS).child(chatId).setValue(newChatInfoForParticipants));

                    databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).setValue(newChatInfo).
                            addOnCompleteListener(chatTask -> {
                                if (!task.isSuccessful()) {
                                    Log.d("newChat", "Unable to create new chat");
                                }
                                onChatCreatedCallback.onChatCreatedCallback(chatId);
                            });
                }
            });
        } else {
            chatParticipants.forEach((currentParticipantId) -> databaseReference.child(Constants.KEY_DATABASE_USERS).
                    child(currentParticipantId).child(Constants.KEY_RECENT_CHATS).child(chatId).setValue(newChatInfoForParticipants));

            databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).setValue(newChatInfo).
                    addOnCompleteListener(chatTask -> {
                        if (!chatTask.isSuccessful()) {
                            Log.d("newChat", "Unable to create new chat");
                        }
                        onChatCreatedCallback.onChatCreatedCallback(chatId);
                    });
        }
    }

    public void getFriends(List<UserInfo> friendsList, OnFriendsLoadedCallback onFriendsLoadedCallback) {
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).child(Constants.KEY_FRIENDS).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadFriends(snapshot, friendsList);
                onFriendsLoadedCallback.onFriendsLoadedCallback(friendsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("newChat", "Unable to load friends");
            }
        });
    }

    private void loadFriends(DataSnapshot friendsDataSnapshot, List<UserInfo> friends){
        friends.clear();
        //Looping through list of IDs of current user friends list
        for (DataSnapshot anotherSnapshot : friendsDataSnapshot.getChildren()) {

            String anotherFriendId = anotherSnapshot.getKey();

            String name = anotherSnapshot.child(Constants.KEY_NAME).getValue().toString();
            String surname = anotherSnapshot.child(Constants.KEY_SURNAME).getValue().toString();
            String image = "";
            if (anotherSnapshot.hasChild(Constants.KEY_IMAGE)) {
                image = anotherSnapshot.child(Constants.KEY_IMAGE).getValue().toString();
            }

            UserInfo anotherUser = new UserInfo(name, surname, image, anotherFriendId);
            friends.add(anotherUser);
        }
    }
}
