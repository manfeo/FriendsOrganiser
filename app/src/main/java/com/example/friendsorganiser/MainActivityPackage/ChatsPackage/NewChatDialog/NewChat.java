package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting.ChattingActivity;
import com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog.FriendsPickerAdapter;
import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.CreateNewChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewChat extends DialogFragment {
    private CreateNewChatBinding binding;
    private NewChatDialogViewModel newChatDialogViewModel;
    private FriendsPickerAdapter friendsPickerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateNewChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

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
        binding.btNewChatCreate.setOnClickListener(v -> newChatDialogViewModel.
                createNewChat(binding.etNewChatName.getText().toString()));

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
