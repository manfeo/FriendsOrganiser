package com.example.friendsorganiser.Models;

public class RegisterModel {

    private String name, surname, dateOfBirth, email, password;
    private boolean isRegisterValid;

    public RegisterModel(String name, String surname, String dateOfBirth, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.password = password;
    }

    public boolean isRegisterValid() {
        return isRegisterValid;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setIsRegisterValid(boolean isRegisterValid){
        this.isRegisterValid = isRegisterValid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
