package com.example.friendsorganiser.MainActivityPackage.ChatsPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendsorganiser.Models.RecentChat;
import com.example.friendsorganiser.databinding.ItemRecentConversationBinding;

import java.util.List;
import java.util.Objects;

public class RecentChatsAdapter extends RecyclerView.Adapter<RecentChatsAdapter.AllChatsHolder> {

    private List<RecentChat> recentChatList;
    private OnChatClickedListener onChatClickedListener;

    public RecentChatsAdapter(List<RecentChat> recentChatList, OnChatClickedListener onChatClickedListener){
        this.recentChatList = recentChatList;
        this.onChatClickedListener = onChatClickedListener;
    }

    class AllChatsHolder extends RecyclerView.ViewHolder{
        public ItemRecentConversationBinding binding;

        public AllChatsHolder(ItemRecentConversationBinding itemRecentConversationBinding){
            super(itemRecentConversationBinding.getRoot());
            binding = itemRecentConversationBinding;
        }

        private void setBinding(RecentChat recentChat){
            binding.tvChatName.setText(recentChat.getChatName());
            binding.tvChatLastMessage.setText(recentChat.getLastMessage());
            if (recentChat.getMessageSentTime().equals("0"))
                binding.tvChatSentTime.setText(null);
            else
                binding.tvChatSentTime.setText(recentChat.getMessageSentTime());
            binding.getRoot().setOnClickListener(v -> {
                String recentChatId = recentChat.getChatId();
                onChatClickedListener.onChatCLicked(recentChatId);
            });
        }
    }

    @NonNull
    @Override
    public AllChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AllChatsHolder(
                ItemRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AllChatsHolder holder, int position) {
        holder.setBinding(recentChatList.get(position));
    }

    @Override
    public int getItemCount() {
        return recentChatList.size();
    }
}
