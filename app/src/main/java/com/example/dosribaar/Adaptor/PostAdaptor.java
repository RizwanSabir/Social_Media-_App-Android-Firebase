package com.example.dosribaar.Adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosribaar.CommentActivity;
import com.example.dosribaar.Fragments.HomeFragment;
import com.example.dosribaar.Fragments.friendProfile;
import com.example.dosribaar.Model.Notification;
import com.example.dosribaar.Model.Post;
import com.example.dosribaar.OpenImage;
import com.example.dosribaar.R;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.DashboardRvSampleBinding;
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

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.viewHolder> {

    ArrayList<Post> list;
    Context ctx;
    Intent intent = new Intent();
    ValueEventListener v1;
    String IdMade;
    boolean CheckedLike = true;

    public PostAdaptor(ArrayList<Post> list, Context ctx, String IdMade) {
        this.list = list;
        this.ctx = ctx;
        this.IdMade = IdMade;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.dashboard_rv_sample, parent, false);
        return new viewHolder(view) {


        };
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


        Post dasBoardModel = list.get(position);
        Picasso.get().load(dasBoardModel.getPostImage()).placeholder(R.drawable.loading)
                .into(holder.binding.postimage);

        holder.binding.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ctx, OpenImage.class);
                intent.putExtra("i", dasBoardModel.getPostImage());
                view.getContext().startActivity(intent);
            }
        });

        String description = dasBoardModel.getPostDescription();
        if (description == null || description.equals("")) {
            holder.binding.postdescription.setVisibility(View.GONE);
        } else {
            holder.binding.postdescription.setText(dasBoardModel.getPostDescription());
            holder.binding.postdescription.setVisibility(View.VISIBLE);
        }

        DatabaseReference rf1 = FirebaseDatabase.getInstance().getReference().child("User")
                .child(dasBoardModel.getPostedBy());
        v1 = rf1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.loading).into(holder.binding.profileImage);

                holder.binding.username.setText(user.getName());
                holder.binding.about.setText(user.getId());
                if (list.size() == position - 1) {
                    rf1.removeEventListener(v1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference rf2 = FirebaseDatabase.getInstance().getReference()
                .child("Post").child(dasBoardModel.getPostedBy()).child(dasBoardModel.getPostId()).child("Like")
                .child(FirebaseAuth.getInstance().getUid());
        rf2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    holder.binding.Like.
                            setCompoundDrawablesWithIntrinsicBounds
                                    (R.drawable.icons8_heart_puzzle_24,
                                            0, 0, 0);

                } else {
                    holder.binding.Like
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.
                                            getInstance().
                                            getReference().child("Post").
                                            child(dasBoardModel.getPostedBy()).
                                            child(dasBoardModel.getPostId())
                                            .child("Like").
                                            child(FirebaseAuth.getInstance().
                                                    getUid()).setValue(true).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().
                                                            getReference().child("Post").
                                                            child(dasBoardModel.getPostedBy())
                                                            .child(dasBoardModel.getPostId())
                                                            .child("postlike").
                                                            setValue(dasBoardModel.getPostlike() + 1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    int like;
                                                                    if (holder.binding.Like.
                                                                            getText().toString().equals("")) {
                                                                        like = 0;
                                                                    } else {
                                                                        like = Integer.parseInt(holder.
                                                                                binding.Like.getText().toString());
                                                                    }

                                                                    if (CheckedLike) {
                                                                        CheckedLike = false;
                                                                    }


                                                                    holder.binding.Like.
                                                                            setCompoundDrawablesWithIntrinsicBounds
                                                                                    (R.drawable.icons8_heart_puzzle_24, 0, 0, 0);


                                                                    Notification notification = new Notification();
                                                                    notification.setNotfby(IdMade);
                                                                    notification.setNotfAt(new Date().getTime());
                                                                    notification.setPostId(dasBoardModel.getPostId());
                                                                    notification.setPostBy(dasBoardModel.getPostedBy());
                                                                    notification.setCheckOpen(false);
                                                                    notification.setType("Like");


                                                                    FirebaseDatabase.getInstance().getReference().child("Notf")
                                                                            .child(dasBoardModel.getPostedBy()).
                                                                            push().setValue(notification);
                                                                }
                                                            });

                                                }
                                            });
                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.binding.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, friendProfile.class);
                intent.putExtra("PID", IdMade);
                intent.putExtra("Ids", dasBoardModel.getPostedBy());
                ctx.startActivity(intent);

            }
        });

        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, CommentActivity.class);
                intent.putExtra("ID", IdMade);
                intent.putExtra("postId", dasBoardModel.getPostId());
                intent.putExtra("postedBy", dasBoardModel.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public abstract class viewHolder extends RecyclerView.ViewHolder {
        DashboardRvSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DashboardRvSampleBinding.bind(itemView);
        }

;
    }
}
