package com.example.dosribaar.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.dosribaar.Adaptor.PostAdaptor;
import com.example.dosribaar.Adaptor.StoryAdaptor;
import com.example.dosribaar.Chat.MessageActivity;
import com.example.dosribaar.Model.Follow;
import com.example.dosribaar.Model.Post;
import com.example.dosribaar.Model.Story;
import com.example.dosribaar.Model.UserStory;
import com.example.dosribaar.PostClassQuery;
import com.example.dosribaar.R;
import com.example.dosribaar.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    ShimmerRecyclerView DashBoardRecyclerView;
    ArrayList<Story> StoryList;
    ArrayList<Post> PostList;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    ImageView addStoryImage;
    ActivityResultLauncher<String> galleryLauncher;
    ProgressDialog dialogBox;
    String ID;
    PostAdaptor DashBoardAdaptor;
    String Key = null;
    boolean FirstTimeRun = false;
    ValueEventListener valueEventStory1;
    DatabaseReference rStory3;
    ValueEventListener vStory3;
    ImageView profile_image;

    Task<Void> TaskStory2;

    ImageView postImage;
    boolean loading;
    NestedScrollView nestedScrollView;
    ValueEventListener v1;
    PostClassQuery postClassQuery;
    ArrayList<Post> reverseListPost;
    int length;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogBox = new ProgressDialog(getContext());
        ID = getArguments().getString("ID");
        ID = getArguments().getString("ID");
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        DashBoardRecyclerView = view.findViewById(R.id.dasborard);
        DashBoardRecyclerView.showShimmerAdapter();

        postImage = view.findViewById(R.id.postimage);
        nestedScrollView = view.findViewById(R.id.nested);

        recyclerView = view.findViewById(R.id.storyRv);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        StoryList = new ArrayList<>();

        profile_image = view.findViewById(R.id.profile_image);

        dialogBox.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogBox.setTitle("Story Uploading");
        dialogBox.setMessage("Please Wait...");
        dialogBox.setCancelable(false);

        StoryAdaptor storyAdaptor = new StoryAdaptor(StoryList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(storyAdaptor);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("Ids", ID);
                startActivity(intent);
            }
        });

        database.getReference().child("User").child(ID).child("StoryFollowing").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {

                    StoryList.clear();
                    System.out.println("children count is "+task.getResult().getChildrenCount());
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        Story story = dataSnapshot.getValue(Story.class);
                        try {
                            long time = story.getTime();
                            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                            Timestamp timestamp = new Timestamp(Long.parseLong(String.valueOf(time)));
                            Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
                            Date d1 = sdformat.parse(String.valueOf(timestamp));
                            Date d2 = sdformat.parse(String.valueOf(timestamp2));

                            if (d1.compareTo(d2) < 0) {

                                database.getReference().child("User").child(ID).child("StoryFollowing").child(dataSnapshot.getKey()).removeValue();
                            } else {
                                StoryList.add(story);
                            }
                            System.out.println("ok done");

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    storyAdaptor.notifyDataSetChanged();
                }
            }
        });


        reverseListPost = new ArrayList<>();
        PostList = new ArrayList<>();


        DashBoardAdaptor = new PostAdaptor(PostList, getContext(), ID);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        DashBoardRecyclerView.setLayoutManager(layoutManager1);
        DashBoardRecyclerView.setNestedScrollingEnabled(false);

        postClassQuery = new PostClassQuery(ID);
        FirstTimeRun = true;
        PostList.clear();
        Key = null;

        reverseListPost.clear();
        LoadPost();

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                    LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();

                    int totalitem = linearLayoutManager1.getItemCount();
                    int lastvisible = linearLayoutManager1.findLastCompletelyVisibleItemPosition();

                    if (totalitem < lastvisible + 3) {
                        if (!loading) {
                            loading = true;
                            LoadPost();
                        }
                    }
                }
            }
        });



        FirebaseDatabase.getInstance().getReference().child("User").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addStoryImage = view.findViewById(R.id.postimage);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLauncher.launch("image/*");
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                addStoryImage.setImageURI(result);
                dialogBox.show();

                StorageReference storageReference = storage.getReference().child("Story").child(ID).child(new Date().getTime() + "");
                storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override
                            public void onSuccess(Uri uri) {

                                UserStory userStory = new UserStory(uri.toString(), new Date().getTime());
                                Story story = new Story(ID, uri.toString(), System.currentTimeMillis());

                                DatabaseReference rs1 = database.getReference().child("User").child(ID).child("Story").child(ID);
                                valueEventStory1 = rs1.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        length = (int) snapshot.getChildrenCount();
                                        DatabaseReference rs2 = database.getReference().child("User").child(ID).child("Story").child(ID).child(ID + "-" + (length + 1));

                                        rs1.removeEventListener(valueEventStory1);

                                        TaskStory2 = rs2.setValue(userStory).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                rStory3 = database.getReference().child("User").child(ID).child("Followers");
                                                vStory3 = rStory3.addValueEventListener(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            Follow follow = dataSnapshot.getValue(Follow.class);
                                                            database.getReference().child("User").child(follow.getFollowedBy()).child("StoryFollowing").child(ID).setValue(story);
                                                        }
                                                        database.getReference().child("User").child(ID).child("StoryFollowing").child(ID).setValue(story);
                                                        Toast.makeText(getContext(), "Story uploaded Successfull", Toast.LENGTH_SHORT).show();
                                                        dialogBox.dismiss();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });
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
            }
        });
        return view;
    }

    private void LoadPost() {
        ArrayList<Post> ReverseList = new ArrayList<>();

        v1 = postClassQuery.get(Key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    Key = dataSnapshot.getKey();
                    ReverseList.add(post);
                }
                Collections.reverse(ReverseList);
                PostList.addAll(ReverseList);
                DashBoardRecyclerView.setAdapter(DashBoardAdaptor);
                loading = false;
                DashBoardAdaptor.notifyDataSetChanged();
                DashBoardRecyclerView.hideShimmerAdapter();
                postClassQuery.get(Key).removeEventListener(v1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}