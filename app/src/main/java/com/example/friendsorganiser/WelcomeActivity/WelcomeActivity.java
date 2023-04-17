package com.example.friendsorganiser.WelcomeActivity;

import androidx.appcompat.app.AppCompatActivity;

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
    private FirebaseAuth currentUserAuth;
    private FirebaseUser currentUser;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        currentUserAuth = FirebaseAuth.getInstance();
        currentUser = currentUserAuth.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(this::handleCurrentUser, 4000);
    }

    private void handleCurrentUser() {
        if (currentUser == null) {
            startRegisterLoginActivity();
        } else if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            startMainActivity();
        } else {
            startRegisterLoginActivity();
        }
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