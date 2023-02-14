package com.example.dosribaar.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosribaar.Model.Follow;
import com.example.dosribaar.R;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.FriendRvSampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowAdaptor extends RecyclerView.Adapter<FollowAdaptor.viewholder> {

    ArrayList<Follow> list;
    Context ctx;


    public FollowAdaptor(ArrayList<Follow> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.friend_rv_sample, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Follow model = list.get(position);

        FirebaseDatabase.getInstance().getReference().child("User").child(model.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.loading)
                        .into(holder.binding.profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        FriendRvSampleBinding binding;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            binding = FriendRvSampleBinding.bind(itemView);
        }
    }
}

