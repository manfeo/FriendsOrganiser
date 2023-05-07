package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog;

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
import java.util.HashMap;
import java.util.List;

public class NewChatDialogRepository {
    private static NewChatDialogRepository instance;
    private DatabaseReference databaseReference;
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
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
    }

    public void createNewChat(List<UserInfo> friends, String chatName, OnChatCreatedCallback onChatCreatedCallback) {
        ArrayList<String> chatParticipants = new ArrayList<>();
        for (UserInfo friend : friends){
            if (friend.getIsChecked())
                chatParticipants.add(friend.getId());
        }
        chatParticipants.add(currentUserId);

        HashMap<String, Object> newChatInfo = new HashMap<>();
        newChatInfo.put(Constants.KEY_CHAT_NAME, chatName);
        newChatInfo.put(Constants.KEY_CHAT_PARTICIPANTS, chatParticipants);

        String chatId = databaseReference.child(Constants.KEY_DATABASE_CHATS).push().getKey();

        HashMap<String, Object> newChatInfoForParticipants = new HashMap<>();
        newChatInfoForParticipants.put(Constants.KEY_CHAT_NAME, chatName);
        newChatInfoForParticipants.put(Constants.KEY_TIMESTAMP, 0);
        newChatInfoForParticipants.put(Constants.KEY_LAST_MESSAGE, "");

        chatParticipants.forEach((currentParticipantId) -> databaseReference.child(Constants.KEY_DATABASE_USERS).
                child(currentParticipantId).child(Constants.KEY_RECENT_CHATS).child(chatId).setValue(newChatInfoForParticipants));

        databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).setValue(newChatInfo).
                addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("newChat", "Unable to create new chat");
                    }
                    onChatCreatedCallback.onChatCreatedCallback(chatId);
                });
    }

    public void getFriends(List<UserInfo> friendsList, OnFriendsLoadedCallback onFriendsLoadedCallback) {
        databaseReference.child(Constants.KEY_DATABASE_USERS).addValueEventListener(new ValueEventListener() {
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
        if (friendsDataSnapshot.child(currentUserId).hasChild(Constants.KEY_FRIENDS)) {
            friends.clear();
            //Looping through list of IDs of current user friends list
            for (DataSnapshot anotherSnapshot : friendsDataSnapshot.child(currentUserId).
                    child(Constants.KEY_FRIENDS).getChildren()) {

                String anotherFriendId = anotherSnapshot.getValue().toString();
                if (currentUserId.equals(anotherFriendId))
                    continue;

                String name = friendsDataSnapshot.child(anotherFriendId).child(Constants.KEY_NAME).getValue().toString();
                String surname = friendsDataSnapshot.child(anotherFriendId).child(Constants.KEY_SURNAME).getValue().toString();
                String image = "";
                if (friendsDataSnapshot.child(anotherFriendId).hasChild(Constants.KEY_IMAGE)) {
                    image = friendsDataSnapshot.child(anotherFriendId).child(Constants.KEY_IMAGE).getValue().toString();
                }
                String token = "";
                if (friendsDataSnapshot.child(anotherFriendId).hasChild(Constants.KEY_FCM_TOKEN)) {
                    token = friendsDataSnapshot.child(anotherFriendId).child(Constants.KEY_FCM_TOKEN).getValue().toString();
                }
                String id = friendsDataSnapshot.child(anotherFriendId).getKey();
                UserInfo anotherUser = new UserInfo(name, surname, image, token, id);
                friends.add(anotherUser);
            }
        }
    }
}
