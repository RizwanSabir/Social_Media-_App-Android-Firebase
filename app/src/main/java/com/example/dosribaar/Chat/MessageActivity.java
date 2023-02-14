package com.example.dosribaar.Chat;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dosribaar.R;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.ActivityMessageBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    ActivityMessageBinding binding;
    List<messageList> list = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int unseenMessage = 0;
    DatabaseReference t1;
    ValueEventListener v1;
    messageAdaptor messageAdaptor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String Id = getIntent().getStringExtra("Ids");


        binding.messageRecyclerView.setHasFixedSize(true);
        binding.messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageAdaptor1 = new messageAdaptor(list, MessageActivity.this);
        binding.messageRecyclerView.setAdapter(messageAdaptor1);


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        t1 = database.getReference().child("User").child(Id);
        v1 = t1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.icons8_male_user_24)
                        .into(binding.ProfileImage);

                for (DataSnapshot dataSnapshot : snapshot.child("Chat").getChildren()) {
                    ChatUser user1 = null;
                    unseenMessage = 0;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            user1 = dataSnapshot1.getValue(ChatUser.class);
                            if (!user1.isCheck()) {
                                unseenMessage = unseenMessage + 1;
                            }
                        }
                        System.out.println("ch3");
                        messageList messageList = new messageList(dataSnapshot.getKey(),
                                dataSnapshot.getKey(), user1.getMessage(), "",
                                unseenMessage, "", Id);
                        list.add(messageList);
                    }
                }
                binding.messageRecyclerView.setAdapter(new messageAdaptor(list, MessageActivity.this));
                messageAdaptor1.updateData(list);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

}
