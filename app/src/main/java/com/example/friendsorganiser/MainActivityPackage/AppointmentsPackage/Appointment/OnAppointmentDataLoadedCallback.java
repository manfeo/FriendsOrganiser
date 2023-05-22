package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.Appointment;

import com.example.friendsorganiser.Models.AppointmentModel;
import com.example.friendsorganiser.Models.UserInfo;

import java.util.List;

public interface OnAppointmentDataLoadedCallback {
    void onAppointmentDataLoadedCallback(AppointmentModel loadedData, List<UserInfo> loadedParticipants);
}
