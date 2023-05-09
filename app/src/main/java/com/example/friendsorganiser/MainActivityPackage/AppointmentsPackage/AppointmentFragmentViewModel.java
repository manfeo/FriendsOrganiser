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


    public void init(){
        appointmentFragmentRepository = AppointmentFragmentRepository.getInstance();
        appointmentFragmentRepository.init();

        allAppointments = new ArrayList<>();
        appointmentsList = new MutableLiveData<>();
        appointmentsList.setValue(allAppointments);
    }

    public LiveData<List<Appointment>> getAppointmentsList(){
        return appointmentsList;
    }

    @Override
    public void onAppointmentsLoaded(List<Appointment> allAppointments) {
        appointmentsList.setValue(allAppointments);
    }
}
