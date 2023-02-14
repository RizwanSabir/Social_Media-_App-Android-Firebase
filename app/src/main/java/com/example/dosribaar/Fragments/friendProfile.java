package com.example.dosribaar.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dosribaar.Adaptor.FollowAdaptor;
import com.example.dosribaar.Adaptor.FriendProfileAdaptor;
import com.example.dosribaar.Chat.ChatActivity;
import com.example.dosribaar.Model.Follow;
import com.example.dosribaar.Model.Post;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.ActivityFriendProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class friendProfile extends AppCompatActivity {

    ActivityFriendProfileBinding binding;
    FirebaseDatabase database;

    ArrayList<Post> postLists;
    FriendProfileAdaptor friendProfileAdaptor;
    ArrayList<Follow> list;
    String Id;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        Id = getIntent().getStringExtra("Ids");
        String PID = getIntent().getStringExtra("PID");

        list = new ArrayList<>();

        FollowAdaptor friendAdaptor = new FollowAdaptor(list, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        binding.recy.setLayoutManager(linearLayoutManager);
        binding.recy.setAdapter(friendAdaptor);

        binding.uploadPostsRecy.setHasFixedSize(true);
        binding.uploadPostsRecy.setLayoutManager(new GridLayoutManager(this, 3));
        postLists = new ArrayList<>();
        friendProfileAdaptor = new FriendProfileAdaptor(this, postLists,Id);
        binding.uploadPostsRecy.setAdapter(friendProfileAdaptor);
        myPhotoUpload();

        binding.Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("PID", PID);
                intent.putExtra("Ids", Id);
                intent.putExtra("Name", user.getName());
                intent.putExtra("Pic", user.getProfilePhoto());
                startActivity(intent);
            }
        });


        database.getReference().child("User").child(Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);

                    Picasso.get().load(user.getCoverPhoto()).into(binding.coverPhoto);
                    Picasso.get().load(user.getProfilePhoto()).into(binding.Profile);

                    binding.followingsc.setText(user.getFollowingCount() + "");
                    binding.ProfileName.setText(user.getName());
                    binding.Idprofile.setText(user.getId());
                    binding.Followersc.setText(user.getFollowersCount() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("User").child(Id).child("Followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Follow follow = dataSnapshot.getValue(Follow.class);
                            list.add(follow);

                        }
                        friendAdaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void myPhotoUpload() {
        FirebaseDatabase.getInstance().getReference().child("Post").child(Id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postLists.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            post.setPostedBy(Id);
                            post.setPostId(dataSnapshot.getKey());
                            postLists.add(post);
                        }
                        Collections.reverse(postLists);
                        friendProfileAdaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

}