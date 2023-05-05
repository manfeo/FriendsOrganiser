package com.example.friendsorganiser.Models;

public class LoginModel {

    private String email, password;
    private boolean rememberMe, isLoginValid;

    public LoginModel(String email, String password, boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }


    public boolean isRememberMe() {
        return rememberMe;
    }

    public boolean isLoginValid() {
        return isLoginValid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public void setLoginValid(boolean loginValid) {
        isLoginValid = loginValid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
