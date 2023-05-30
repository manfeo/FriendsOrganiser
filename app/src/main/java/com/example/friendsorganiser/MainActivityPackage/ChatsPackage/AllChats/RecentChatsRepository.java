package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.AllChats;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.RecentChat;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentChatsRepository {
    private static RecentChatsRepository instance;
    private DatabaseReference databaseReference;
    private PreferenceManager preferenceManager;
    private String currentUserId;

    public static RecentChatsRepository getInstance(){
        if (instance == null)
            instance = new RecentChatsRepository();
        return instance;
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
    }

    static class RecentChatsSort implements Comparator<RecentChat> {
        @Override
        public int compare(RecentChat o1, RecentChat o2) {
            return o1.getMessageSentTimeDate().compareTo(o2.getMessageSentTimeDate());
        }
    }

    public void setChatsListener(List<RecentChat> recentChatsList, OnRecentChatsChangedCallback onRecentChatsChangedCallback){
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).child(Constants.KEY_RECENT_CHATS).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        collectRecentChat(snapshot, recentChatsList);
                        recentChatsList.sort((new RecentChatsSort()).reversed());
                        onRecentChatsChangedCallback.onRecentChatsChangedCallback(recentChatsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("error", "Unable to load recent chat");
                    }
                });
    }

    private List<RecentChat> collectRecentChat(DataSnapshot dataSnapshot, List<RecentChat> recentChatList){
        recentChatList.clear();
        for (DataSnapshot anotherDataSnapshot : dataSnapshot.getChildren()){
            String chatId = anotherDataSnapshot.getKey();
            long lastMessageMillis = anotherDataSnapshot.child(Constants.KEY_TIMESTAMP).getValue(Long.class);
            LocalDateTime localDateTime = Instant.ofEpochMilli(lastMessageMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
            String lastMessage = anotherDataSnapshot.child(Constants.KEY_LAST_MESSAGE).getValue().toString();
            String chatName = anotherDataSnapshot.child(Constants.KEY_CHAT_NAME).getValue().toString();
            Uri chatPhoto = null;
            if (anotherDataSnapshot.hasChild(Constants.KEY_IMAGE))
                chatPhoto = Uri.parse(anotherDataSnapshot.child(Constants.KEY_IMAGE).getValue().toString());
            RecentChat recentChat = new RecentChat(chatId, chatName, lastMessage,
                    dateBeautifulizer(localDateTime), localDateTime, chatPhoto);
            recentChatList.add(recentChat);
        }
        return recentChatList;
    }

    private String dateBeautifulizer(LocalDateTime localDateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return localDateTime.format(dateTimeFormatter);
    }

}
