package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker;

import android.widget.TextView;
import com.example.friendsorganiser.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class CustomMarkerInfoWindow extends MarkerInfoWindow {

    private String fullAddress;

    public CustomMarkerInfoWindow(int layoutResId, MapView mapView, String fullAddress) {
        super(layoutResId, mapView);
        this.fullAddress = fullAddress;
    }

    @Override
    public void onOpen(Object item) {
        super.onOpen(item);
        TextView title = mView.findViewById(R.id.bubble_title);
        String titleString = "Адрес: " + fullAddress;
        title.setText(titleString);
    }
}
