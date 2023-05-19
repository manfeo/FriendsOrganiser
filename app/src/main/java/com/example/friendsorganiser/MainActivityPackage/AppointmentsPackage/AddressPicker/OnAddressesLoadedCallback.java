package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker;

import com.example.friendsorganiser.Models.AddressModel;

import java.util.List;

public interface OnAddressesLoadedCallback {
    void onAddressesLoadedCallback(List<AddressModel> loadedAddresses);
}
