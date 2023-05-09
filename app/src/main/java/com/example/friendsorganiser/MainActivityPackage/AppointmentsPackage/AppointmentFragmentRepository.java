package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage;

import com.example.friendsorganiser.Models.Appointment;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AppointmentFragmentRepository {

    private static AppointmentFragmentRepository instance;
    private PreferenceManager preferenceManager;
    private DatabaseReference databaseReference;
    private String currentUserId;

    public static AppointmentFragmentRepository getInstance(){
        if (instance == null)
            instance = new AppointmentFragmentRepository();
        return instance;
    }

    public void init(){
        preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadAppointments(List<Appointment> appointmentList){

    }
}
