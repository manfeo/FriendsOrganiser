package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.NewChatDialog;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendsorganiser.Models.UserInfo;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.ItemUserBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsPickerAdapter extends RecyclerView.Adapter<FriendsPickerAdapter.FriendsPickerHolder> {
    private List<UserInfo> friends;

    public FriendsPickerAdapter(List<UserInfo> friends) {
        this.friends = friends;
    }

    class FriendsPickerHolder extends RecyclerView.ViewHolder{

        public ItemUserBinding binding;

        public FriendsPickerHolder(ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            binding = itemUserBinding;
        }

        private void setBinding(UserInfo userInfo){
            String fullName = userInfo.getName() + " " + userInfo.getSurname();
            binding.tvUserName.setText(fullName);
            Uri userPhoto = userInfo.getPhoto();
            if (userPhoto != null)
                Picasso.get().load(userPhoto).noFade().into(binding.ivUserPhoto);
        }
    }

    @NonNull
    @Override
    public FriendsPickerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding itemUserBinding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new FriendsPickerHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsPickerHolder holder, int position) {
        UserInfo anotherFriend = friends.get(position);
        holder.setBinding(anotherFriend);

        holder.binding.ctvUserChecked.setOnClickListener(v -> {

            if (anotherFriend.getIsChecked()){
                anotherFriend.setChecked(false);
                holder.binding.ctvUserChecked.setCheckMarkDrawable(R.drawable.checked);

            } else {
                anotherFriend.setChecked(true);
                holder.binding.ctvUserChecked.setCheckMarkDrawable(R.drawable.check);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
