package com.example.friendsorganiser.Models;

import android.net.Uri;

public class UserProfileInfo {
    private String name, surname, email, dateOfBirth, id;
    private Uri photo;

    public UserProfileInfo(String name, String surname, String email, String dateOfBirth, String id, Uri photo) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.id = id;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Uri getPhoto() {
        return photo;
    }
}
