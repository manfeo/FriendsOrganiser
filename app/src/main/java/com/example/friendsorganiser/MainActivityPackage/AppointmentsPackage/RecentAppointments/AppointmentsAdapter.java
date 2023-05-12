package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.RecentAppointments;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendsorganiser.Models.Appointment;
import com.example.friendsorganiser.databinding.ItemAppointmentBinding;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder> {

    private List<Appointment> appointmentList;
    private OnAppointmentClick onAppointmentClick;

    public AppointmentsAdapter(List<Appointment> appointments, OnAppointmentClick onAppointmentClick){
        this.appointmentList = appointments;
        this.onAppointmentClick = onAppointmentClick;
    }

    public class AppointmentsViewHolder extends RecyclerView.ViewHolder{

        public ItemAppointmentBinding binding;

        public AppointmentsViewHolder(ItemAppointmentBinding itemAppointmentBinding) {
            super(itemAppointmentBinding.getRoot());
            binding = itemAppointmentBinding;
        }

        public void setBinding(Appointment appointment){
            binding.tvAppointmentTitle.setText(appointment.getAppointmentTitle());
            binding.tvAppointmentDate.setText(appointment.getAppointmentDate());

            binding.getRoot().setOnClickListener(v -> {
                String appointmentId = appointment.getAppointmentId();
                onAppointmentClick.onAppointmentClick(appointmentId);
            });
        }
    }

    @NonNull
    @Override
    public AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppointmentsViewHolder(
                ItemAppointmentBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsViewHolder holder, int position) {
        holder.setBinding(appointmentList.get(position));
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }
}
