package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker;

import android.location.Address;

import com.example.friendsorganiser.Models.AddressModel;
import com.example.friendsorganiser.Utilities.Constants;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddressPickerActivityRepository {
    private static AddressPickerActivityRepository instance;
    private String userAgent;

    public static AddressPickerActivityRepository getInstance() {
        if (instance == null)
            instance = new AddressPickerActivityRepository();
        return instance;
    }

    public void init(){
        userAgent = System.getProperty("http.agent");
    }

    public void getAddressOfGeoPoint(GeoPoint geoPoint, OnAddressRetrievedCallback onAddressRetrievedCallback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            GeocoderNominatim geocoder = new GeocoderNominatim(userAgent);
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

    public void getGeoPointsFromQuery(String query, GeoPoint startPoint, OnPOILoadedCallback onPOILoadedCallback, OnAddressesLoadedCallback onAddressesLoadedCallback){
        if (!SpecialPhrasesProvider.isPOI(query)) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                GeocoderNominatim geocoder = new GeocoderNominatim(userAgent);
                List<AddressModel> foundedAddresses = new ArrayList<>();
                try {
                    List<Address> foundedPlaces = geocoder.getFromLocationName(query, 20,
                            (startPoint.getLatitude() - 0.2), startPoint.getLongitude() - 0.2,
                            startPoint.getLatitude() + 0.2, startPoint.getLongitude() + 0.2);
                    if (foundedPlaces.size() > 0) {
                        for (Address anotherAddress : foundedPlaces){
                            String[] rawAddress = anotherAddress.getExtras().getString(Constants.KEY_FULL_ADDRESS).split(",");
                            String title = rawAddress[0];
                            String astest = "asdfklfjkd".replaceAll("[a-zA-Z]", "");
                            String fullAddress = (rawAddress[2].replaceAll("[a-zA-Z]", "") + " " + rawAddress[1]).trim();
                            AddressModel addressModel = new AddressModel(title, fullAddress,
                                    anotherAddress.getLongitude(), anotherAddress.getLatitude());
                            foundedAddresses.add(addressModel);
                        }

                    } else {
                        foundedAddresses = null;
                    }
                } catch (IOException e) {
                    foundedAddresses = null;
                }
                onAddressesLoadedCallback.onAddressesLoadedCallback(foundedAddresses);
            });
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                ArrayList<POI> foundedPlaces;
                try {
                    NominatimPOIProvider nominatimPOIProvider = new NominatimPOIProvider(userAgent);
                    foundedPlaces = nominatimPOIProvider.getPOICloseTo(startPoint, query, 50, 0.1);
                } catch (Exception e) {
                    foundedPlaces = null;
                }
                onPOILoadedCallback.onPOILoadedCallback(foundedPlaces);
            });
        }
    }
}
