package com.example.dosribaar.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosribaar.Model.Story;
import com.example.dosribaar.Model.UserStory;
import com.example.dosribaar.R;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.StoryRvDesignBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdaptor extends RecyclerView.Adapter<StoryAdaptor.Viewholder> {

    ArrayList<Story> list;
    Context ctx;
    ArrayList<UserStory> userStoryArrayList = new ArrayList<>();

    public StoryAdaptor(ArrayList<Story> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.story_rv_design, parent, false);

        return new Viewholder(view) {
            @Override
            public void setFixedHeight() {
                ViewGroup.LayoutParams parentParams = parent.getLayoutParams();
                parentParams.width =
                        ((RecyclerView) parent).computeHorizontalScrollRange()
                                + parent.getPaddingLeft()
                                + parent.getPaddingRight();
                parent.setLayoutParams(parentParams);
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Story story = list.get(position);
        holder.setFixedHeight();
        Picasso.get().load(story.getLatestImage()).into(holder.binding.postimage);

        FirebaseDatabase.getInstance().
                getReference().child("User").
                child(story.getStoryby()).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).into(holder.binding.profileImage);
                holder.binding.name.setText(user.getName());
                holder.binding.postimage
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseDatabase.getInstance().
                                getReference().child("User").
                                child(story.getStoryby()).child("Story").
                                child(story.getStoryby())
                                .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                userStoryArrayList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    UserStory userStory = dataSnapshot.getValue(UserStory.class);
                                    try {
                                        long time = story.getTime();
                                        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                                        Timestamp timestamp = new Timestamp(Long.parseLong(String.valueOf(time)));
                                        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
                                        Date d1 = sdformat.parse(String.valueOf(timestamp));
                                        Date d2 = sdformat.parse(String.valueOf(timestamp2));

                                        if (d1.compareTo(d2) < 0) {
                                            FirebaseDatabase.getInstance().getReference().child("User")
                                                    .child(story.getStoryby()).child("Story")
                                                    .child(story.getStoryby())
                                                    .removeValue();
                                        } else {
                                            userStoryArrayList.add(userStory);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                story.setUserStories(userStoryArrayList);
                                ArrayList<MyStory> myStories = new ArrayList<>();

                                for (UserStory stories : story.getUserStories()) {
                                    myStories.add(new MyStory(
                                            stories.getImage()
                                    ));
                                }

                                new StoryView.Builder(((AppCompatActivity) ctx).getSupportFragmentManager())
                                        .setStoriesList(myStories) // Required
                                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                        .setTitleText(user.getName()) // Default is Hidden
                                        .setSubtitleText("") // Default is Hidden
                                        .setTitleLogoUrl(user.getProfilePhoto()) // Default is Hidden
                                        .setStoryClickListeners(new StoryClickListeners() {
                                            @Override
                                            public void onDescriptionClickListener(int position) {}

                                            @Override
                                            public void onTitleIconClickListener(int position) {}
                                        })
                                        .build()
                                        .show();
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public abstract class Viewholder extends RecyclerView.ViewHolder {
        StoryRvDesignBinding binding;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            binding = StoryRvDesignBinding.bind(itemView);
        }
        public abstract void setFixedHeight();
    }

}
