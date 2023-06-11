package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.ChatMessage;
import com.example.friendsorganiser.Models.ChattingInfo;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ChattingActivityRepository {

    private static ChattingActivityRepository instance;
    private PreferenceManager preferenceManager;
    private String currentUserId;
    private String chatId;
    private String chatName;
    private DatabaseReference databaseReference;
    private List<String> participants;

    public static ChattingActivityRepository getInstance(){
        if (instance == null)
            instance = new ChattingActivityRepository();
        return instance;
    }

    public void init(String chatId){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        participants = new ArrayList<>();

        this.chatId = chatId;
    }

    public String getCurrentUserId(){
        return currentUserId;
    }

    public void loadParticipants(OnChatLoadedCallback onChatLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatName = snapshot.child(Constants.KEY_CHAT_NAME).getValue().toString();
                        Uri chatPhoto = null;
                        if (snapshot.hasChild(Constants.KEY_IMAGE))
                            chatPhoto = Uri.parse(snapshot.child(Constants.KEY_IMAGE).getValue().toString());

                        for (DataSnapshot dataSnapshot : snapshot.child(Constants.KEY_CHAT_PARTICIPANTS).getChildren()){
                            participants.add(dataSnapshot.getValue().toString());
                        }

                        ChattingInfo chattingInfo = new ChattingInfo(chatName, chatPhoto);
                        onChatLoadedCallback.onChatLoadedCallback(chattingInfo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("chatting", "Unable to load chat data");
                    }
                });
    }

    static class ChatMessagesSort implements Comparator<ChatMessage> {
        @Override
        public int compare(ChatMessage o1, ChatMessage o2) {
            return o1.getCreatedAtDate().isBefore(o2.getCreatedAtDate()) ? 1 : 0;
        }
    }

    public void setMessagesListener(List<ChatMessage> messages, OnMessageSentCallback onMessageSentCallback){
        databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).
                child(Constants.KEY_MESSAGES).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatMessage anotherMessage = collectNewMessage(dataSnapshot);
                            if (!messages.contains(anotherMessage))
                                messages.add(anotherMessage);
                        }
                        messages.sort(new ChatMessagesSort());
                        if (messages.size() > 0)
                            updateLastMessage(messages.get(messages.size() - 1));
                        onMessageSentCallback.onMessageSentCallback(messages);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("chatting", "Can't load new messages");
                    }
                });
    }

    public void sendMessage(String sentMessage){
        HashMap<String, Object> message = new HashMap<>();
        String senderFullName = preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_SURNAME);
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_MESSAGE, sentMessage);
        message.put(Constants.KEY_TIMESTAMP, System.currentTimeMillis());
        message.put(Constants.KEY_SENDER_FULL_NAME, senderFullName);
        databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).child(Constants.KEY_MESSAGES).push().setValue(message).
                addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        Log.d("chatting", "Unable to send message");
                });
    }

    private ChatMessage collectNewMessage(DataSnapshot dataSnapshot){
        String senderId = dataSnapshot.child(Constants.KEY_SENDER_ID).getValue().toString();
        String message = dataSnapshot.child(Constants.KEY_MESSAGE).getValue().toString();
        Long messageSentMillis = dataSnapshot.child(Constants.KEY_TIMESTAMP).getValue(Long.class);
        LocalDateTime localDateTime = Instant.ofEpochMilli(messageSentMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        String fullName = dataSnapshot.child(Constants.KEY_SENDER_FULL_NAME).getValue().toString();
        ChatMessage chatMessage = new ChatMessage(senderId, message,
                dateBeautifulizer(localDateTime), localDateTime, fullName);
        return chatMessage;
    }

    private void updateLastMessage(ChatMessage lastMessage){
        HashMap<String, Object> lastMessageInfo = new HashMap<>();
        long millisOfLastMessage = lastMessage.getCreatedAtDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        lastMessageInfo.put(Constants.KEY_LAST_MESSAGE, lastMessage.getMessage());
        lastMessageInfo.put(Constants.KEY_TIMESTAMP, millisOfLastMessage);
        lastMessageInfo.put(Constants.KEY_CHAT_NAME, chatName);

        //Setting last messageTime for each participant to sort all chats in chats fragment
        participants.forEach((currentId) -> databaseReference.child(Constants.KEY_DATABASE_USERS).
                child(currentId).child(Constants.KEY_RECENT_CHATS).child(chatId).updateChildren(lastMessageInfo));
    }

    private String dateBeautifulizer(LocalDateTime localDateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return localDateTime.format(dateTimeFormatter);
    }
}
