package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.RecentAppointments;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendsorganiser.Models.AppointmentModel;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.ItemAppointmentBinding;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder> {

    private List<AppointmentModel> appointmentModelList;
    private OnAppointmentClick onAppointmentClick;

    public AppointmentsAdapter(List<AppointmentModel> appointmentModels, OnAppointmentClick onAppointmentClick){
        this.appointmentModelList = appointmentModels;
        this.onAppointmentClick = onAppointmentClick;
    }

    public class AppointmentsViewHolder extends RecyclerView.ViewHolder{

        public ItemAppointmentBinding binding;

        public AppointmentsViewHolder(ItemAppointmentBinding itemAppointmentBinding) {
            super(itemAppointmentBinding.getRoot());
            binding = itemAppointmentBinding;
        }

        public void setBinding(AppointmentModel appointmentModel){
            binding.tvAppointmentTitle.setText(appointmentModel.getAppointmentTitle());
            binding.tvAppointmentDate.setText(appointmentModel.getAppointmentDate());
            Uri appointmentPhoto = appointmentModel.getAppointmentPhoto();
            if (appointmentPhoto != null)
                Glide.with(binding.getRoot()).load(appointmentPhoto).into(binding.ivAppointmentPhoto);
            else
                binding.ivAppointmentPhoto.setImageResource(R.drawable.avatar);
            binding.getRoot().setOnClickListener(v -> {
                String appointmentId = appointmentModel.getAppointmentId();
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
        holder.setBinding(appointmentModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return appointmentModelList.size();
    }
}
