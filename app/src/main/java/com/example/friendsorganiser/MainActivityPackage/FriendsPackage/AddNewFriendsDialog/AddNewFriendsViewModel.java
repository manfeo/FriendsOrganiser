package com.example.friendsorganiser.MainActivityPackage.FriendsPackage.AddNewFriendsDialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class AddNewFriendsViewModel extends ViewModel implements OnUsersLoadedCallback, OnSentRequestsCallback {

    private AddNewFriendsRepository addNewFriendsRepository;
    private MutableLiveData<List<UserInfo>> usersList;
    private MutableLiveData<Boolean> isUsersListLoading;
    private MutableLiveData<Boolean> isSendingRequests;
    private List<UserInfo> allUsers;


    public void init(){
        addNewFriendsRepository = AddNewFriendsRepository.getInstance();
        addNewFriendsRepository.init();

        allUsers = new ArrayList<>();
        usersList = new MutableLiveData<>();
        usersList.setValue(allUsers);

        isUsersListLoading = new MutableLiveData<>();
        isSendingRequests = new MutableLiveData<>();
    }

    public void loadUsers(){
        isUsersListLoading.setValue(true);
        addNewFriendsRepository.getUsers(allUsers, this);
    }

    public void sendRequests(){
        isSendingRequests.setValue(true);
        addNewFriendsRepository.sendRequests(allUsers);
    }

    public LiveData<List<UserInfo>> getUsersList(){
        return usersList;
    }

    public LiveData<Boolean> isUsersLoading(){
        return isUsersListLoading;
    }

    public LiveData<Boolean> isSentRequests(){
        return isSendingRequests;
    }

    @Override
    public void onUsersLoadedCallback(List<UserInfo> allUsersList) {
        isUsersListLoading.setValue(false);
        usersList.setValue(allUsersList);
    }

    @Override
    public void onSentRequestsCallback(boolean isRequestsLoading) {
        isSendingRequests.setValue(false);
    }
}
