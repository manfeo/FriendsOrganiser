package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityAddressPickerBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

public class AddressPickerActivity extends AppCompatActivity {
    private ActivityAddressPickerBinding binding;
    private NewAppointmentDialogViewModel newAppointmentDialogViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddressPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        newAppointmentDialogViewModel = new ViewModelProvider(this).get(NewAppointmentDialogViewModel.class);

        MapKitFactory.initialize(this);
        binding.mvNewAppointmentMap.getMap().move(
                new CameraPosition(new Point(58.147900, 52.661581), 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);

        setListeners();
    }

    private void setListeners(){
        binding.ibNewAppointmentCancelMap.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStop() {
        binding.mvNewAppointmentMap.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        binding.mvNewAppointmentMap.onStart();
    }
}