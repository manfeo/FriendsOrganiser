package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.Appointment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.AppointmentModel;
import com.example.friendsorganiser.Models.UserInfo;

import java.util.List;

public class AppointmentViewModel extends ViewModel implements OnAppointmentDataLoadedCallback{
    private AppointmentRepository appointmentRepository;
    private MutableLiveData<AppointmentModel> appointmentModel;
    private MutableLiveData<List<UserInfo>> participants;

    public void init(){
        appointmentRepository = AppointmentRepository.getInstance();
        appointmentRepository.init();

        appointmentModel = new MutableLiveData<>();
        participants = new MutableLiveData<>();
    }

    public LiveData<AppointmentModel> getAppointmentModel(){
        return appointmentModel;
    }

    public LiveData<List<UserInfo>> getParticipants(){
        return participants;
    }

    public void loadAppointmentData(String appointmentId){
        appointmentRepository.getAppointmentData(appointmentId, this);
    }

    @Override
    public void onAppointmentDataLoadedCallback(AppointmentModel loadedData, List<UserInfo> loadedParticipants) {
        appointmentModel.setValue(loadedData);
        participants.setValue(loadedParticipants);
    }
}
