package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityAddressPickerBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.user_location.UserLocationLayer;

public class AddressPickerActivity extends AppCompatActivity {
    private ActivityAddressPickerBinding binding;
    private NewAppointmentDialogViewModel newAppointmentDialogViewModel;
    private float ZOOM_LEVEL = 18f;
    private double DESIRED_ACCURACY = 0;
    private long MINIMAL_TIME = 1000;
    private double MINIMAL_DISTANCE = 1f;
    private boolean USE_IN_BACKGROUND = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Point myLocation;
    private Point defaultPosition = new Point(55.751437, 37.618897);
    private boolean statusOfGPS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddressPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        newAppointmentDialogViewModel = new ViewModelProvider(this).get(NewAppointmentDialogViewModel.class);

        MapKitFactory.initialize(this);

        askPermission();
        setListeners();
        initLocation();
        setGPSListener();
    }

    private void askPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        android.location.LocationManager manager = (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE );
        statusOfGPS = manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    private void initLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (!statusOfGPS){
                moveCamera(defaultPosition);
                return;
            }
            subscribeToUserLocation();

        } else {
            moveCamera(defaultPosition);
        }
    }

    private void subscribeToUserLocation(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                if (myLocation == null)
                    moveCamera(location.getPosition());
                myLocation = location.getPosition();
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
                Log.d("map", locationStatus.name());
            }
        };
        locationManager = MapKitFactory.getInstance().createLocationManager();
        locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY,
                MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, locationListener);
        MapKit mapKit = MapKitFactory.getInstance();
        UserLocationLayer userLocationLayer = mapKit.createUserLocationLayer(binding.mvNewAppointmentMap.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
    }

    private final BroadcastReceiver gpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (android.location.LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                android.location.LocationManager systemLocationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = systemLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);

                if (isGpsEnabled) {
                    if (locationListener == null && locationManager == null)
                        subscribeToUserLocation();
                } else {
                    Log.d("map", "GPS is off");
                }
            }
        }
    };

    private void setGPSListener(){
        IntentFilter filter = new IntentFilter(android.location.LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        registerReceiver(gpsSwitchStateReceiver, filter);
    }

    private void setListeners(){
        binding.ibNewAppointmentCancelMap.setOnClickListener(v -> finish());
    }

    private void moveCamera(Point movePoint){
        binding.mvNewAppointmentMap.getMap().move(
                new CameraPosition(movePoint, ZOOM_LEVEL, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        binding.mvNewAppointmentMap.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.mvNewAppointmentMap.onStop();
        MapKitFactory.getInstance().onStop();
        if (locationListener != null && locationManager != null)
            locationManager.unsubscribe(locationListener);
        unregisterReceiver(gpsSwitchStateReceiver);
    }
}