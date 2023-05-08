package com.example.friendsorganiser.Models;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private String name, surname, image, token, id, dateOfBirth;

    //For different choosing activities(e.g. Add new friends)
    private boolean isChecked;

    public UserInfo(String name, String surname, String image, String token, String id) {
        this.name = name;
        this.surname = surname;
        this.image = image;
        this.token = token;
        this.id = id;
    }

    public UserInfo(String name, String surname, String dateOfBirth, String id) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getImage() {
        return image;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateOfBirth(String dateOfBirth) {this.dateOfBirth = dateOfBirth;}

    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }
    public boolean getIsChecked(){
        return isChecked;
    }
}
