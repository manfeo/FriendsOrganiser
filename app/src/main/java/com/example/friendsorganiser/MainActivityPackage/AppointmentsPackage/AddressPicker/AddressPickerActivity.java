package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment.NewAppointmentDialogViewModel;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.ActivityAddressPickerBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;

public class AddressPickerActivity extends AppCompatActivity{
    private ActivityAddressPickerBinding binding;
    private AddressPickerActivityViewModel addressPickerActivityViewModel;
    private IMapController iMapController;
    private MyLocationNewOverlay myLocationNewOverlay;
    private final GeoPoint defaultGeoPoint = new GeoPoint(55.7438, 37.6199);
    private GeoPoint selectedGeoPoint;
    private Marker selectedMarker;
    private boolean statusOfGPS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = ActivityAddressPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addressPickerActivityViewModel = new ViewModelProvider(this).get(AddressPickerActivityViewModel.class);
        addressPickerActivityViewModel.init();

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
        //Creating marker for place selection
        selectedMarker = new Marker(binding.mvMap);
    }

    private void initUserLocation(){
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), binding.mvMap);
        myLocationNewOverlay.enableMyLocation();
        /*
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.user_location, null);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        myLocationNewOverlay.setPersonIcon(bitmap);
         */
        myLocationNewOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
            iMapController.setZoom(18.85);
            moveCamera(myLocationNewOverlay.getMyLocation());
        }));
        binding.mvMap.getOverlays().add(myLocationNewOverlay);
    }

    private void setListeners(){
        binding.ibNewAppointmentCancelMap.setOnClickListener(v -> finish());
        binding.mvMap.getOverlays().add(new Overlay() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event, MapView mapView) {
                try {
                    handleTap(event, mapView);
                } catch (Exception e){
                    Toast.makeText(AddressPickerActivity.this, "Unable to get address", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        binding.etSearchAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                String query = binding.etSearchAddress.getText().toString();
                addressPickerActivityViewModel.searchAddress(query, myLocationNewOverlay.getMyLocation());
            }
            return true;
        });
        addressPickerActivityViewModel.getPOIs().observe(this, pois -> {
            Drawable markerIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.search_result, null);
            FolderOverlay folderOverlay = new FolderOverlay();
            binding.mvMap.getOverlays().add(folderOverlay);
            for (POI poi : pois){
                Marker poiMarker = new Marker(binding.mvMap);
                poiMarker.setTitle(poi.mType);
                poiMarker.setSnippet(poi.mDescription);
                poiMarker.setPosition(poi.mLocation);
                poiMarker.setIcon(markerIcon);
                folderOverlay.add(poiMarker);
            }
            binding.mvMap.invalidate();
        });
    }

    private void handleTap(MotionEvent event, MapView mapView){
        int coordinateX = (int) event.getX();
        int coordinateY = (int) event.getY();
        Projection projection = mapView.getProjection();
        Drawable markerIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.search_result, null);
        selectedGeoPoint = (GeoPoint) projection.fromPixels(coordinateX, coordinateY);

        addressPickerActivityViewModel.loadAddress(selectedGeoPoint);

        addressPickerActivityViewModel.getAddress().observe(this, fullAddress -> {
            selectedMarker.setPosition(selectedGeoPoint);
            selectedMarker.setIcon(markerIcon);
            selectedMarker.setInfoWindow(new CustomMarkerInfoWindow(
                    R.layout.marker_info_window,
                    binding.mvMap,
                    fullAddress));
            selectedMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER);
            binding.mvMap.getOverlays().add(selectedMarker);
            binding.mvMap.invalidate();
        });
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