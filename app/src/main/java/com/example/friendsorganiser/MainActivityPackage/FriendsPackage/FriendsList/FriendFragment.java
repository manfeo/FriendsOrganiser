package com.example.friendsorganiser.MainActivityPackage.FriendsPackage.FriendsList;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.friendsorganiser.MainActivityPackage.FriendsPackage.AddNewFriendsDialog.AddNewFriends;
import com.example.friendsorganiser.MainActivityPackage.Profile.ProfileActivity;
import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.FragmentFriendBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment implements OnFriendClickedListener {
    private FragmentFriendBinding binding;
    private FriendFragmentViewModel friendFragmentViewModel;
    private FriendsAdapter friendsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendFragmentViewModel = new ViewModelProvider(requireActivity()).get(FriendFragmentViewModel.class);
        friendFragmentViewModel.init();

        friendFragmentViewModel.loadFriends();

        init();
        setListeners();
    }

    private void init(){
        friendsAdapter = new FriendsAdapter(friendFragmentViewModel.getFriendsList().getValue(), this);
        binding.rvAllFriendsDisplay.setAdapter(friendsAdapter);
    }

    private void setListeners(){
        friendFragmentViewModel.getFriendsList().observe(getViewLifecycleOwner(), friendsList -> {
            if (friendsList.size() > 0) {
                friendsAdapter.notifyDataSetChanged();
                binding.tvEmptyFriendsList.setVisibility(View.INVISIBLE);
                binding.rvAllFriendsDisplay.setVisibility(View.VISIBLE);
                binding.fbAddNewFriend.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyFriendsList.setVisibility(View.VISIBLE);
                binding.fbAddNewFriend.setVisibility(View.VISIBLE);
            }
        });

        binding.fbAddNewFriend.setOnClickListener(v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            AddNewFriends addNewFriends = new AddNewFriends();
            addNewFriends.show(manager, "addNewFriendsDialogFragment");
        });

        friendFragmentViewModel.isFriendsLoading().observe(getViewLifecycleOwner(), this::loading);
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.pbLoadingFriendsList.setVisibility(View.VISIBLE);
        } else {
            binding.pbLoadingFriendsList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFriendCLicked(String friendId) {
        Intent friendProfile = new Intent(getActivity(), ProfileActivity.class);
        friendProfile.putExtra(Constants.KEY_USER_ID, friendId);
        startActivity(friendProfile);
    }
}