package com.example.friendsorganiser.MainActivityPackage.Notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.ActivityNotificationsBinding;

public class NotificationsActivity extends AppCompatActivity implements OnNotificationAnswer{
    private ActivityNotificationsBinding binding;
    private NotificationsActivityViewModel notificationsActivityViewModel;
    private NotificationsAdapter notificationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        notificationsActivityViewModel = new ViewModelProvider(this).get(NotificationsActivityViewModel.class);
        notificationsActivityViewModel.init();

        init();
        setBinding();
        setListeners();
    }

    private void init(){
        notificationsAdapter = new NotificationsAdapter(notificationsActivityViewModel.getNotifications().getValue(), this);
        binding.rvAllNotifications.setAdapter(notificationsAdapter);
    }

    private void setBinding(){
        setSupportActionBar(binding.toolbarNotifications.getRoot());

        binding.toolbarNotifications.tvNotificationsTitle.setText("Уведомления");
    }

    private void setListeners(){
        notificationsActivityViewModel.getNotifications().observe(this, notifications -> {
            if (notifications.size() == 0){
                binding.rvAllNotifications.setVisibility(View.GONE);
                binding.tvNoNotifications.setVisibility(View.VISIBLE);
            } else {
                notificationsAdapter.notifyDataSetChanged();
                binding.rvAllNotifications.setVisibility(View.VISIBLE);
                binding.tvNoNotifications.setVisibility(View.GONE);
            }
        });
        binding.toolbarNotifications.ibBackButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onNotificationAnswer(UserInfo notificationUser, String answer) {
        notificationsActivityViewModel.onNotificationAnswer(notificationUser, answer);
    }
}