package com.example.dosribaar.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dosribaar.CommentActivity;
import com.example.dosribaar.Model.Post;
import com.example.dosribaar.R;
import com.example.dosribaar.databinding.PhotouploadBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendProfileAdaptor extends RecyclerView.Adapter<FriendProfileAdaptor.ViewHolder> {

    Context ctx;
    ArrayList<Post> list;
    String ID;
    public FriendProfileAdaptor(Context ctx, ArrayList<Post> list, String ID) {
        this.ctx = ctx;
        this.list = list;
        this.ID=ID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.photoupload, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = list.get(position);
        Picasso.get().load(post.getPostImage()).placeholder(R.drawable.loading).into(holder.binding.postimage);




        holder.binding.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, CommentActivity.class);
                intent.putExtra("ID",ID);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("postedBy", post.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PhotouploadBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PhotouploadBinding.bind(itemView);
        }
    }
}
