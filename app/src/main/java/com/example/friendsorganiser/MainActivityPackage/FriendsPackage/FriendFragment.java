package com.example.friendsorganiser.MainActivityPackage.FriendsPackage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class FriendFragment extends Fragment implements OnFriendClickedListener{
    private FragmentFriendBinding binding;
    private String currentUserId;
    private DatabaseReference databaseReference;
    private FriendsAdapter friendsAdapter;
    private List<UserInfo> friends;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFriends();
        setBinding();
    }

    private void setBinding(){
        binding.fbAddNewFriend.setOnClickListener(v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            AddNewFriends addNewFriends = new AddNewFriends(friendsAdapter, friends, binding);
            addNewFriends.show(manager, "addNewFriendsDialogFragment");
        });
    }

    private void getFriends(){
        loading(true);
        databaseReference.child(Constants.KEY_DATABASE_USERS).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadFriends(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFriends(DataSnapshot friendsDataSnapshot){
        friends = new ArrayList<>();

        if (friendsDataSnapshot.child(currentUserId).hasChild(Constants.KEY_FRIENDS)) {

            //Looping through list of IDs of current user friends list
            for (DataSnapshot anotherSnapshot : friendsDataSnapshot.child(currentUserId).
                    child(Constants.KEY_FRIENDS).getChildren()) {

                String anotherFriendId = anotherSnapshot.getValue().toString();
                if (currentUserId.equals(anotherFriendId))
                    continue;

                String name = friendsDataSnapshot.child(anotherFriendId).child(Constants.KEY_NAME).getValue().toString();
                String surname = friendsDataSnapshot.child(anotherFriendId).child(Constants.KEY_SURNAME).getValue().toString();
                String image = "";
                if (friendsDataSnapshot.child(anotherFriendId).hasChild(Constants.KEY_IMAGE)) {
                    image = friendsDataSnapshot.child(anotherFriendId).child(Constants.KEY_IMAGE).getValue().toString();
                }
                String token = "";
                if (friendsDataSnapshot.child(anotherFriendId).hasChild(Constants.KEY_FCM_TOKEN)) {
                    token = friendsDataSnapshot.child(anotherFriendId).child(Constants.KEY_FCM_TOKEN).getValue().toString();
                }
                String id = friendsDataSnapshot.child(anotherFriendId).getKey();
                UserInfo anotherUser = new UserInfo(name, surname, "", token, id);
                friends.add(anotherUser);
            }
        }
        loading(false);
        friendsAdapter = new FriendsAdapter(friends, this);
        if (friends.size() > 0) {
            binding.rvAllFriendsDisplay.setAdapter(friendsAdapter);
            binding.rvAllFriendsDisplay.setVisibility(View.VISIBLE);
            binding.fbAddNewFriend.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmptyFriendsList.setVisibility(View.VISIBLE);
            binding.fbAddNewFriend.setVisibility(View.VISIBLE);
        }
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