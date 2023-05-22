package com.example.friendsorganiser.Models;

import java.time.LocalDateTime;

public class AppointmentModel {
    private String appointmentTitle, appointmentDate, appointmentPhoto, appointmentId, appointmentAddress;
    private LocalDateTime appointmentDateObject;
    private double latitude, longitude;

    public AppointmentModel(String appointmentTitle, String appointmentDate,
                            String appointmentPhoto, LocalDateTime appointmentDateObject, String appointmentId) {
        this.appointmentTitle = appointmentTitle;
        this.appointmentDate = appointmentDate;
        this.appointmentPhoto = appointmentPhoto;
        this.appointmentDateObject = appointmentDateObject;
        this.appointmentId = appointmentId;
    }

    public AppointmentModel(String appointmentTitle, String appointmentDate, String appointmentPhoto,
                            String appointmentAddress, double latitude, double longitude){
        this.appointmentTitle = appointmentTitle;
        this.appointmentDate = appointmentDate;
        this.appointmentPhoto = appointmentPhoto;
        this.appointmentAddress = appointmentAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setAppointmentTitle(String appointmentTitle) {
        this.appointmentTitle = appointmentTitle;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentPhoto(String appointmentPhoto) {
        this.appointmentPhoto = appointmentPhoto;
    }

    public void setAppointmentDateObject(LocalDateTime appointmentDateObject) {
        this.appointmentDateObject = appointmentDateObject;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentPhoto() {
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
