package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChattingActivityViewModel extends ViewModel implements OnMessageSentCallback, OnChatLoadedCallback{
    private ChattingActivityRepository chattingActivityRepository;
    private MutableLiveData<List<ChatMessage>> messageList;
    private MutableLiveData<String> currentUserId;
    private MutableLiveData<String> chatName;
    private List<ChatMessage> allMessages;

    public void init(String chatId){
        chattingActivityRepository = ChattingActivityRepository.getInstance();
        chattingActivityRepository.init(chatId);
        chattingActivityRepository.loadParticipants(this);

        currentUserId = new MutableLiveData<>(chattingActivityRepository.getCurrentUserId());

        allMessages = new ArrayList<>();

        messageList = new MutableLiveData<>();
        messageList.setValue(allMessages);

        chatName = new MutableLiveData<>();

        chattingActivityRepository.setMessagesListener(allMessages, this);
    }

    public LiveData<String> getCurrentUserId(){
        return currentUserId;
    }

    public LiveData<List<ChatMessage>> getMessageList(){
        return messageList;
    }

    public LiveData<String> getChatName(){
        return chatName;
    }

    public void sendMessage(String message){
        chattingActivityRepository.sendMessage(message);
    }

    @Override
    public void onMessageSentCallback(List<ChatMessage> chatMessageList) {
        messageList.setValue(chatMessageList);
    }

    @Override
    public void onChatLoadedCallback(String currentChatName) {
        chatName.setValue(currentChatName);
    }
}
