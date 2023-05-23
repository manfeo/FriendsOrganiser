package com.example.friendsorganiser.MainActivityPackage.Notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.ActivityNotificationsBinding;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements OnNotificationAnswer{
    private ActivityNotificationsBinding binding;
    private NotificationsActivityViewModel notificationsActivityViewModel;
    private NotificationsAdapter notificationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.appcompat.widget.Toolbar toolbarNotifications = findViewById(R.id.toolbar_notifications);
        setSupportActionBar(toolbarNotifications);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);


        notificationsActivityViewModel = new ViewModelProvider(this).get(NotificationsActivityViewModel.class);
        notificationsActivityViewModel.init();

        init();
        setListeners();
    }

    private void init(){
        notificationsAdapter = new NotificationsAdapter(notificationsActivityViewModel.getNotifications().getValue(), this);
        binding.rvAllNotifications.setAdapter(notificationsAdapter);
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
    }

    @Override
    public void onNotificationAnswer(UserInfo notificationUser, String answer) {
        notificationsActivityViewModel.onNotificationAnswer(notificationUser, answer);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}