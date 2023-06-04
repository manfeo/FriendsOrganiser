package com.example.friendsorganiser.Models;

import android.net.Uri;

import java.time.LocalDateTime;

public class AppointmentModel {
    private String appointmentTitle, appointmentDate, appointmentId, appointmentAddress;
    private Uri appointmentPhoto;
    private LocalDateTime appointmentDateObject;
    private double latitude, longitude;

    public AppointmentModel(String appointmentTitle, String appointmentDate, LocalDateTime appointmentDateObject,
                            String appointmentId, Uri appointmentPhoto) {
        this.appointmentTitle = appointmentTitle;
        this.appointmentDate = appointmentDate;
        this.appointmentPhoto = appointmentPhoto;
        this.appointmentDateObject = appointmentDateObject;
        this.appointmentId = appointmentId;
    }



    public AppointmentModel(String appointmentTitle, String appointmentDate, String appointmentAddress,
                            double latitude, double longitude){
        this.appointmentTitle = appointmentTitle;
        this.appointmentDate = appointmentDate;
        this.appointmentAddress = appointmentAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public Uri getAppointmentPhoto() {
        return appointmentPhoto;
    }

    public LocalDateTime getAppointmentDateObject() {
        return appointmentDateObject;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getAppointmentAddress() {
        return appointmentAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
