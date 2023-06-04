package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAppointmentDialogRepository {
    private static NewAppointmentDialogRepository instance;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
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
        storageReference = FirebaseStorage.getInstance().getReference();
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
            Uri friendImage = null;
            if (anotherSnapshot.hasChild(Constants.KEY_IMAGE)) {
                friendImage = Uri.parse(anotherSnapshot.child(Constants.KEY_IMAGE).getValue().toString());
            }

            UserInfo anotherUser = new UserInfo(name, surname, friendImage, anotherFriendId);
            friends.add(anotherUser);
        }
    }

    public void createNewAppointment(String appointmentTitle, String appointmentAddress, String appointmentDate,
                                     String appointmentTime, double latitude, double longitude,
                                     List<UserInfo> friends, Uri appointmentPhoto, OnAppointmentCreatedCallback onAppointmentCreatedCallback) {
        String appointmentId = databaseReference.child(Constants.KEY_DATABASE_APPOINTMENTS).push().getKey();
        Map<String, Object> appointmentParticipants = new HashMap<>();
        for (UserInfo friend : friends){
            if (friend.getIsChecked()) {
                String friendFullName = friend.getName() + " " + friend.getSurname();
                Map<String, Object> friendMap = new HashMap<>();
                friendMap.put(Constants.KEY_NAME, friendFullName);
                Uri friendPhoto = friend.getPhoto();
                if (friendPhoto != null)
                    friendMap.put(Constants.KEY_IMAGE, friendPhoto.toString());
                appointmentParticipants.put(friend.getId(), friendMap);
            }
        }

        //Adding current user to participants
        Map<String, Object> me = new HashMap<>();
        String myName = preferenceManager.getString(Constants.KEY_NAME);
        String mySurname = preferenceManager.getString(Constants.KEY_SURNAME);
        String myFullName = myName + " " + mySurname;
        me.put(Constants.KEY_NAME, myFullName);
        if (preferenceManager.contains(Constants.KEY_IMAGE)) {
            String myImage = preferenceManager.getString(Constants.KEY_IMAGE);
            me.put(Constants.KEY_IMAGE, myImage);
        }

        appointmentParticipants.put(currentUserId, me);

        String fullTime = appointmentDate + " " + appointmentTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(fullTime, formatter);
        long appointmentMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Map<String, Object> dataForParticipants = new HashMap<>();
        Map<String, Object> newAppointment = new HashMap<>();

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
        if (appointmentPhoto != null){
            StorageReference ref = storageReference.child(Constants.KEY_FIRESTORE_APPOINTMENTS_IMAGES).child(appointmentId);
            UploadTask uploadTask = ref.putFile(appointmentPhoto);
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful())
                    Log.d("image", "Unable to load profile image");
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadedChatPhoto = task.getResult();
                    String chatString = downloadedChatPhoto.toString();

                    newAppointment.put(Constants.KEY_IMAGE, chatString);
                    dataForParticipants.put(Constants.KEY_IMAGE, chatString);

                    appointmentParticipants.forEach((currentParticipantId, currentParticipantFullName) -> databaseReference.
                            child(Constants.KEY_DATABASE_USERS).child(currentParticipantId).
                            child(Constants.KEY_RECENT_APPOINTMENTS).child(appointmentId).setValue(dataForParticipants));

                    databaseReference.child(Constants.KEY_DATABASE_APPOINTMENTS).child(appointmentId).setValue(newAppointment).
                            addOnCompleteListener(newTask -> {
                                if (!newTask.isSuccessful())
                                    Log.d("newAppointment", "Unable to create appointment");
                                onAppointmentCreatedCallback.onAppointmentCreatedCallback(false);
                            });
                }
            });
        } else {
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
}
