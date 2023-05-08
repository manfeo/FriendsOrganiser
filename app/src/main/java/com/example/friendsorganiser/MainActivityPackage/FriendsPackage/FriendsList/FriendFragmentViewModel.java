package com.example.friendsorganiser.MainActivityPackage.FriendsPackage.FriendsList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class FriendFragmentViewModel extends ViewModel implements OnFriendsLoadedCallback{
    private FriendFragmentRepository friendFragmentRepository;
    private MutableLiveData<List<UserInfo>> friendsList;
    private List<UserInfo> allFriends;
    private MutableLiveData<Boolean> isFriendsListLoading;

    public void init(){
        friendFragmentRepository = FriendFragmentRepository.getInstance();
        friendFragmentRepository.init();

        allFriends = new ArrayList<>();
        friendsList = new MutableLiveData<>();
        friendsList.setValue(allFriends);

        isFriendsListLoading = new MutableLiveData<>();
    }

    public LiveData<List<UserInfo>> getFriendsList(){
        return friendsList;
    }

    public LiveData<Boolean> isFriendsLoading(){
        return isFriendsListLoading;
    }

    public void loadFriends(){
        isFriendsListLoading.setValue(true);
        friendFragmentRepository.loadFriends(allFriends, this);
    }

    @Override
    public void onFriendsLoadedCallback(List<UserInfo> currentUserFriends) {
        isFriendsListLoading.setValue(false);
        friendsList.setValue(currentUserFriends);
    }
}
