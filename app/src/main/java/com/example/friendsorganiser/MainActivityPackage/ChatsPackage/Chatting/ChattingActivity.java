package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.friendsorganiser.Models.ChatMessage;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.ActivityChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ChattingActivity extends AppCompatActivity {

    private String chatId;
    private List<ChatMessage> messages;
    private List<String> participants;
    private ChattingAdapter chattingAdapter;
    private PreferenceManager preferenceManager;
    private DatabaseReference databaseReference;
    private String chatName;
    private ActivityChatBinding binding;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initData();
        loadReceivedData();
        setListeners();
    }

    private void initData(){
        preferenceManager = PreferenceManager.getInstance();
        messages = new ArrayList<>();
        participants = new ArrayList<>();
        chattingAdapter = new ChattingAdapter(messages, preferenceManager.getString(Constants.KEY_USER_ID));
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar_chatToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.rvChatDisplay.setAdapter(chattingAdapter);
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        String senderFullName = preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_SURNAME);
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_MESSAGE, binding.etChatInputText.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, System.currentTimeMillis());
        message.put(Constants.KEY_SENDER_FULL_NAME, senderFullName);
        databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).child(Constants.KEY_MESSAGES).push().setValue(message).
                addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(this, "Message is sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to send message", Toast.LENGTH_SHORT).show();
            }
        });
        binding.etChatInputText.setText(null);
    }

    private void loadReceivedData(){
        chatId = getIntent().getStringExtra(Constants.KEY_CHAT_ID);
        databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatName = snapshot.child(Constants.KEY_CHAT_NAME).getValue().toString();
                binding.toolbarChatToolbar.tvPageDefiner.setText(chatName);

                for (DataSnapshot dataSnapshot : snapshot.child(Constants.KEY_CHAT_PARTICIPANTS).getChildren()){
                    participants.add(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChattingActivity.this, "Unable to load chat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ChatMessagesSort implements Comparator<ChatMessage> {
        @Override
        public int compare(ChatMessage o1, ChatMessage o2) {
            return o1.getCreatedAtDate().isBefore(o2.getCreatedAtDate()) ? 1 : 0;
        }
    }

    private final ValueEventListener forNewMessagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int count = messages.size();
            messages.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                ChatMessage anotherMessage = collectNewMessage(dataSnapshot);
                if (!messages.contains(anotherMessage))
                    messages.add(anotherMessage);
            }
            messages.sort(new ChatMessagesSort());
            if (messages.size() > 0)
                updateLastMessage(messages.get(messages.size() - 1));
            if (count == 0)
                chattingAdapter.notifyDataSetChanged();
            else {
                chattingAdapter.notifyItemInserted(messages.size() - 1);
                binding.rvChatDisplay.post(() -> binding.rvChatDisplay.smoothScrollToPosition(messages.size() - 1));
            }
            binding.rvChatDisplay.setVisibility(View.VISIBLE);
            binding.pbLoadingChatMessages.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(ChattingActivity.this, "Can't load new messages", Toast.LENGTH_SHORT).show();
        }
    };

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
                child(currentId).child(Constants.KEY_RECENT_CHATS).child(chatId).setValue(lastMessageInfo));
    }

    private void setListeners(){
        binding.ibChatSendMessage.setOnClickListener(v -> sendMessage());
        toolbar.setOnClickListener(v -> onBackPressed());
        databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).child(Constants.KEY_MESSAGES).addValueEventListener(forNewMessagesListener);
    }

    private String dateBeautifulizer(LocalDateTime localDateTime){
        String beautifulMessageSentTime = "";
        beautifulMessageSentTime += localDateTime.getHour() >= 10 ? localDateTime.getHour() : "0" + localDateTime.getHour();
        beautifulMessageSentTime += ":";
        beautifulMessageSentTime += localDateTime.getMinute() >= 10 ? localDateTime.getMinute() : "0" + localDateTime.getMinute();
        return beautifulMessageSentTime;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}