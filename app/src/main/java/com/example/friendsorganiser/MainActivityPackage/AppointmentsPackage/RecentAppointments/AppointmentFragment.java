package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.RecentAppointments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.Appointment.Appointment;
import com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment.NewAppointmentDialog;
import com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog.NewChat;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.FragmentAppointmentBinding;

public class AppointmentFragment extends Fragment implements OnAppointmentClick{

    private FragmentAppointmentBinding binding;
    private AppointmentFragmentViewModel appointmentFragmentViewModel;
    private AppointmentsAdapter appointmentsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appointmentFragmentViewModel = new ViewModelProvider(requireActivity()).get(AppointmentFragmentViewModel.class);
        appointmentFragmentViewModel.init();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init(){
        appointmentsAdapter = new AppointmentsAdapter(appointmentFragmentViewModel.getAppointmentsList().getValue(), this);
        binding.rvAllAppointments.setAdapter(appointmentsAdapter);
    }

    private void setListeners(){
        appointmentFragmentViewModel.getAppointmentsList().observe(getViewLifecycleOwner(), appointmentList -> {
            if (appointmentList.size() == 0){
                binding.tvNoAppointments.setVisibility(View.VISIBLE);
                binding.rvAllAppointments.setVisibility(View.GONE);
            } else {
                binding.tvNoAppointments.setVisibility(View.GONE);
                appointmentsAdapter.notifyDataSetChanged();
                binding.rvAllAppointments.setVisibility(View.VISIBLE);
            }
            binding.fbCreateNewAppointment.setVisibility(View.VISIBLE);
        });
        appointmentFragmentViewModel.isAppointmentListLoading().observe(getViewLifecycleOwner(), this::appointmentListLoading);
        binding.fbCreateNewAppointment.setOnClickListener(v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            NewAppointmentDialog appointmentDialog = new NewAppointmentDialog();
            appointmentDialog.show(manager, "newAppointmentCreation");
        });
    }

    private void appointmentListLoading(boolean isLoading){
        if (isLoading)
            binding.pbLoadingAppointments.setVisibility(View.VISIBLE);
        else
            binding.pbLoadingAppointments.setVisibility(View.GONE);
    }

    @Override
    public void onAppointmentClick(String appointmentId) {
        Intent appointmentIntent = new Intent(getActivity(), Appointment.class);
        appointmentIntent.putExtra(Constants.KEY_APPOINTMENT_ID, appointmentId);
        startActivity(appointmentIntent);
    }
}