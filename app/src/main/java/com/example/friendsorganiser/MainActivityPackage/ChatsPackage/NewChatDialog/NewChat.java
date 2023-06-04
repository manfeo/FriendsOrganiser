package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting.ChattingActivity;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.CreateNewChatBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class NewChat extends DialogFragment {
    private CreateNewChatBinding binding;
    private NewChatDialogViewModel newChatDialogViewModel;
    private FriendsPickerAdapter friendsPickerAdapter;
    private Uri chatImage = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateNewChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> startActivityForImageCropping = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    chatImage = UCrop.getOutput(intent);
                    Glide.with(binding.getRoot()).load(chatImage).into(binding.ibNewChatPicture);
                } else {
                    Toast.makeText(getContext(), "Выберете другое изображение", Toast.LENGTH_SHORT).show();
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
                    options.setCompressionQuality(20);
                    Intent cropIntent = UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withOptions(options).getIntent(getContext());
                    startActivityForImageCropping.launch(cropIntent);
                }
            });

    ActivityResultLauncher<String[]> onRequestPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (!result.containsValue(false))
                    openImagesDocument();
            }
    );

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newChatDialogViewModel = new ViewModelProvider(requireActivity()).get(NewChatDialogViewModel.class);
        newChatDialogViewModel.init();
        newChatDialogViewModel.loadFriends();

        setListeners();
        setObservers();
    }

    private void setListeners() {
        binding.ibNewChatPicture.setOnClickListener(v -> {
            int readExternalStorageGranted = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalStorageGranted = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (readExternalStorageGranted != PackageManager.PERMISSION_GRANTED || writeExternalStorageGranted != PackageManager.PERMISSION_GRANTED)
                onRequestPermissionResult.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
            else
                openImagesDocument();
        });

        binding.btNewChatCreate.setOnClickListener(v -> newChatDialogViewModel.
                createNewChat(binding.etNewChatName.getText().toString(), chatImage));

        binding.ibNewChatCloseDialog.setOnClickListener(v -> dismiss());
    }

    private void setObservers(){
        newChatDialogViewModel.isFriendsLoading().observe(getViewLifecycleOwner(), this::loading);

        newChatDialogViewModel.getChatId().observe(getViewLifecycleOwner(), this::loadChattingActivity);

        newChatDialogViewModel.getFriendsList().observe(getViewLifecycleOwner(), friendsList -> {
            friendsPickerAdapter = new FriendsPickerAdapter(friendsList);
            if (friendsList.size() > 0) {
                binding.rvNewChatDisplayFriends.setAdapter(friendsPickerAdapter);
                binding.rvNewChatDisplayFriends.setVisibility(View.VISIBLE);
            } else {
                binding.tvNewChatNoFriends.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForImagePicking.launch(Intent.createChooser(pictureIntent, "Выберете изображение"));
    }

    private void loadChattingActivity(String chatId){
        Intent chat = new Intent(getActivity(), ChattingActivity.class);
        chat.putExtra(Constants.KEY_CHAT_ID, chatId);
        startActivity(chat);
        dismiss();
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.pbNewChatLoadingFriends.setVisibility(View.VISIBLE);
        } else {
            binding.pbNewChatLoadingFriends.setVisibility(View.INVISIBLE);
        }
    }

    private File getImageFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        try {
            return File.createTempFile(imageFileName, ".jpg", getActivity().getCacheDir());
        } catch (IOException e){
            Log.d("photo", "Unable to make temp file");
        }
        return null;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }
}
