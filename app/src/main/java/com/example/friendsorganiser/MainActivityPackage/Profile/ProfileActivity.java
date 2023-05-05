package com.example.friendsorganiser.MainActivityPackage.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileActivityViewModel profileActivityViewModel;
    private String userToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileActivityViewModel = new ViewModelProvider(this).get(ProfileActivityViewModel.class);
        profileActivityViewModel.init();

        setBinding();
        loadData();
        setObservers();
    }

    private void setBinding(){
        Toolbar toolbarProfile = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbarProfile);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);

        //Setting upper toolbar text to "Your profile"
        binding.toolbarProfile.tvPageDefiner.setText("Ваш профиль");
    }

    private void loadData(){
        userToShow = getIntent().getStringExtra(Constants.KEY_USER_ID);
        profileActivityViewModel.loadUser(userToShow);
    }

    private void setObservers(){
        profileActivityViewModel.getUserInfo().observe(this, userInfo -> {
            binding.tvProfileName.setText(userInfo.getName());
            binding.tvProfileSurname.setText(userInfo.getSurname());
            binding.tvProfileBirthDate.setText(userInfo.getDateOfBirth());
        });
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