package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    private DatabaseReference databaseReference;
    private String currentUserId;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setBinding();

    }

    private void setBinding(){
        androidx.appcompat.widget.Toolbar toolbarProfile = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbarProfile);

        FirebaseAuth currentAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = currentAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = currentUser.getUid();

        setDataToProfile();

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);

        //Setting upper toolbar text to "Your profile"
        binding.toolbarProfile.tvPageDefiner.setText("Ваш профиль");
    }

    private void setDataToProfile() {
        databaseReference.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.child("name").getValue().toString();
                        String userSurname = snapshot.child("surname").getValue().toString();
                        String userBirthDate = snapshot.child("dateOfBirth").getValue().toString();

                        binding.tvProfileName.setText(userName);
                        binding.tvProfileSurname.setText(userSurname);
                        binding.tvProfileBirthDate.setText(userBirthDate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
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