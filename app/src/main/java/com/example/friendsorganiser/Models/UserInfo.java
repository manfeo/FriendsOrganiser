package com.example.friendsorganiser.Models;

import android.net.Uri;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private String name, surname, token, id, dateOfBirth, fullName;
    private Uri photo;
    //For different choosing activities(e.g. Add new friends)
    private boolean isChecked;

    public UserInfo(String name, String surname, Uri photo, String token, String id) {
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.token = token;
        this.id = id;
    }

    public UserInfo(String name, String surname, String dateOfBirth, String id) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.id = id;
    }

    public UserInfo(String name, String surname, Uri photo, String id){
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.id = id;
    }

    public UserInfo(String name, String surname, String id){
        this.name = name;
        this.surname = surname;
        this.id = id;
    }

    public UserInfo(String fullName, Uri photo){
        this.fullName = fullName;
        this.photo = photo;
    }
    public UserInfo(String fullName){
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Uri getPhoto() {
        return photo;
    }

    public String getToken() {
        return token;
    }

    public String getId() {
        return id;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean getIsChecked(){
        return isChecked;
    }

    public String getFullName() {
        return fullName;
    }
    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }
}
