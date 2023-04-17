package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private DatabaseReference databaseReference;
    private String currentUId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBindings();
    }

    private void setBindings(){
        FirebaseAuth currentAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = currentAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUId = currentUser.getUid();
        getToken();

        //Setting listener to profile activity
        binding.toolbarMainPage.ibProfile.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
        //Setting listener to settings activity
        binding.toolbarMainPage.ibSettings.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        androidx.appcompat.widget.Toolbar toolbarMainPage = findViewById(R.id.toolbar_mainPage);
        setSupportActionBar(toolbarMainPage);

        //Setting text to upper toolbar
        binding.toolbarMainPage.tvPageDefiner.setText("Мероприятия");

        //Setting adapter to bottom navigation bar
        binding.viewPagerMainFragmentPager.setAdapter(new BottomNavigationFragmentsAdapter(this));
        setUpNavigationBar();
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

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUId).
                child(Constants.KEY_FCM_TOKEN).setValue(token).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Токен успешно загружен", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = task.getException().toString();
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}