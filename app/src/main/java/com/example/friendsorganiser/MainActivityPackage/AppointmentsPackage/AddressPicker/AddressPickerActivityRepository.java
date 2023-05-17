package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker;

import android.location.Address;
import com.example.friendsorganiser.Utilities.Constants;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddressPickerActivityRepository {
    private static AddressPickerActivityRepository instance;

    public static AddressPickerActivityRepository getInstance() {
        if (instance == null)
            instance = new AddressPickerActivityRepository();
        return instance;
    }

    public void getAddressOfGeoPoint(GeoPoint geoPoint, OnAddressRetrievedCallback onAddressRetrievedCallback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            GeocoderNominatim geocoder = new GeocoderNominatim(System.getProperty("http.agent"));
            String fullAddress;
            try {
                List<Address> addresses = geocoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String selectedAddress = address.getExtras().getString(Constants.KEY_FULL_ADDRESS);
                    String[] rawAddress = selectedAddress.split(",");
                    String correctAddressName = rawAddress[2].replaceAll("[a-zA-Z]", "").trim();
                    fullAddress = correctAddressName + " " + rawAddress[1] +  " " + rawAddress[0];
                } else {
                    fullAddress = null;
                }
            } catch (IOException e) {
                fullAddress = null;
            }
            onAddressRetrievedCallback.onAddressRetrievedCallback(fullAddress);
        });
    }
}
