package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.friendsorganiser.databinding.ActivityAddressPickerBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class AddressPickerActivity extends AppCompatActivity{
    private ActivityAddressPickerBinding binding;
    private NewAppointmentDialogViewModel newAppointmentDialogViewModel;
    private float ZOOM_LEVEL = 18f;
    private double DESIRED_ACCURACY = 0, MINIMAL_DISTANCE = 1;
    private long MINIMAL_TIME = 1000;
    private boolean USE_IN_BACKGROUND = false;
    private IMapController iMapController;
    private MyLocationNewOverlay myLocationNewOverlay;
    private GeoPoint defaultGeoPoint = new GeoPoint(55.7438, 37.6199);
    private boolean statusOfGPS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = ActivityAddressPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        newAppointmentDialogViewModel = new ViewModelProvider(this).get(NewAppointmentDialogViewModel.class);

        //Adding map configuration
        askPermission();
        configureMap();
        setGPSListener();

        setListeners();
    }

    private void askPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private final BroadcastReceiver gpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                LocationManager systemLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = systemLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (isGpsEnabled) {
                    initUserLocation();
                } else {
                    Log.d("map", "GPS is off");
                }
            }
        }
    };

    private void setGPSListener(){
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        registerReceiver(gpsSwitchStateReceiver, filter);
    }

    private void configureMap(){
        binding.mvMap.setTileSource(TileSourceFactory.MAPNIK);
        binding.mvMap.setMultiTouchControls(true);
        iMapController = binding.mvMap.getController();
        iMapController.setZoom(10.0);
        moveCamera(defaultGeoPoint);

        //Adding rotation opportunity to map
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(binding.mvMap);
        mRotationGestureOverlay.setEnabled(true);
        binding.mvMap.getOverlays().add(mRotationGestureOverlay);
    }

    private Runnable moveCameraRunnable = new Runnable() {
        @Override
        public void run() {
            moveCamera(myLocationNewOverlay.getMyLocation());
        }
    };

    private void initUserLocation(){
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), binding.mvMap);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
            iMapController.setZoom(18.85);
            moveCamera(myLocationNewOverlay.getMyLocation());
        }));
        binding.mvMap.getOverlays().add(myLocationNewOverlay);
    }

    private void setListeners(){
        binding.ibNewAppointmentCancelMap.setOnClickListener(v -> finish());
    }

    private void moveCamera(GeoPoint geoPoint){
        iMapController.animateTo(geoPoint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mvMap.onResume();
        if (statusOfGPS)
            initUserLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mvMap.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(gpsSwitchStateReceiver);
    }
}