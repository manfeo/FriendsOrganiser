package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.AllChats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.RecentChat;

import java.util.ArrayList;
import java.util.List;

public class RecentChatsViewModel extends ViewModel implements OnRecentChatsChangedCallback{

    private RecentChatsRepository recentChatsRepository;
    private MutableLiveData<List<RecentChat>> recentChats;
    private List<RecentChat> allChatsList;

    public void init(){
        recentChatsRepository = RecentChatsRepository.getInstance();
        recentChatsRepository.init();

        allChatsList = new ArrayList<>();
        recentChats = new MutableLiveData<>();
        recentChats.setValue(allChatsList);

        recentChatsRepository.setChatsListener(allChatsList, this);
    }

    public LiveData<List<RecentChat>> getRecentChats(){
        return recentChats;
    }

    @Override
    public void onRecentChatsChangedCallback(List<RecentChat> recentChatList) {
        recentChats.setValue(recentChatList);
    }
}
