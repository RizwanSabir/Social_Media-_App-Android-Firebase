package com.example.dosribaar.Chat;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosribaar.R;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.MessagesAdaptorLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class messageAdaptor extends RecyclerView.Adapter<messageAdaptor.MyViewHolder> {


    List<messageList> messageLists;
    Context context;
    FirebaseDatabase database;
    DatabaseReference r1;
    ValueEventListener t1;

    public messageAdaptor(List<messageList> messageLists, Context context) {
        this.messageLists = messageLists;
        this.context = context;
    }

    @NonNull
    @Override
    public messageAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.messages_adaptor_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull messageAdaptor.MyViewHolder holder, int position) {
        messageList messageList = messageLists.get(position);

        database = FirebaseDatabase.getInstance();


        r1 = database.getReference().child("User").child(messageList.getName());

        t1 = r1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.binding.name.setText(user.getName());
                holder.binding.LastMessage.setText(messageList.getLastMessage() + "");
                Picasso.get().load(user.getProfilePhoto()).into(holder.binding.ProfileImage);


                if (messageList.getUnseenMessage() == 0) {
                    holder.binding.unSeenMessage.setVisibility(View.GONE);
                    holder.binding.unSeenMessage.setTextColor(Color.parseColor("#959595"));
                } else {
                    holder.binding.unSeenMessage.setVisibility(View.VISIBLE);
                    holder.binding.unSeenMessage.setText(messageList.getUnseenMessage() + "");
                    holder.binding.unSeenMessage.setTextColor(context.getResources().getColor(R.color.white));
                }


                holder.binding.RootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("PID", messageList.PID);
                        intent.putExtra("Ids", user.getId());
                        intent.putExtra("Name", user.getName());
                        intent.putExtra("Pic", user.getProfilePhoto());

                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateData(List<messageList> messageLists) {
        this.messageLists = messageLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messageLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        MessagesAdaptorLayoutBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MessagesAdaptorLayoutBinding.bind(itemView);
        }
    }

}
