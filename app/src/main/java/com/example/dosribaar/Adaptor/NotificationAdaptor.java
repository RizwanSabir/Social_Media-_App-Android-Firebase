package com.example.dosribaar.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosribaar.CommentActivity;
import com.example.dosribaar.Model.Notification;
import com.example.dosribaar.R;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.Notification2sampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class NotificationAdaptor extends RecyclerView.Adapter<NotificationAdaptor.viewHolder> {
    ArrayList<Notification> list;
    Context cx;

    public NotificationAdaptor(ArrayList<Notification> list, Context cx) {
        this.list = list;
        this.cx = cx;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cx).inflate(R.layout.notification2sample, parent, false);

        return new viewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Notification model = list.get(position);
        String Type = model.getType();
        FirebaseDatabase.getInstance().getReference().child("User").
                child(model.getNotfby()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfilePhoto()).into(holder.binding.profileImage);

                        if (Type.equals("Like")) {
                            holder.binding.Notification.
                                    setText(Html.fromHtml("<b>" +
                                            user.getName() + "</b>" +
                                            "Liked Your Post"));
                        } else if (Type.equals("Comment")) {
                            holder.binding.Notification.setText(Html.
                                    fromHtml("<b>" +
                                            user.getName() + "</b>" +
                                            "Comment on your Post"));
                        } else {
                            holder.binding.Notification.
                                    setText(Html.fromHtml("<b>" +
                                            user.getName() + "</b>" + "" +
                                            "Started following you"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.openNotf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(Type.equals("Follow"))) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Notf").child(model.getPostBy()).
                            child(model.getNotfId())
                            .child("checkOpen").setValue(true);
                    Intent intent = new Intent(cx, CommentActivity.class);
                    intent.putExtra("postId", model.getPostId());
                    intent.putExtra("postedBy", model.getPostBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    cx.startActivity(intent);
                }
                if (model.isCheckOpen()) {
                    holder.binding.openNotf.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }
        });

        if (model.isCheckOpen()) {
            holder.binding.openNotf.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        Notification2sampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = Notification2sampleBinding.bind(itemView);
        }
    }
}
