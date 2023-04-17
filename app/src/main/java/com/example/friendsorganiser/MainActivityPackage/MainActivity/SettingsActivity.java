package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.friendsorganiser.R;
import com.example.friendsorganiser.RegistrationLogin.RegisterLoginActivity;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.ActivitySettingsBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private DatabaseReference databaseReference;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setBinding();
    }

    private void setBinding(){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        androidx.appcompat.widget.Toolbar toolbarSettings = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbarSettings);

        binding.toolbarSettings.ibSettings.setVisibility(View.INVISIBLE);
        binding.toolbarSettings.tvPageDefiner.setText("Настройки");

        binding.toolbarSettings.ibProfile.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.btSignOut.setOnClickListener(v -> {
            signOut();
        });
    }

    private void signOut(){
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).
                child(Constants.KEY_FCM_TOKEN).getRef().removeValue();

        preferenceManager.clear();

        Intent registerLoginIntent = new Intent(this, RegisterLoginActivity.class);
        registerLoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerLoginIntent);
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