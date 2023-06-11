package com.example.friendsorganiser.MainActivityPackage.FriendsPackage.AddNewFriendsDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.friendsorganiser.MainActivityPackage.FriendsPackage.FriendsList.FriendsAdapter;
import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.AddNewFriendsDialogFragmentBinding;
import com.example.friendsorganiser.databinding.FragmentFriendBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddNewFriends extends DialogFragment {

    private AddNewFriendsDialogFragmentBinding binding;
    private AddNewFriendsViewModel addNewFriendsViewModel;
    private AddNewFriendsAdapter addNewFriendsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddNewFriendsDialogFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addNewFriendsViewModel = new ViewModelProvider(requireActivity()).get(AddNewFriendsViewModel.class);
        addNewFriendsViewModel.init();
        addNewFriendsViewModel.loadUsers();

        init();
        setBinding();
    }

    private void init(){
        addNewFriendsAdapter = new AddNewFriendsAdapter(addNewFriendsViewModel.getUsersList().getValue());
        binding.rvAllUsers.setAdapter(addNewFriendsAdapter);
    }

    private void setBinding(){
        binding.btSendFriendsRequests.setOnClickListener(v -> addNewFriendsViewModel.sendRequests());
        binding.ibCancelAddNewFriends.setOnClickListener(v -> dismiss());

        addNewFriendsViewModel.getUsersList().observe(getViewLifecycleOwner(), users -> {
            if (users.size() > 0) {
                addNewFriendsAdapter.notifyDataSetChanged();
                binding.rvAllUsers.setVisibility(View.VISIBLE);
                binding.tvNoUsers.setVisibility(View.INVISIBLE);
            } else {
                binding.tvNoUsers.setVisibility(View.VISIBLE);
            }
        });

        addNewFriendsViewModel.isUsersLoading().observe(getViewLifecycleOwner(), this::loadingListOfUsers);

        addNewFriendsViewModel.isSentRequests().observe(getViewLifecycleOwner(), isLoadingRequest -> {
            loadingSendFriendsRequests(isLoadingRequest);
            dismiss();
        });
    }

    private void loadingListOfUsers(boolean isLoading){
        if (isLoading){
            binding.pbLoadingAllUsers.setVisibility(View.VISIBLE);
        } else {
            binding.pbLoadingAllUsers.setVisibility(View.INVISIBLE);
        }
    }

    private void loadingSendFriendsRequests(boolean isLoading){
        if (isLoading){
            binding.btSendFriendsRequests.setVisibility(View.INVISIBLE);
            binding.pbLoadingFriendsRequests.setVisibility(View.VISIBLE);
        } else {
            binding.btSendFriendsRequests.setVisibility(View.VISIBLE);
            binding.pbLoadingFriendsRequests.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        int height = (int)(getResources().getDisplayMetrics().heightPixels);

        getDialog().getWindow().setLayout(width, height);
    }
}
