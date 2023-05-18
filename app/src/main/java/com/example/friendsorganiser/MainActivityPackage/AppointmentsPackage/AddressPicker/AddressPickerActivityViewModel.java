package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class AddressPickerActivityViewModel extends ViewModel implements OnAddressRetrievedCallback, OnPOILoadedCallback {

    private MutableLiveData<String> address;
    private MutableLiveData<ArrayList<POI>> allPOIs;
    private AddressPickerActivityRepository addressPickerActivityRepository;

    public void init(){
        addressPickerActivityRepository = AddressPickerActivityRepository.getInstance();
        addressPickerActivityRepository.init();
        address = new MutableLiveData<>();
        allPOIs = new MutableLiveData<>();
    }

    public LiveData<String> getAddress(){
        return address;
    }

    public LiveData<ArrayList<POI>> getPOIs(){
        return allPOIs;
    }

    public void loadAddress(GeoPoint geoPoint) {
        addressPickerActivityRepository.getAddressOfGeoPoint(geoPoint, this);
    }

    public void searchAddress(String query, GeoPoint startPoint){
        addressPickerActivityRepository.getGeoPointsFromQuery(query, startPoint, this);
    }

    @Override
    public void onAddressRetrievedCallback(String fullAddress) {
        address.postValue(fullAddress);
    }

    @Override
    public void onPOILoadedCallback(ArrayList<POI> listOfPOIs) {
        allPOIs.postValue(listOfPOIs);
    }
}
