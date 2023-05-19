package com.example.friendsorganiser.Models;

import java.io.Serializable;

public class AddressModel implements Serializable {
    private String title, address;
    private double longitude, latitude;

    public AddressModel(String title, String address, double longitude, double latitude) {
        this.title = title;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public AddressModel(String address, double longitude, double latitude){
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
