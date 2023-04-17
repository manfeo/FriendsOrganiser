package com.example.friendsorganiser.MainActivityPackage.FriendsPackage;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.AddNewFriendsDialogFragmentBinding;
import com.example.friendsorganiser.databinding.ItemFriendBinding;
import com.example.friendsorganiser.databinding.ItemUserBinding;

import java.util.List;

public class AddNewFriendsAdapter extends RecyclerView.Adapter<AddNewFriendsAdapter.AddNewFriendsHolder>{

    private final List<UserInfo> users;
    public AddNewFriendsAdapter(List<UserInfo> users){
        this.users = users;
    }

    class AddNewFriendsHolder extends RecyclerView.ViewHolder{
        public ItemUserBinding binding;

        public AddNewFriendsHolder(ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            binding = itemUserBinding;
        }

        private void setBinding(UserInfo userInfo) {
            String fullName = userInfo.getName() + userInfo.getSurname();
            binding.tvUserName.setText(fullName);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AddNewFriendsHolder holder, int position) {
        UserInfo anotherUser = users.get(position);
        holder.setBinding(anotherUser);

        holder.binding.ctvUserChecked.setOnClickListener(v -> {
            if (anotherUser.getIsChecked()){
                anotherUser.setChecked(false);
                holder.binding.ctvUserChecked.setCheckMarkDrawable(R.drawable.checked);
            } else {
                anotherUser.setChecked(true);
                holder.binding.ctvUserChecked.setCheckMarkDrawable(R.drawable.check);
            }
        });
    }

    @NonNull
    @Override
    public AddNewFriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding itemUserBinding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AddNewFriendsHolder(itemUserBinding);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
