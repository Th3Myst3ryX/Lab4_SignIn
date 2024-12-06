package com.example.lab4_login.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab4_login.databinding.ItemContainerReceivedBinding;
import com.example.lab4_login.databinding.ItemContainerSendBinding;
import com.example.lab4_login.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Bitmap receiverProfileImage;

    private final List<ChatMessage> chatMessages;

    private final String sendId;

    public static final int VIEW_TYPE_SENT = 1;

    public static final int VIEW_TYPE_RECEIVED = 2;


    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage,String sendId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.sendId = sendId;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ItemContainerSendBinding
                    .inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else{
            return new ReceivedMessageViewHolder(ItemContainerReceivedBinding
                    .inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(getItemViewType(position) == VIEW_TYPE_SENT){
                ((SentMessageViewHolder)holder).setData(chatMessages.get(position));
            }else{
                ((ReceivedMessageViewHolder)holder)
                        .setData(chatMessages.get(position),receiverProfileImage);
            }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position){
        if(chatMessages.get(position).senderId.equals(sendId)){
            return VIEW_TYPE_SENT;
        }else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerSendBinding binding;

        public SentMessageViewHolder(ItemContainerSendBinding itemContainerSendBinding) {
            super(itemContainerSendBinding.getRoot());
            binding = itemContainerSendBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerReceivedBinding binding;
        public ReceivedMessageViewHolder(ItemContainerReceivedBinding itemContainerReceivedBinding) {
            super(itemContainerReceivedBinding.getRoot());
            binding = itemContainerReceivedBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            if(receiverProfileImage !=null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
        }

    }
}
