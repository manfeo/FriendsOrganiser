package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.Appointment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker.CustomMarkerInfoWindow;
import com.example.friendsorganiser.MainActivityPackage.FriendsPackage.FriendsList.FriendsAdapter;
import com.example.friendsorganiser.Models.AppointmentModel;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityAppointmentBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

public class Appointment extends AppCompatActivity {
    private ActivityAppointmentBinding binding;
    private AppointmentViewModel appointmentViewModel;
    private AppointmentParticipantsAdapter appointmentParticipantsAdapter;
    private IMapController iMapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For map correct work
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        appointmentViewModel.init();

        configureMap();
        getLoadedData();
        setListeners();
    }

    private void configureMap(){
        binding.mvAppointmentPlace.setTileSource(TileSourceFactory.MAPNIK);
        binding.mvAppointmentPlace.setMultiTouchControls(true);
        iMapController = binding.mvAppointmentPlace.getController();
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(binding.mvAppointmentPlace);
        mRotationGestureOverlay.setEnabled(true);
        binding.mvAppointmentPlace.getOverlays().add(mRotationGestureOverlay);
    }

    private void getLoadedData(){
        String appointmentId = getIntent().getStringExtra(Constants.KEY_APPOINTMENT_ID);
        appointmentViewModel.loadAppointmentData(appointmentId);
    }

    private void setListeners(){
        binding.ibCreatedAppointmentClose.setOnClickListener(v -> finish());

        appointmentViewModel.getAppointmentModel().observe(this, appointmentModel -> {
            binding.tvCreatedAppointmentTitle.setText(appointmentModel.getAppointmentTitle());
            binding.tvCreatedAppointmentAddress.setText(appointmentModel.getAppointmentAddress());
            binding.tvCreatedAppointmentTime.setText(appointmentModel.getAppointmentDate());
            setAddressOnMap(appointmentModel);
        });
        appointmentViewModel.getParticipants().observe(this, participants -> {
            appointmentParticipantsAdapter = new AppointmentParticipantsAdapter(participants);
            binding.rvCreatedAppointmentParticipants.setAdapter(appointmentParticipantsAdapter);
        });
    }

    private void setAddressOnMap(AppointmentModel appointmentModel){
        FolderOverlay folderOverlay = new FolderOverlay();
        Drawable markerIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.search_result, null);
        GeoPoint selectedGeoPoint = new GeoPoint(appointmentModel.getLatitude(), appointmentModel.getLongitude());
        String geoPointAddress = appointmentModel.getAppointmentAddress();
        Marker selectedMarker = new Marker(binding.mvAppointmentPlace);
        selectedMarker.setPosition(selectedGeoPoint);
        selectedMarker.setIcon(markerIcon);
        selectedMarker.setInfoWindow(new CustomMarkerInfoWindow(
                R.layout.marker_info_window,
                binding.mvAppointmentPlace,
                geoPointAddress));
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        folderOverlay.add(selectedMarker);
        binding.mvAppointmentPlace.getOverlays().add(folderOverlay);
        binding.mvAppointmentPlace.invalidate();
        iMapController.setZoom(18.85);
        iMapController.animateTo(selectedGeoPoint);
    }
}