package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AddressPicker.AddressPickerActivity;
import com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog.FriendsPickerAdapter;
import com.example.friendsorganiser.Models.AddressModel;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.CreateNewAppointmentBinding;

import java.time.LocalDateTime;

public class NewAppointmentDialog extends DialogFragment {
    private CreateNewAppointmentBinding binding;
    private NewAppointmentDialogViewModel newAppointmentDialogViewModel;
    private FriendsPickerAdapter friendsPickerAdapter;
    private AddressModel setAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateNewAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> startActivityForResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = data.getExtras();
                    setAddress = (AddressModel) extras.get(Constants.KEY_SET_ADDRESS);
                    binding.etNewAppointmentManualAddress.setText(setAddress.getAddress());
                }
            });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newAppointmentDialogViewModel = new ViewModelProvider(requireActivity()).get(NewAppointmentDialogViewModel.class);
        newAppointmentDialogViewModel.init();

        init();
        setListeners();
    }

    private void init(){
        friendsPickerAdapter = new FriendsPickerAdapter(newAppointmentDialogViewModel.getFriends().getValue());
        binding.rvNewAppointmentFriends.setAdapter(friendsPickerAdapter);
    }

    private void setListeners(){
        binding.etNewAppointmentDate.addTextChangedListener(new DateTextWatcher(binding.etNewAppointmentDate, binding.etNewAppointmentTime));
        binding.etNewAppointmentTime.addTextChangedListener(new TimeTextWatcher(binding.etNewAppointmentTime, binding.etNewAppointmentDate));

        binding.btNewAppointmentOpenMap.setOnClickListener(v -> {
            Intent openMapIntent = new Intent(getActivity(), AddressPickerActivity.class);
            startActivityForResultLauncher.launch(openMapIntent);
        });

        binding.btNewAppointmentDateDialog.setOnClickListener(v -> createDatePickerDialog());

        binding.ibNewAppointmentCancel.setOnClickListener(v -> dismiss());

        binding.btNewAppointmentCreate.setOnClickListener(v -> {
            String appointmentTitle = binding.etNewAppointmentTitle.getText().toString();
            String appointmentAddress = setAddress.getAddress();
            String appointmentDate = binding.etNewAppointmentDate.getText().toString();
            String appointmentTime = binding.etNewAppointmentTime.getText().toString();
            double latitude = setAddress.getLatitude();
            double longitude = setAddress.getLongitude();

            newAppointmentDialogViewModel.createNewAppointment(appointmentTitle, appointmentAddress,
                    appointmentDate, appointmentTime, latitude, longitude);
        });

        newAppointmentDialogViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            if (friends.size() == 0){
                binding.rvNewAppointmentFriends.setVisibility(View.GONE);
                binding.tvNewAppointmentNoFriends.setVisibility(View.VISIBLE);
            } else {
                friendsPickerAdapter.notifyDataSetChanged();
                binding.rvNewAppointmentFriends.setVisibility(View.VISIBLE);
                binding.tvNewAppointmentNoFriends.setVisibility(View.GONE);
            }
        });

        newAppointmentDialogViewModel.isFriendsListLoading().observe(getViewLifecycleOwner(), this::friendsLoading);
        newAppointmentDialogViewModel.isNewAppointmentLoading().observe(getViewLifecycleOwner(), isNewAppointmentLoading -> {
            newAppointmentLoading(isNewAppointmentLoading);
            dismiss();
        });
    }

    private void createDatePickerDialog(){
        LocalDateTime currentTime = LocalDateTime.now();
        int currentYear = currentTime.getYear();
        int currentMonth = currentTime.getMonth().getValue() - 1;
        int currentDay = currentTime.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String setDate = String.format("%02d/%02d/%02d", dayOfMonth, month, year);
            binding.etNewAppointmentDate.setText(setDate);
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
        datePickerDialog.show();
    }

    private void friendsLoading(boolean isLoading){
        if (isLoading)
            binding.pbNewAppointmentFriendsLoading.setVisibility(View.VISIBLE);
        else
            binding.pbNewAppointmentFriendsLoading.setVisibility(View.GONE);
    }

    private void newAppointmentLoading(boolean isLoading){
        if (isLoading) {
            binding.btNewAppointmentCreate.setVisibility(View.GONE);
            binding.pbNewAppointmentCreateLoading.setVisibility(View.VISIBLE);
        } else {
            binding.btNewAppointmentCreate.setVisibility(View.VISIBLE);
            binding.pbNewAppointmentCreateLoading.setVisibility(View.GONE);
        }
    }
}
