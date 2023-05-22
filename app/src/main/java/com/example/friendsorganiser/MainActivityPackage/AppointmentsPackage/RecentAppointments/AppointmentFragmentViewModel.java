package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.RecentAppointments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.AppointmentModel;

import java.util.ArrayList;
import java.util.List;

public class AppointmentFragmentViewModel extends ViewModel implements OnAppointmentsLoaded{

    private AppointmentFragmentRepository appointmentFragmentRepository;
    private MutableLiveData<List<AppointmentModel>> appointmentsList;
    private List<AppointmentModel> allAppointmentModels;
    private MutableLiveData<Boolean> appointmentListIsLoading;


    public void init(){
        appointmentFragmentRepository = AppointmentFragmentRepository.getInstance();
        appointmentFragmentRepository.init();

        allAppointmentModels = new ArrayList<>();
        appointmentsList = new MutableLiveData<>();
        appointmentsList.setValue(allAppointmentModels);

        appointmentListIsLoading = new MutableLiveData<>();

        loadAppointments();
    }

    private void loadAppointments(){
        appointmentListIsLoading.setValue(true);
        appointmentFragmentRepository.loadAppointments(allAppointmentModels, this);
    }

    public LiveData<List<AppointmentModel>> getAppointmentsList(){
        return appointmentsList;
    }

    public LiveData<Boolean> isAppointmentListLoading(){
        return appointmentListIsLoading;
    }

    @Override
    public void onAppointmentsLoaded(List<AppointmentModel> allAppointmentModels) {
        appointmentListIsLoading.setValue(false);
        appointmentsList.setValue(allAppointmentModels);
    }
}
