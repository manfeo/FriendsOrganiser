package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.RecentAppointments;

import com.example.friendsorganiser.Models.Appointment;

import java.util.List;

public interface OnAppointmentsLoaded {
    void onAppointmentsLoaded(List<Appointment> allAppointments);
}