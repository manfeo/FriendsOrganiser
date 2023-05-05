package com.example.friendsorganiser.MainActivityPackage.ChatsPackage.Chatting;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendsorganiser.Models.ChatMessage;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.databinding.SingleTextFromMeBinding;
import com.example.friendsorganiser.databinding.SingleTextFromOthersBinding;

import java.util.List;

public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatMessages;
    private String senderId;

    public ChattingAdapter(List<ChatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    static class SendMessageHolder extends RecyclerView.ViewHolder{
        private SingleTextFromMeBinding binding;

        public SendMessageHolder(SingleTextFromMeBinding singleTextFromMeBinding){
            super(singleTextFromMeBinding.getRoot());
            binding = singleTextFromMeBinding;
        }

        private void setBinding(ChatMessage chatMessage){
            binding.tvMessageFromMe.setText(chatMessage.getMessage());
            binding.tvMessageFromMeTime.setText(chatMessage.getCreatedAt());
        }
    }

    static class ReceiveMessageHolder extends RecyclerView.ViewHolder{
        private SingleTextFromOthersBinding binding;

        public ReceiveMessageHolder(SingleTextFromOthersBinding singleTextFromOthersBinding){
            super(singleTextFromOthersBinding.getRoot());
            binding = singleTextFromOthersBinding;
        }

        private void setBinding(ChatMessage chatMessage){
            binding.tvMessageFromOthers.setText(chatMessage.getMessage());
            binding.tvMessageFromOtherTime.setText(chatMessage.getCreatedAt());
            binding.tvMessageSender.setText(chatMessage.getFullname());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constants.KEY_MESSAGE_FROM_ME){
            return new SendMessageHolder(
                    SingleTextFromMeBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceiveMessageHolder(
                    SingleTextFromOthersBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == Constants.KEY_MESSAGE_FROM_ME){
            ((SendMessageHolder) holder).setBinding(chatMessages.get(position));
        } else {
            ((ReceiveMessageHolder) holder).setBinding(chatMessages.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId().equals(senderId)){
            return Constants.KEY_MESSAGE_FROM_ME;
        } else {
            return Constants.KEY_MESSAGE_FROM_OTHERS;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
}
