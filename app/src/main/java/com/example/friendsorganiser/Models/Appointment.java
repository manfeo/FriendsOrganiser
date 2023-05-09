package com.example.friendsorganiser.Models;

import java.time.LocalDateTime;

public class Appointment {
    private String appointmentTitle, appointmentDate, appointmentPhoto, appointmentId;
    private LocalDateTime appointmentDateObject;

    public Appointment(String appointmentTitle, String appointmentDate, String appointmentPhoto, LocalDateTime appointmentDateObject) {
        this.appointmentTitle = appointmentTitle;
        this.appointmentDate = appointmentDate;
        this.appointmentPhoto = appointmentPhoto;
        this.appointmentDateObject = appointmentDateObject;
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
}
