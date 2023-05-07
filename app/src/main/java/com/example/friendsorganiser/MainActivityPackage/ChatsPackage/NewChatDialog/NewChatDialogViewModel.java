package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class NewChatDialogViewModel extends ViewModel implements OnFriendsLoadedCallback, OnChatCreatedCallback{
    private NewChatDialogRepository newChatDialogRepository;
    private MutableLiveData<List<UserInfo>> friendsList;
    private MutableLiveData<Boolean> isFriendListLoading;
    private MutableLiveData<String> newChatId;
    private List<UserInfo> allFriendsList;

    public void init(){
        newChatDialogRepository = NewChatDialogRepository.getInstance();
        newChatDialogRepository.init();

        allFriendsList = new ArrayList<>();
        friendsList = new MutableLiveData<>();

        isFriendListLoading = new MutableLiveData<>();

        newChatId = new MutableLiveData<>();
    }

    public LiveData<List<UserInfo>> getFriendsList(){
        return friendsList;
    }

    public LiveData<String> getChatId(){
        return newChatId;
    }

    public LiveData<Boolean> isFriendsLoading(){
        return isFriendListLoading;
    }

    public void loadFriends(){
        isFriendListLoading.setValue(true);
        newChatDialogRepository.getFriends(allFriendsList, this);
    }

    public void createNewChat(String chatName){
        newChatDialogRepository.createNewChat(allFriendsList, chatName, this);
    }

    @Override
    public void onFriendsLoadedCallback(List<UserInfo> currentFriendsList) {
        isFriendListLoading.setValue(false);
        friendsList.setValue(currentFriendsList);
    }

    @Override
    public void onChatCreatedCallback(String chatId) {
        newChatId.setValue(chatId);
    }
}
