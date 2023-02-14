package com.example.dosribaar.Chat;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosribaar.R;
import com.example.dosribaar.databinding.ChatAdaptorLayoutBinding;

import java.util.List;

public class ChatAdaptor extends RecyclerView.Adapter<ChatAdaptor.ViewHolder> {
    List<ChatList> chatList;
    String userMobile;
    Context context;

    public ChatAdaptor(List<ChatList> chatListList, Context context, String ID) {
        this.chatList = chatListList;
        this.context = context;
        this.userMobile = ID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_adaptor_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatList chatList2 = chatList.get(position);
        if (!chatList2.getMobile().equals(userMobile)) {
            holder.binding.oppolayout.setVisibility(View.VISIBLE);
            holder.binding.mylayout.setVisibility(View.GONE);
            holder.binding.oppoMessge.setText(chatList2.getMessgae());
            holder.binding.oppoMsgTime.setText(chatList2.getDate() + " " + chatList2.getTime());
        } else {
            holder.binding.oppolayout.setVisibility(View.GONE);
            holder.binding.mylayout.setVisibility(View.VISIBLE);
            holder.binding.myMessge.setText(chatList2.getMessgae());
            holder.binding.myMsgTime.setText(chatList2.getDate() + " " + chatList2.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateChatLists(List<ChatList> chatList) {
        this.chatList = chatList;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ChatAdaptorLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatAdaptorLayoutBinding.bind(itemView);
        }
    }
}
