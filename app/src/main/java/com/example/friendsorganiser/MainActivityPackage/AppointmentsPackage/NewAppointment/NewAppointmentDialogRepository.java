package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAppointmentDialogRepository {
    private static NewAppointmentDialogRepository instance;
    private DatabaseReference databaseReference;
    private PreferenceManager preferenceManager;
    private String currentUserId;
    private String currentUserFullName;

    public static NewAppointmentDialogRepository getInstance(){
        if (instance == null)
            instance = new NewAppointmentDialogRepository();
        return instance;
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        currentUserFullName = preferenceManager.getString(Constants.KEY_NAME) + " " + preferenceManager.getString(Constants.KEY_SURNAME);
    }

    public void getFriends(List<UserInfo> friendsList, OnFriendsLoadedCallback onFriendsLoadedCallback) {
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).child(Constants.KEY_FRIENDS).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadFriends(snapshot, friendsList);
                        onFriendsLoadedCallback.onFriendsLoadedCallback(friendsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("newChat", "Unable to load friends");
                    }
                });
    }

    private void loadFriends(DataSnapshot friendsDataSnapshot, List<UserInfo> friends){
        friends.clear();
        //Looping through list of IDs of current user friends list
        for (DataSnapshot anotherSnapshot : friendsDataSnapshot.getChildren()) {

            String anotherFriendId = anotherSnapshot.getKey();

            String name = anotherSnapshot.child(Constants.KEY_NAME).getValue().toString();
            String surname = anotherSnapshot.child(Constants.KEY_SURNAME).getValue().toString();
            String image = "";
            if (anotherSnapshot.hasChild(Constants.KEY_IMAGE)) {
                image = anotherSnapshot.child(Constants.KEY_IMAGE).getValue().toString();
            }

            UserInfo anotherUser = new UserInfo(name, surname, image, anotherFriendId);
            friends.add(anotherUser);
        }
    }

    public void createNewAppointment(String appointmentTitle, String appointmentAddress, String appointmentDate,
                                     String appointmentTime, double latitude, double longitude,
                                     List<UserInfo> friends, OnAppointmentCreatedCallback onAppointmentCreatedCallback) {
        Map<String, Object> appointmentParticipants = new HashMap<>();
        for (UserInfo friend : friends){
            if (friend.getIsChecked()) {
                String friendFullName = friend.getName() + " " + friend.getSurname();
                appointmentParticipants.put(friend.getId(), friendFullName);
            }
        }
        appointmentParticipants.put(currentUserId, currentUserFullName);

        String fullTime = appointmentDate + " " + appointmentTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(fullTime, formatter);
        long appointmentMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Map<String, Object> dataForParticipants = new HashMap<>();
        Map<String, Object> newAppointment = new HashMap<>();

        String appointmentId = databaseReference.child(Constants.KEY_DATABASE_APPOINTMENTS).push().getKey();

        dataForParticipants.put(Constants.KEY_APPOINTMENT_TITLE, appointmentTitle);
        dataForParticipants.put(Constants.KEY_APPOINTMENT_DATE, appointmentMillis);

        newAppointment.put(Constants.KEY_APPOINTMENT_TITLE, appointmentTitle);
        newAppointment.put(Constants.KEY_APPOINTMENT_DATE, appointmentMillis);
        newAppointment.put(Constants.KEY_APPOINTMENT_ADDRESS, appointmentAddress);
        newAppointment.put(Constants.KEY_APPOINTMENT_PARTICIPANTS, appointmentParticipants);

        Map<String, Object> geoPointMap = new HashMap<>();
        geoPointMap.put(Constants.KEY_APPOINTMENT_LATITUDE, latitude);
        geoPointMap.put(Constants.KEY_APPOINTMENT_LONGITUDE, longitude);
        newAppointment.put(Constants.KEY_APPOINTMENT_GEO_POINT, geoPointMap);

        appointmentParticipants.forEach((currentParticipantId, currentParticipantFullName) -> databaseReference.
                child(Constants.KEY_DATABASE_USERS).child(currentParticipantId).
                child(Constants.KEY_RECENT_APPOINTMENTS).child(appointmentId).setValue(dataForParticipants));

        databaseReference.child(Constants.KEY_DATABASE_APPOINTMENTS).child(appointmentId).setValue(newAppointment).
                addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        Log.d("newAppointment", "Unable to create appointment");
                    onAppointmentCreatedCallback.onAppointmentCreatedCallback(false);
                });
    }
}
