package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.friendsorganiser.MainActivityPackage.Notifications.NotificationsActivity;
import com.example.friendsorganiser.MainActivityPackage.Profile.ProfileActivity;
import com.example.friendsorganiser.MainActivityPackage.Settings.SettingsActivity;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.init();

        setBindings();
        setListeners();
    }

    private void setBindings(){
        mainActivityViewModel.setToken();

        setSupportActionBar(binding.toolbarMainPage.getRoot());

        //Setting text to upper toolbar
        binding.toolbarMainPage.tvPageDefiner.setText("Мероприятия");

        //Setting adapter to bottom navigation bar
        binding.viewPagerMainFragmentPager.setAdapter(new BottomNavigationFragmentsAdapter(this));
        setUpNavigationBar();
    }

    private void setListeners(){
        //Setting listener to profile activity
        binding.toolbarMainPage.ivProfileImage.setOnClickListener(view -> {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            profileIntent.putExtra(Constants.KEY_USER_ID, mainActivityViewModel.getCurrentUserId().getValue());
            startActivity(profileIntent);
        });
        //Setting listener to settings activity
        binding.toolbarMainPage.ibSettings.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        });
        //Setting listener to notifications activity
        binding.toolbarMainPage.ibNotifications.setOnClickListener(view -> {
            Intent notificationsIntent = new Intent(this, NotificationsActivity.class);
            startActivity(notificationsIntent);
        });
        mainActivityViewModel.getCurrentUserPhoto().observe(this, uri -> {
            if (uri != null)
                Glide.with(binding.getRoot()).load(uri).into(binding.toolbarMainPage.ivProfileImage);
        });
        mainActivityViewModel.getIsNotifications().observe(this, isNotifications -> {
            if (isNotifications)
                binding.toolbarMainPage.ibNotifications.setImageResource(R.drawable.notification);
            else
                binding.toolbarMainPage.ibNotifications.setImageResource(R.drawable.no_notifications);
        });
    }

    private void setUpNavigationBar() {
        binding.navBarMain.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.item_appointments: {
                    binding.viewPagerMainFragmentPager.setCurrentItem(0);
                    binding.toolbarMainPage.tvPageDefiner.setText("Мероприятия");
                    break;
                }
                case R.id.item_chats: {
                    binding.viewPagerMainFragmentPager.setCurrentItem(1);
                    binding.toolbarMainPage.tvPageDefiner.setText("Сообщения");
                    break;
                }
                case R.id.item_friends: {
                    binding.viewPagerMainFragmentPager.setCurrentItem(2);
                    binding.toolbarMainPage.tvPageDefiner.setText("Друзья");
                    break;
                }
            }
            return true;
        });
    }
}