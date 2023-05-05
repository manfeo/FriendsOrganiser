package com.example.friendsorganiser.WelcomeActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.friendsorganiser.MainActivityPackage.MainActivity.MainActivity;
import com.example.friendsorganiser.RegistrationLogin.RegisterLoginActivity;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.ActivityWelcomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;
    private WelcomeActivityViewModel welcomeActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        welcomeActivityViewModel = new ViewModelProvider(this).get(WelcomeActivityViewModel.class);
        welcomeActivityViewModel.init();
        setObservers();
    }

    private void setObservers(){
        welcomeActivityViewModel.getIsSignedIn().observe(this, isSignedIn -> {
            if (isSignedIn)
                startMainActivity();
            else
                startRegisterLoginActivity();
        });
    }

    private void startRegisterLoginActivity(){
        Intent registerLoginIntent = new Intent(this, RegisterLoginActivity.class);
        startActivity(registerLoginIntent);
    }

    private void startMainActivity(){
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }
}