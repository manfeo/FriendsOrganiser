package com.example.friendsorganiser.MainActivityPackage.ChatsPackage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.friendsorganiser.Models.ChatMessage;
import com.example.friendsorganiser.Models.RecentChat;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.FragmentChatBinding;
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
import java.util.List;

public class RecentChats extends Fragment implements OnChatClickedListener{
    private FragmentChatBinding binding;
    private RecentChatsAdapter recentChatsAdapter;
    private DatabaseReference databaseReference;
    private String currentUserId;
    private List<RecentChat> recentChatsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setListeners();
    }

    private void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        PreferenceManager preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        recentChatsList = new ArrayList<>();
        recentChatsAdapter = new RecentChatsAdapter(recentChatsList, this);
        binding.rvAllChatsDisplay.setAdapter(recentChatsAdapter);
    }

    private void setListeners() {
        binding.fbAddNewChat.setOnClickListener(v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            NewChat newChat = new NewChat();
            newChat.show(manager, "newChatCreation");
        });
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).child(Constants.KEY_RECENT_CHATS).
                addValueEventListener(valueEventListener);
    }

    class RecentChatsSort implements Comparator<RecentChat> {
        @Override
        public int compare(RecentChat o1, RecentChat o2) {
            return o1.getMessageSentTimeDate().isBefore(o2.getMessageSentTimeDate()) ? 1 : 0;
        }
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int count = recentChatsList.size();
            recentChatsList.clear();
            for (DataSnapshot recentChatSnapshot : snapshot.getChildren()){
                RecentChat anotherRecentChat = collectRecentChat(recentChatSnapshot);
                recentChatsList.add(anotherRecentChat);
            }
            if (recentChatsList.size() == 0){
                binding.tvEmptyChatsList.setVisibility(View.VISIBLE);
            } else {
                recentChatsList.sort(new RecentChatsSort());
                recentChatsAdapter.notifyDataSetChanged();
                binding.rvAllChatsDisplay.post(() -> binding.rvAllChatsDisplay.smoothScrollToPosition(0));
                binding.rvAllChatsDisplay.setVisibility(View.VISIBLE);
            }
            binding.pbLoadingAllChats.setVisibility(View.GONE);
            binding.fbAddNewChat.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getContext(), "Unable to load recent chats", Toast.LENGTH_SHORT).show();
        }
    };

    private RecentChat collectRecentChat(DataSnapshot dataSnapshot){
        String chatId = dataSnapshot.getKey();
        long lastMessageMillis = dataSnapshot.child(Constants.KEY_TIMESTAMP).getValue(Long.class);
        LocalDateTime localDateTime = Instant.ofEpochMilli(lastMessageMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        String lastMessage = dataSnapshot.child(Constants.KEY_LAST_MESSAGE).getValue().toString();
        String chatName = dataSnapshot.child(Constants.KEY_CHAT_NAME).getValue().toString();
        RecentChat recentChat = new RecentChat(chatId, chatName, lastMessage, dateBeautifulizer(localDateTime), localDateTime);
        return recentChat;
    }

    private String dateBeautifulizer(LocalDateTime localDateTime){
        String beautifulMessageSentTime = "";
        beautifulMessageSentTime += localDateTime.getHour() >= 10 ? localDateTime.getHour() : "0" + localDateTime.getHour();
        beautifulMessageSentTime += ":";
        beautifulMessageSentTime += localDateTime.getMinute() >= 10 ? localDateTime.getMinute() : "0" + localDateTime.getMinute();
        return beautifulMessageSentTime;
    }

    @Override
    public void onChatCLicked(String recentChatId) {
        Intent openChatIntent = new Intent(getActivity(), ChattingActivity.class);
        openChatIntent.putExtra(Constants.KEY_CHAT_ID, recentChatId);
        startActivity(openChatIntent);
    }
}