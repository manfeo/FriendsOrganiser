package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


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
    private ChattingAdapter chattingAdapter;
    private ActivityChatBinding binding;
    private ChattingActivityViewModel chattingActivityViewModel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadReceivedData();

        chattingActivityViewModel = new ViewModelProvider(this).get(ChattingActivityViewModel.class);
        chattingActivityViewModel.init(chatId);

        initData();
        setListeners();
    }

    private void initData(){
        chattingAdapter = new ChattingAdapter(chattingActivityViewModel.getMessageList().getValue(),
                chattingActivityViewModel.getCurrentUserId().getValue());
        binding.rvChatDisplay.setAdapter(chattingAdapter);

        toolbar = findViewById(R.id.toolbar_chatToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadReceivedData(){
        chatId = getIntent().getStringExtra(Constants.KEY_CHAT_ID);
    }

    private void setListeners(){
        binding.ibChatSendMessage.setOnClickListener(v -> sendMessage());
        toolbar.setOnClickListener(v -> onBackPressed());

        chattingActivityViewModel.getChatName().observe(this,
                chatName -> binding.toolbarChatToolbar.tvPageDefiner.setText(chatName));
        chattingActivityViewModel.getMessageList().observe(this, this::handleNewMessage);
    }

    private void handleNewMessage(List<ChatMessage> newMessages){
        if (newMessages.size() == 0)
            chattingAdapter.notifyDataSetChanged();
        else {
            chattingAdapter.notifyItemInserted(newMessages.size() - 1);
            binding.rvChatDisplay.post(() -> binding.rvChatDisplay.smoothScrollToPosition(newMessages.size() - 1));
        }
        binding.rvChatDisplay.setVisibility(View.VISIBLE);
        binding.pbLoadingChatMessages.setVisibility(View.GONE);
    }

    private void sendMessage(){
        chattingActivityViewModel.sendMessage(binding.etChatInputText.getText().toString());
        binding.etChatInputText.setText(null);
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