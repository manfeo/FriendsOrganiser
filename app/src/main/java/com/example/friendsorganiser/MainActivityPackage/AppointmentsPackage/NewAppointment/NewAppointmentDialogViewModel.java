package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class NewAppointmentDialogViewModel extends ViewModel implements OnFriendsLoadedCallback, OnAppointmentCreatedCallback{

    private NewAppointmentDialogRepository newAppointmentDialogRepository;
    private MutableLiveData<List<UserInfo>> friendsList;
    private MutableLiveData<Boolean> friendsListLoading;
    private MutableLiveData<Boolean> newAppointmentLoading;
    private List<UserInfo> allFriends;

    public void init(){
        newAppointmentDialogRepository = NewAppointmentDialogRepository.getInstance();
        newAppointmentDialogRepository.init();

        friendsList = new MutableLiveData<>();
        allFriends = new ArrayList<>();
        friendsList.setValue(allFriends);

        friendsListLoading = new MutableLiveData<>();
        newAppointmentLoading = new MutableLiveData<>();

        loadFriends();
    }

    public LiveData<List<UserInfo>> getFriends(){
        return friendsList;
    }


    public void loadFriends(){
        friendsListLoading.setValue(true);
        newAppointmentDialogRepository.getFriends(allFriends, this);
    }

    public LiveData<Boolean> isFriendsListLoading(){
        return friendsListLoading;
    }

    public LiveData<Boolean> isNewAppointmentLoading(){
        return newAppointmentLoading;
    }

    public void createNewAppointment(String appointmentTitle, String appointmentAddress, String appointmentDate,
                                     String appointmentTime, double latitude, double longitude){
        newAppointmentDialogRepository.createNewAppointment(appointmentTitle, appointmentAddress, appointmentDate,
                appointmentTime, latitude, longitude, allFriends, this);
    }

    @Override
    public void onFriendsLoadedCallback(List<UserInfo> listOfFriends) {
        friendsListLoading.setValue(false);
        friendsList.setValue(listOfFriends);
    }

    @Override
    public void onAppointmentCreatedCallback(boolean isLoading) {
        newAppointmentLoading.setValue(isLoading);
    }
}
