package com.example.friendsorganiser.MainActivityPackage.Notifications;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ItemNewFriendNotificationBinding;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsHolder> {
    private List<UserInfo> notifications;
    private OnNotificationAnswer onNotificationAnswer;

    public NotificationsAdapter(List<UserInfo> notifications, OnNotificationAnswer onNotificationAnswer){
        this.notifications = notifications;
        this.onNotificationAnswer = onNotificationAnswer;
    }

    class NotificationsHolder extends RecyclerView.ViewHolder{
        private ItemNewFriendNotificationBinding binding;

        public NotificationsHolder(ItemNewFriendNotificationBinding itemNewFriendNotificationBinding){
            super(itemNewFriendNotificationBinding.getRoot());
            binding = itemNewFriendNotificationBinding;
        }

        public void setBinding(UserInfo userInfo){
            String notificationText = userInfo.getName() + " " + userInfo.getSurname() + " хочет быть другом";
            binding.tvNewFriendName.setText(notificationText);
            Uri userPhoto = userInfo.getPhoto();
            if (userPhoto != null)
                Glide.with(binding.getRoot()).load(userPhoto).into(binding.ivNewFriendPhoto);
            binding.btAcceptNewFriend.setOnClickListener(v -> onNotificationAnswer.onNotificationAnswer(userInfo, Constants.KEY_ACCEPT_FRIENDSHIP));
            binding.btRejectNewFriend.setOnClickListener(v -> onNotificationAnswer.onNotificationAnswer(userInfo, Constants.KEY_REJECT_FRIENDSHIP));
        }
    }

    @NonNull
    @Override
    public NotificationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewFriendNotificationBinding itemNewFriendNotificationBinding = ItemNewFriendNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new NotificationsHolder(itemNewFriendNotificationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsHolder holder, int position) {
        holder.setBinding(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
