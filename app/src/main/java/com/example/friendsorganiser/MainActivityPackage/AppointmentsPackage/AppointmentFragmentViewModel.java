package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentFragmentViewModel extends ViewModel implements OnAppointmentsLoaded{

    private AppointmentFragmentRepository appointmentFragmentRepository;
    private MutableLiveData<List<Appointment>> appointmentsList;
    private List<Appointment> allAppointments;
    private MutableLiveData<Boolean> appointmentListIsLoading;


    public void init(){
        appointmentFragmentRepository = AppointmentFragmentRepository.getInstance();
        appointmentFragmentRepository.init();

        allAppointments = new ArrayList<>();
        appointmentsList = new MutableLiveData<>();
        appointmentsList.setValue(allAppointments);

        appointmentListIsLoading = new MutableLiveData<>();

        loadAppointments();
    }

    private void loadAppointments(){
        appointmentListIsLoading.setValue(true);
        appointmentFragmentRepository.loadAppointments(allAppointments, this);
    }

    public LiveData<List<Appointment>> getAppointmentsList(){
        return appointmentsList;
    }

    public LiveData<Boolean> isAppointmentListLoading(){
        return appointmentListIsLoading;
    }

    @Override
    public void onAppointmentsLoaded(List<Appointment> allAppointments) {
        appointmentListIsLoading.setValue(false);
        appointmentsList.setValue(allAppointments);
    }
}
