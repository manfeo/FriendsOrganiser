package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.Appointment;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.ItemFriendBinding;

import java.util.List;

public class AppointmentParticipantsAdapter extends RecyclerView.Adapter<AppointmentParticipantsAdapter.AppointmentParticipantsHolder>{

    private final List<UserInfo> friends;

    public AppointmentParticipantsAdapter(List<UserInfo> friends){
        this.friends = friends;
    }

    class AppointmentParticipantsHolder extends RecyclerView.ViewHolder{
        ItemFriendBinding binding;

        public AppointmentParticipantsHolder(ItemFriendBinding itemFriendBinding) {
            super(itemFriendBinding.getRoot());
            binding = itemFriendBinding;
        }

        private void setBinding(UserInfo userInfo) {
            String fullName = userInfo.getFullName();
            Uri friendPhoto = userInfo.getPhoto();
            if (friendPhoto != null)
                Glide.with(binding.getRoot()).load(friendPhoto).into(binding.ivFriendPhoto);
            else
                binding.ivFriendPhoto.setImageResource(R.drawable.avatar);
            binding.tvFriendName.setText(fullName);
        }
    }

    @NonNull
    @Override
    public AppointmentParticipantsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendBinding itemFriendBinding = ItemFriendBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AppointmentParticipantsHolder(itemFriendBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentParticipantsHolder holder, int position) {
        holder.setBinding(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}
