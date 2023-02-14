package com.example.dosribaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dosribaar.Adaptor.CommentAdaptor;
import com.example.dosribaar.Model.Notification;
import com.example.dosribaar.Model.Post;
import com.example.dosribaar.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding binding;
    Intent intent;
    String postId;
    String postedBy;

    ArrayList<Comment> commentList = new ArrayList<>();

    FirebaseDatabase database;
    FirebaseAuth auth;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        intent = getIntent();
        ID = intent.getStringExtra("ID");
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");


        database.getReference().child("Post").child(postedBy).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Post post = snapshot.getValue(Post.class);
                Picasso.get().load(post.getPostImage()).placeholder(R.drawable.loading).into(binding.postimage);

                DatabaseReference rf2 = FirebaseDatabase.getInstance().getReference().child("Post").child(postedBy).child(postId).child("Like").child(FirebaseAuth.getInstance().getUid());

                rf2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons8_heart_puzzle_24, 0, 0, 0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                binding.like.setText(post.getPostlike() + "");
                binding.comment.setText(post.getCommentCount() + "");

                String description = post.getPostDescription();

                if (description == null || description.equals("")) {
                    binding.description.setVisibility(View.GONE);
                } else {
                    binding.description.setText(post.getPostDescription());
                    binding.description.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        database.getReference().child("User").child(postedBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).into(binding.profileimage);
                binding.Name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        binding.commentPostBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Comment comment = new Comment();
                comment.setCommentAt(new Date().getTime());
                comment.setCommentBody(binding.commentEt.getText().toString());
                comment.setCommentBy(ID);


                database.getReference().child("Post").child(postedBy).child(postId).child("Comment").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("Post").child(postedBy).child(postId).child("CommentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int CommentCount = 0;
                                if (snapshot.exists()) {
                                    CommentCount = snapshot.getValue(Integer.class);

                                }
                                database.getReference().child("Post").child(postedBy).child(postId).child("CommentCount").setValue(CommentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        binding.commentEt.setText("");
                                        Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();

                                        Notification notification = new Notification();
                                        notification.setNotfby(ID);
                                        notification.setNotfAt(new Date().getTime());
                                        notification.setPostId(postId);
                                        notification.setPostBy(postedBy);
                                        notification.setCheckOpen(false);
                                        notification.setType("Comment");


                                        FirebaseDatabase.getInstance().getReference().child("Notf").child(postedBy).push().setValue(notification);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });
            }
        });

        CommentAdaptor commentAdaptor = new CommentAdaptor(commentList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.commentRv.setLayoutManager(linearLayoutManager);
        binding.commentRv.setAdapter(commentAdaptor);

        database.getReference().child("Post").child(postedBy).child(postId).child("Comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}