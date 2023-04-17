package com.example.friendsorganiser.MainActivityPackage.FriendsPackage;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.databinding.ItemFriendBinding;
import com.example.friendsorganiser.databinding.ItemUserBinding;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsHolder> {

    private final List<UserInfo> friends;
    private OnFriendClickedListener onFriendClickedListener;

    public FriendsAdapter(List<UserInfo> friends, OnFriendClickedListener onFriendClickedListener){
        this.friends = friends;
        this.onFriendClickedListener = onFriendClickedListener;
    }

    class FriendsHolder extends RecyclerView.ViewHolder{
        ItemFriendBinding binding;

        public FriendsHolder(ItemFriendBinding itemFriendBinding) {
            super(itemFriendBinding.getRoot());
            binding = itemFriendBinding;
        }

        private void setBinding(UserInfo userInfo) {
            String fullName = userInfo.getName() + userInfo.getSurname();
            binding.tvFriendName.setText(fullName);
            binding.getRoot().setOnClickListener(v -> {
                String friendId = userInfo.getId();
                onFriendClickedListener.onFriendCLicked(friendId);
            });
        }
    }

    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendBinding itemFriendBinding = ItemFriendBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new FriendsHolder(itemFriendBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {
        holder.setBinding(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}
