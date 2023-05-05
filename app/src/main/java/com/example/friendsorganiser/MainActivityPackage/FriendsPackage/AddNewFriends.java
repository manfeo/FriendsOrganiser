package com.example.friendsorganiser.MainActivityPackage.FriendsPackage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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
    private String currentUserId;
    private DatabaseReference databaseReference;
    private List<UserInfo> users;
    private List<UserInfo> friends;
    private FriendsAdapter friendsAdapter;
    private FragmentFriendBinding fragmentFriendBinding;

    public AddNewFriends(FriendsAdapter friendsAdapter, List<UserInfo> friends, FragmentFriendBinding fragmentFriendBinding){
        this.friendsAdapter = friendsAdapter;
        this.friends = friends;
        this.fragmentFriendBinding = fragmentFriendBinding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddNewFriendsDialogFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferenceManager preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getUsers();
        setBinding();
    }

    private void setBinding(){
        binding.btSendFriendsRequests.setOnClickListener(v -> {
            sendRequests();
        });
        binding.ibCancelAddNewFriends.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void sendRequests(){
        loadingSendFriendsRequests(true);

        boolean addedNewFriend = false;
        for (UserInfo user : users){
            if (user.getIsChecked()){
                String anotherUserId = user.getId();
                databaseReference.child(Constants.KEY_DATABASE_USERS).child(currentUserId).
                        child(Constants.KEY_FRIENDS).push().setValue(anotherUserId);
                databaseReference.child(Constants.KEY_DATABASE_USERS).child(anotherUserId).
                        child(Constants.KEY_FRIENDS).push().setValue(currentUserId);
                friends.add(user);
                addedNewFriend = true;
            }
        }
        if (addedNewFriend)
            fragmentFriendBinding.tvEmptyFriendsList.setVisibility(View.INVISIBLE);
        friendsAdapter.notifyDataSetChanged();

        loadingSendFriendsRequests(false);
        dismiss();
    }

    private void getUsers(){
        loadingListOfUsers(true);
        databaseReference.child(Constants.KEY_DATABASE_USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadUsers(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsers(DataSnapshot snapshot){
        users = new ArrayList<>();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
            if (currentUserId.equals(dataSnapshot.getKey()) || areFriendsAlready(dataSnapshot))
                continue;
            String name = dataSnapshot.child(Constants.KEY_NAME).getValue().toString();
            String surname = dataSnapshot.child(Constants.KEY_SURNAME).getValue().toString();
            String image = "";
            if (dataSnapshot.hasChild(Constants.KEY_IMAGE)) {
                image = dataSnapshot.child(Constants.KEY_IMAGE).getValue().toString();
            }
            String token = "";
            if (dataSnapshot.hasChild(Constants.KEY_FCM_TOKEN)) {
                token = dataSnapshot.child(Constants.KEY_FCM_TOKEN).getValue().toString();
            }
            String id = dataSnapshot.getKey();
            UserInfo anotherUser = new UserInfo(name, surname, image, token, id);
            users.add(anotherUser);
        }
        loadingListOfUsers(false);
        if (users.size() > 0) {
            AddNewFriendsAdapter addNewFriendsAdapter = new AddNewFriendsAdapter(users);
            binding.rvAllUsers.setAdapter(addNewFriendsAdapter);
            binding.rvAllUsers.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoUsers.setVisibility(View.VISIBLE);
        }
    }

    private boolean areFriendsAlready(DataSnapshot snapshot){
        if (snapshot.hasChild(Constants.KEY_FRIENDS)) {
            for (DataSnapshot dataSnapshot : snapshot.child(Constants.KEY_FRIENDS).getChildren()) {
                if (dataSnapshot.getValue().equals(currentUserId))
                    return true;
            }
        }
        return false;
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
}
