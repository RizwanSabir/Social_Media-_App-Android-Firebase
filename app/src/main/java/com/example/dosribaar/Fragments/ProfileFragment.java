package com.example.dosribaar.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dosribaar.Adaptor.FollowAdaptor;
import com.example.dosribaar.Adaptor.FriendProfileAdaptor;
import com.example.dosribaar.LoginActivity;
import com.example.dosribaar.Model.Follow;
import com.example.dosribaar.Model.Post;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;


public class ProfileFragment extends Fragment {


    static String DATA_RECEIVE1;
    FragmentProfileBinding binding;
    ArrayList<Post> postLists;
    FriendProfileAdaptor friendProfileAdaptor;

    ArrayList<Follow> list;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    String ID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ID = getArguments().getString("ID");
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.uploadPostsRecy.setNestedScrollingEnabled(false);

        database.getReference().child("User").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    Picasso.get().load(user.getCoverPhoto()).into(binding.coverPhoto);
                    Picasso.get().load(user.getProfilePhoto()).into(binding.Profile);

                    binding.followingsc.setText(user.getFollowingCount() + "");
                    binding.ProfileName.setText(user.getName().toString());
                    binding.Idprofile.setText(user.getId().toString());
                    binding.Followersc.setText(user.getFollowersCount() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        list = new ArrayList<>();

        FollowAdaptor friendAdaptor = new FollowAdaptor(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.recy.setLayoutManager(linearLayoutManager);

        database.getReference().child("User").child(ID).child("Followers").addValueEventListener(new ValueEventListener() {
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

        binding.changeCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });

        binding.Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        binding.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String EditTextName = binding.EditText.getText().toString();

                if (EditTextName.equals("Edit")) {
                    binding.EditText.setText("Save");
                    binding.Bio.setFocusable(true);
                    binding.Bio.setEnabled(true);
                    binding.Bio.setCursorVisible(true);

                } else {
                    String bio = binding.Bio.getText().toString();
                    if (!(bio.equals(""))) {
                        database.getReference().child("User").child(ID).child("Bio").setValue(bio).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Updated Bio", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    binding.EditText.setText("Edit");
                    binding.Bio.setFocusable(false);
                    binding.Bio.setEnabled(false);
                    binding.Bio.setCursorVisible(false);
                    binding.Bio.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });


        binding.uploadPostsRecy.setHasFixedSize(true);
        binding.uploadPostsRecy.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postLists = new ArrayList<>();
        friendProfileAdaptor = new FriendProfileAdaptor(getContext(), postLists,ID);
        binding.uploadPostsRecy.setAdapter(friendProfileAdaptor);
        myPhotoUpload();

        return binding.getRoot();
    }

    private void myPhotoUpload() {

        FirebaseDatabase.getInstance().getReference().child("Post").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();

                int PostCount = (int) snapshot.getChildrenCount();
                binding.textView10.setText(PostCount + "");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostedBy(ID);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 11) {
                Uri uri = data.getData();
                binding.coverPhoto.setImageURI(uri);
                final StorageReference storageReference = storage.getReference().child("CoverPhoto").child(ID);
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Cover Photo Upload", Toast.LENGTH_SHORT).show();
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("User").child(ID).child("CoverPhoto").setValue(uri.toString());
                            }
                        });
                    }
                });


            }


            if (requestCode == 10) {
                Uri uri = data.getData();
                binding.Profile.setImageURI(uri);
                final StorageReference storageReference = storage.getReference().child("ProfilePhoto").child(ID);
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Profile Photo Upload", Toast.LENGTH_SHORT).show();
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("User").child(ID).child("ProfilePhoto").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }

    }
}