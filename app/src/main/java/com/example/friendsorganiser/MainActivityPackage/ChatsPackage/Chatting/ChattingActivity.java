package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.friendsorganiser.Models.ChatMessage;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityChatBinding;

import java.util.List;

public class ChattingActivity extends AppCompatActivity {
    private String chatId;
    private ChattingAdapter chattingAdapter;
    private ActivityChatBinding binding;
    private ChattingActivityViewModel chattingActivityViewModel;

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

        setSupportActionBar(binding.toolbarChatToolbar.getRoot());
    }

    private void loadReceivedData(){
        chatId = getIntent().getStringExtra(Constants.KEY_CHAT_ID);
    }

    private void setListeners(){
        binding.ibChatSendMessage.setOnClickListener(v -> sendMessage());
        binding.toolbarChatToolbar.ibBackButton.setOnClickListener(v -> onBackPressed());

        chattingActivityViewModel.getChattingInfo().observe(this, chattingInfo -> {
            binding.toolbarChatToolbar.tvChatName.setText(chattingInfo.getChatTitle());
            Uri chattingPhoto = chattingInfo.getChatPhoto();
            if (chattingPhoto != null)
                Glide.with(binding.getRoot()).load(chattingPhoto).into(binding.toolbarChatToolbar.ivChatImage);
        });
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
}