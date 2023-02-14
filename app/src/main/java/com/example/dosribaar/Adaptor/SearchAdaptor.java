package com.example.dosribaar.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosribaar.Fragments.friendProfile;
import com.example.dosribaar.Model.Follow;
import com.example.dosribaar.Model.Notification;
import com.example.dosribaar.R;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.UserSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.viewHolder> {

    Context context;
    ArrayList<User> list;
    String Id;

    public SearchAdaptor(Context context, ArrayList<User> users, String Id) {
        this.context = context;
        this.list = users;
        this.Id = Id;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = list.get(position);
        Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.loading).into(holder.binding.profileimage);
        holder.binding.Pname.setText(user.getName());
        holder.binding.ProfileId.setText(user.getId());


        FirebaseDatabase.getInstance().getReference()
                .child("User").child(user.getUserId()).
                child("Followers").child(Id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.FollowBtn.
                                    setBackgroundDrawable(ContextCompat.getDrawable(context,
                                            R.drawable.follow_active_btn));
                            holder.binding.FollowBtn.setText("Following");
                            holder.binding.FollowBtn.setTextColor(context.getResources().
                                    getColor(R.color.black));
                            holder.binding.FollowBtn.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.binding.Pname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, friendProfile.class);
                intent.putExtra("PID", Id);
                intent.putExtra("Ids", user.getUserId());
                context.startActivity(intent);

            }
        });


        holder.binding.FollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Follow follow = new Follow();
                follow.setFollowedBy(Id);
                follow.setFollowedAt(new Date().getTime());
                FirebaseDatabase.getInstance().getReference()
                        .child("User").child(user.getUserId()).child("Followers")
                        .child(Id).setValue(follow).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child("User").child(Id).child("followingCount");
                                FirebaseDatabase.getInstance().getReference()
                                        .child("User").child(Id).child("Following").child(user.getUserId()).setValue(user.getUserId());

                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int count = snapshot.getValue(Integer.class);
                                        reference.setValue(count + 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                                FirebaseDatabase.getInstance().getReference()
                                        .child("User").child(user.getUserId()).child("followersCount")
                                        .setValue(user.getFollowersCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                holder.binding.FollowBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                                                holder.binding.FollowBtn.setText("Following");
                                                holder.binding.FollowBtn.setTextColor(context.getResources().getColor(R.color.black));
                                                holder.binding.FollowBtn.setEnabled(false);
                                                Toast.makeText(view.getContext(), "You Followed " + user.getName(), Toast.LENGTH_SHORT).show();

                                                Notification notification = new Notification();
                                                notification.setNotfby(Id);
                                                notification.setNotfAt(new Date().getTime());
                                                notification.setCheckOpen(false);
                                                notification.setType("Follow");


                                                FirebaseDatabase.getInstance().getReference().child("Notf").child(user.getUserId()).
                                                        push().setValue(notification);
                                            }
                                        });
                            }
                        });
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        UserSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = UserSampleBinding.bind(itemView);
        }
    }


}
