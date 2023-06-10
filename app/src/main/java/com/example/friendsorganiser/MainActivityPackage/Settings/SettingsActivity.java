package com.example.friendsorganiser.MainActivityPackage.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendsorganiser.MainActivityPackage.Profile.ProfileActivity;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.RegistrationLogin.RegisterLoginActivity;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.ActivitySettingsBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SettingsActivityViewModel settingsActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        settingsActivityViewModel = new ViewModelProvider(this).get(SettingsActivityViewModel.class);
        settingsActivityViewModel.init();

        setContentView(binding.getRoot());

        setBinding();
        setListeners();
    }

    private void setBinding(){
        androidx.appcompat.widget.Toolbar toolbarSettings = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbarSettings);

        binding.toolbarSettings.tvSettingsTitle.setText("Настройки");
    }

    private void setListeners(){
        binding.btSignOut.setOnClickListener(v -> signOut());
        binding.toolbarSettings.ibBackButton.setOnClickListener(v -> onBackPressed());
    }

    private void signOut(){
        settingsActivityViewModel.signOut();

        Intent registerLoginIntent = new Intent(this, RegisterLoginActivity.class);
        startActivity(registerLoginIntent);
    }
}