package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.osmdroid.util.GeoPoint;

public class AddressPickerActivityViewModel extends ViewModel implements OnAddressRetrievedCallback {

    private MutableLiveData<String> address;
    private AddressPickerActivityRepository addressPickerActivityRepository;

    public void init(){
        addressPickerActivityRepository = AddressPickerActivityRepository.getInstance();
        address = new MutableLiveData<>();
    }

    public LiveData<String> getAddress(){
        return address;
    }

    public void loadAddress(GeoPoint geoPoint) {
        addressPickerActivityRepository.getAddressOfGeoPoint(geoPoint, this);
    }

    @Override
    public void onAddressRetrievedCallback(String fullAddress) {
        address.postValue(fullAddress);
    }
}
