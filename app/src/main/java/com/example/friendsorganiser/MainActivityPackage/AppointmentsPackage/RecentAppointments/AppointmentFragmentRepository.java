package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.RecentAppointments;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.Appointment;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public void loadAppointments(List<Appointment> appointmentList, OnAppointmentsLoaded onAppointmentsLoaded){
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).child(Constants.KEY_RECENT_APPOINTMENTS).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getAppointments(snapshot, appointmentList);
                onAppointmentsLoaded.onAppointmentsLoaded(appointmentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("appointment", "Unable to load appointments");
            }
        });
    }

    private void getAppointments(DataSnapshot snapshot, List<Appointment> appointmentList){
        appointmentList.clear();
        for (DataSnapshot anotherSnapshot : snapshot.getChildren()){
            String appointmentTitle = anotherSnapshot.child(Constants.KEY_APPOINTMENT_TITLE).getValue().toString();
            long appointmentDateMillis = anotherSnapshot.child(Constants.KEY_APPOINTMENT_DATE).getValue(Long.class);
            LocalDateTime localDateTime = Instant.ofEpochMilli(appointmentDateMillis).
                    atZone(ZoneId.systemDefault()).toLocalDateTime();
            String appointmentId = anotherSnapshot.getKey();

            Appointment anotherAppointment = new Appointment(appointmentTitle, dateBeautifulizer(localDateTime),
                    " ", localDateTime, appointmentId);
            appointmentList.add(anotherAppointment);
        }
    }

    private String dateBeautifulizer(LocalDateTime localDateTime){
        String beautifulAppointmentDate = "";
        beautifulAppointmentDate += localDateTime.getDayOfYear() >= 10 ? localDateTime.getDayOfYear() : "0" + localDateTime.getDayOfYear();
        beautifulAppointmentDate += "/";
        beautifulAppointmentDate += localDateTime.getDayOfMonth() >= 10 ? localDateTime.getDayOfMonth() : "0" + localDateTime.getDayOfMonth();
        beautifulAppointmentDate += "/";
        beautifulAppointmentDate += localDateTime.getYear();
        beautifulAppointmentDate += " ";
        beautifulAppointmentDate += localDateTime.getHour() >= 10 ? localDateTime.getHour() : "0" + localDateTime.getHour();
        beautifulAppointmentDate += ":";
        beautifulAppointmentDate += localDateTime.getMinute() >= 10 ? localDateTime.getMinute() : "0" + localDateTime.getMinute();
        return beautifulAppointmentDate;
    }
}
