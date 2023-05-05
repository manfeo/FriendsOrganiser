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
    public List<UserInfo> friends;
    private CreateNewChatBinding binding;
    private DatabaseReference databaseReference;
    private String currentUserId;
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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        PreferenceManager preferenceManager = PreferenceManager.getInstance();
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        setListeners();
        getFriends();
    }

    private void setListeners() {
        binding.btNewChatCreate.setOnClickListener(v -> openCreatedChat());
        binding.ibNewChatCloseDialog.setOnClickListener(v -> dismiss());
    }

    private void openCreatedChat() {
        ArrayList<String> chatParticipants = new ArrayList<>();
        for (UserInfo friend : friends){
            if (friend.getIsChecked())
                chatParticipants.add(friend.getId());
        }
        chatParticipants.add(currentUserId);

        String chatName = binding.etNewChatName.getText().toString();

        HashMap<String, Object> newChatInfo = new HashMap<>();
        newChatInfo.put(Constants.KEY_CHAT_NAME, chatName);
        newChatInfo.put(Constants.KEY_CHAT_PARTICIPANTS, chatParticipants);

        String chatId = databaseReference.child(Constants.KEY_DATABASE_CHATS).push().getKey();

        HashMap<String, Object> newChatInfoForParticipants = new HashMap<>();
        newChatInfoForParticipants.put(Constants.KEY_CHAT_NAME, chatName);
        newChatInfoForParticipants.put(Constants.KEY_TIMESTAMP, 0);
        newChatInfoForParticipants.put(Constants.KEY_LAST_MESSAGE, "");

        chatParticipants.forEach((currentParticipantId) -> databaseReference.child(Constants.KEY_DATABASE_USERS).
                child(currentParticipantId).child(Constants.KEY_RECENT_CHATS).child(chatId).setValue(newChatInfoForParticipants));

        databaseReference.child(Constants.KEY_DATABASE_CHATS).child(chatId).setValue(newChatInfo).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "New chat created", Toast.LENGTH_SHORT).show();
                        loadChattingActivity(chatId);
                    } else {
                        Toast.makeText(getContext(), "Unable to create chat", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadChattingActivity(String chatId){
        Intent chat = new Intent(getActivity(), ChattingActivity.class);
        chat.putExtra(Constants.KEY_CHAT_ID, chatId);
        startActivity(chat);
        dismiss();
    }

    private void getFriends() {
        loading(true);
        databaseReference.child(Constants.KEY_DATABASE_USERS).addValueEventListener(new ValueEventListener() {
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
        friendsPickerAdapter = new FriendsPickerAdapter(friends);
        if (friends.size() > 0) {
            binding.rvNewChatDisplayFriends.setAdapter(friendsPickerAdapter);
            binding.rvNewChatDisplayFriends.setVisibility(View.VISIBLE);
        } else {
            binding.tvNewChatNoFriends.setVisibility(View.VISIBLE);
        }
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
