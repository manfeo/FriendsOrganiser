package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendsorganiser.Models.Appointment;
import com.example.friendsorganiser.databinding.CreateNewAppointmentBinding;

public class NewAppointmentDialog extends DialogFragment {

    private CreateNewAppointmentBinding binding;
    private NewAppointmentDialogViewModel newAppointmentDialogViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateNewAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newAppointmentDialogViewModel = new ViewModelProvider(requireActivity()).get(NewAppointmentDialogViewModel.class);
        newAppointmentDialogViewModel.init();

        setListeners();
    }

    private void setListeners(){
        binding.ibNewAppointmentCancel.setOnClickListener(v -> dismiss());
        binding.btNewAppointmentCreate.setOnClickListener(v -> {
            String appointmentTitle = binding.etNewAppointmentTitle.getText().toString();
            String appointmentAddress = binding.etNewAppointmentManualAddress.getText().toString();
            String appointmentDate = binding.etNewAppointmentDate.getText().toString();

            newAppointmentDialogViewModel.createNewAppointment(appointmentTitle, appointmentAddress, appointmentDate);
        });
    }
}
