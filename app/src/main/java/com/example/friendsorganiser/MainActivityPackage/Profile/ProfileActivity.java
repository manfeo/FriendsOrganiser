package com.example.friendsorganiser.MainActivityPackage.Profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.ActivityProfileBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileActivityViewModel profileActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileActivityViewModel = new ViewModelProvider(this).get(ProfileActivityViewModel.class);

        setBinding();
        loadData();
        setListeners();
    }

    ActivityResultLauncher<Intent> startActivityForImageCropping = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    Uri newImage = UCrop.getOutput(intent);
                    profileActivityViewModel.uploadProfileImage(newImage);
                } else {
                    Toast.makeText(this, "Выберете другое изображение", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<Intent> startActivityForImagePicking = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    Uri sourceUri = intent.getData();
                    File destinationFile = getImageFile();
                    Uri destinationUri = Uri.fromFile(destinationFile);
                    UCrop.Options options = new UCrop.Options();
                    options.setCircleDimmedLayer(true);
                    Intent cropIntent = UCrop.of(sourceUri, destinationUri).withOptions(options).
                            withAspectRatio(1, 1).getIntent(this);
                    startActivityForImageCropping.launch(cropIntent);
                }
            });

    private void setBinding(){
        Toolbar toolbarProfile = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbarProfile);

        //Setting upper toolbar text to "Your profile"
        binding.toolbarProfile.tvProfileTitle.setText("Ваш профиль");
    }

    private void loadData(){
        String userToShow = getIntent().getStringExtra(Constants.KEY_USER_ID);
        profileActivityViewModel.init(userToShow);
        profileActivityViewModel.loadUser();
    }

    private void setListeners(){
        profileActivityViewModel.getUserInfo().observe(this, userInfo -> {
            binding.tvProfileName.setText(userInfo.getName());
            binding.tvProfileSurname.setText(userInfo.getSurname());
            binding.tvProfileBirthDate.setText(userInfo.getDateOfBirth());
            binding.tvProfileEmail.setText(userInfo.getEmail());
            Uri userPhoto = userInfo.getPhoto();
            if (userPhoto != null)
                Glide.with(binding.getRoot()).load(userPhoto).into(binding.ciProfileImage);
        });
        boolean isMyProfile = profileActivityViewModel.isMyProfile().getValue();
        if (isMyProfile) {
            binding.ciProfileImage.setOnClickListener(v -> {
                int readExternalStorageGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                int writeExternalStorageGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (readExternalStorageGranted != PackageManager.PERMISSION_GRANTED || writeExternalStorageGranted != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                else
                    openImagesDocument();
            });
        }
        binding.toolbarProfile.ibBackButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int anotherResult : grantResults){
            if (anotherResult != PackageManager.PERMISSION_GRANTED)
                return;
        }
        openImagesDocument();
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForImagePicking.launch(Intent.createChooser(pictureIntent, "Выберете изображение"));
    }

    private File getImageFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        try {
            return File.createTempFile(imageFileName, ".jpg", this.getCacheDir());
        } catch (IOException e){
            Log.d("photo", "Unable to make temp file");
        }
        return null;
    }
}