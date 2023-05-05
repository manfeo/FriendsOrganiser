package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.AllChats;

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

import com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting.ChattingActivity;
import com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog.NewChat;
import com.example.friendsorganiser.Models.RecentChat;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.FragmentChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecentChats extends Fragment implements OnChatClickedListener {
    private FragmentChatBinding binding;
    private RecentChatsViewModel recentChatsViewModel;
    private RecentChatsAdapter recentChatsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recentChatsViewModel = new ViewModelProvider(requireActivity()).get(RecentChatsViewModel.class);
        recentChatsViewModel.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setListeners();
    }

    private void init(){
        recentChatsAdapter = new RecentChatsAdapter(recentChatsViewModel.getRecentChats().getValue(), this);
        binding.rvAllChatsDisplay.setAdapter(recentChatsAdapter);
    }

    private void setListeners() {
        binding.fbAddNewChat.setOnClickListener(v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            NewChat newChat = new NewChat();
            newChat.show(manager, "newChatCreation");
        });

        recentChatsViewModel.getRecentChats().observe(getViewLifecycleOwner(), recentChatList -> {
            if (recentChatList.size() == 0){
                binding.tvEmptyChatsList.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyChatsList.setVisibility(View.INVISIBLE);
                recentChatsAdapter.notifyDataSetChanged();
                binding.rvAllChatsDisplay.post(() -> binding.rvAllChatsDisplay.smoothScrollToPosition(0));
                binding.rvAllChatsDisplay.setVisibility(View.VISIBLE);
            }
            binding.pbLoadingAllChats.setVisibility(View.GONE);
            binding.fbAddNewChat.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onChatCLicked(String recentChatId) {
        Intent openChatIntent = new Intent(getActivity(), ChattingActivity.class);
        openChatIntent.putExtra(Constants.KEY_CHAT_ID, recentChatId);
        startActivity(openChatIntent);
    }
}