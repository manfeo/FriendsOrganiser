package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.friendsorganiser.Models.AddressModel;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityAddressPickerBinding;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddressPickerActivity extends AppCompatActivity{
    private ActivityAddressPickerBinding binding;
    private AddressPickerActivityViewModel addressPickerActivityViewModel;
    private IMapController iMapController;
    private MyLocationNewOverlay myLocationNewOverlay;
    private final GeoPoint defaultGeoPoint = new GeoPoint(55.7438, 37.6199);
    private GeoPoint selectedGeoPoint;
    private Marker selectedMarker;
    private AddressModel selectedAddress;
    private FolderOverlay folderOverlay;
    private FolderOverlay selectedPositionOverlay;
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
        else {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                return;
        }
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        //Overlay for markers
        folderOverlay = new FolderOverlay();
        selectedPositionOverlay = new FolderOverlay();
        //Moving do default position
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
                GeoPoint searchLocation;
                if (myLocationNewOverlay == null) {
                    IGeoPoint mapCenter = binding.mvMap.getMapCenter();
                    searchLocation = new GeoPoint(mapCenter.getLatitude(), mapCenter.getLongitude());
                } else {
                    searchLocation = myLocationNewOverlay.getMyLocation();
                }
                addressPickerActivityViewModel.searchAddress(query, searchLocation);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                binding.etSearchAddress.setText(null);
            }
            return true;
        });
        addressPickerActivityViewModel.getPOIs().observe(this, pois -> {
            Drawable markerIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.search_result, null);
            binding.mvMap.getOverlays().remove(folderOverlay);
            binding.mvMap.invalidate();
            folderOverlay = new FolderOverlay();
            for (POI poi : pois){
                Marker poiMarker = new Marker(binding.mvMap);
                poiMarker.setTitle(poi.mType);
                poiMarker.setSnippet(poi.mDescription);
                poiMarker.setPosition(poi.mLocation);
                poiMarker.setIcon(markerIcon);
                folderOverlay.add(poiMarker);
            }
            binding.mvMap.getOverlays().add(folderOverlay);
            binding.mvMap.invalidate();
        });
        addressPickerActivityViewModel.getAddresses().observe(this, addressModels -> {
            Drawable markerIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.search_result, null);
            binding.mvMap.getOverlays().remove(folderOverlay);
            binding.mvMap.invalidate();
            folderOverlay = new FolderOverlay();
            if (addressModels != null) {
                for (AddressModel anotherAddress : addressModels) {
                    Marker poiMarker = new Marker(binding.mvMap);
                    poiMarker.setTitle(anotherAddress.getTitle());
                    poiMarker.setSnippet(anotherAddress.getAddress());
                    GeoPoint markerGeoPoint = new GeoPoint(anotherAddress.getLatitude(), anotherAddress.getLongitude());
                    poiMarker.setPosition(markerGeoPoint);
                    poiMarker.setIcon(markerIcon);
                    folderOverlay.add(poiMarker);
                }
                binding.mvMap.getOverlays().add(folderOverlay);
                binding.mvMap.invalidate();
            }
        });
        binding.btClearAllMarkers.setOnClickListener(v -> {
            binding.mvMap.getOverlays().remove(folderOverlay);
            binding.mvMap.getOverlays().remove(selectedPositionOverlay);
            binding.mvMap.invalidate();
        });
        binding.btNewAppointmentConfirmAddress.setOnClickListener(v -> {
            if (selectedGeoPoint != null) {
                Intent resultingIntent = new Intent();
                resultingIntent.putExtra(Constants.KEY_SET_ADDRESS, selectedAddress);
                setResult(Activity.RESULT_OK, resultingIntent);
                finish();
            }
        });
    }

    private void handleTap(MotionEvent event, MapView mapView){
        int coordinateX = (int) event.getX();
        int coordinateY = (int) event.getY();
        Projection projection = mapView.getProjection();
        Drawable markerIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.selected_address, null);
        selectedGeoPoint = (GeoPoint) projection.fromPixels(coordinateX, coordinateY);

        addressPickerActivityViewModel.loadAddress(selectedGeoPoint);

        addressPickerActivityViewModel.getAddress().observe(this, fullAddress -> {
            selectedAddress = new AddressModel(fullAddress, selectedGeoPoint.getLongitude(), selectedGeoPoint.getLatitude());
            binding.mvMap.getOverlays().remove(selectedPositionOverlay);
            selectedPositionOverlay = new FolderOverlay();
            selectedMarker.setPosition(selectedGeoPoint);
            selectedMarker.setIcon(markerIcon);
            selectedMarker.setInfoWindow(new CustomMarkerInfoWindow(
                    R.layout.marker_info_window,
                    binding.mvMap,
                    fullAddress));
            selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            selectedPositionOverlay.add(selectedMarker);
            binding.mvMap.getOverlays().add(selectedPositionOverlay);
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