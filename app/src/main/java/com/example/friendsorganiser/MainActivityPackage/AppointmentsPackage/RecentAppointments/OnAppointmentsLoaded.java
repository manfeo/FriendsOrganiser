package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.RecentAppointments;

import com.example.friendsorganiser.Models.AppointmentModel;

import java.util.List;

public interface OnAppointmentsLoaded {
    void onAppointmentsLoaded(List<AppointmentModel> allAppointmentModels);
}
