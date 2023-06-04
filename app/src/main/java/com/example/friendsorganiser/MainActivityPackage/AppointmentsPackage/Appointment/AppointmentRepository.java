package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.Appointment;

import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.App;
import com.example.friendsorganiser.Models.AppointmentModel;
import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {
    private static AppointmentRepository instance;
    private DatabaseReference databaseReference;

    public static AppointmentRepository getInstance(){
        if (instance == null)
            instance = new AppointmentRepository();
        return instance;
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void getAppointmentData(String appointmentId, OnAppointmentDataLoadedCallback onAppointmentDataLoadedCallback){
        databaseReference.child(Constants.KEY_DATABASE_APPOINTMENTS).child(appointmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppointmentModel loadedAppointment = collectAppointmentData(snapshot);
                List<UserInfo> participants = collectParticipants(snapshot);
                onAppointmentDataLoadedCallback.onAppointmentDataLoadedCallback(loadedAppointment, participants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("appointment", "Unable to load appointment data");
            }
        });
    }

    private List<UserInfo> collectParticipants(DataSnapshot snapshot){
        List<UserInfo> participants = new ArrayList<>();
        for (DataSnapshot anotherSnapshot : snapshot.child(Constants.KEY_APPOINTMENT_PARTICIPANTS).getChildren()){
            String fullName = anotherSnapshot.child(Constants.KEY_NAME).getValue().toString();
            Uri friendPhoto = null;
            if (anotherSnapshot.hasChild(Constants.KEY_IMAGE))
                friendPhoto = Uri.parse(anotherSnapshot.child(Constants.KEY_IMAGE).getValue().toString());
            UserInfo anotherParticipant = new UserInfo(fullName, friendPhoto);
            participants.add(anotherParticipant);
        }
        return participants;
    }

    private AppointmentModel collectAppointmentData(DataSnapshot snapshot){
        String appointmentTitle = snapshot.child(Constants.KEY_APPOINTMENT_TITLE).getValue().toString();
        long appointmentDateMillis = snapshot.child(Constants.KEY_APPOINTMENT_DATE).getValue(Long.class);
        LocalDateTime localDateTime = Instant.ofEpochMilli(appointmentDateMillis).
                atZone(ZoneId.systemDefault()).toLocalDateTime();
        String appointmentAddress = snapshot.child(Constants.KEY_APPOINTMENT_ADDRESS).getValue().toString();
        double latitude = snapshot.child(Constants.KEY_APPOINTMENT_GEO_POINT).child(Constants.KEY_APPOINTMENT_LATITUDE).getValue(Double.class);
        double longitude = snapshot.child(Constants.KEY_APPOINTMENT_GEO_POINT).child(Constants.KEY_APPOINTMENT_LONGITUDE).getValue(Double.class);
        return new AppointmentModel(appointmentTitle, dateBeautifulizer(localDateTime), appointmentAddress, latitude, longitude);
    }

    private String dateBeautifulizer(LocalDateTime setDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return setDate.format(dateTimeFormatter);
    }
}

