package com.example.dosribaar.Chat;


import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dosribaar.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    FirebaseDatabase database;
    List<ChatList> chatLists = new ArrayList<>();
    ChatAdaptor chatAdaptor;
    boolean LoadingFirstTime = true;
    FirebaseAuth auth;
    String PID;
    DatabaseReference r1;
    ValueEventListener v1;
    int count;
    String Id;
    Runnable userStoppedTyping = new Runnable() {
        @Override
        public void run() {
            database.getReference().child("User").child(Id).child("Presence").setValue("Online");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        PID = getIntent().getStringExtra("PID");

        Id = getIntent().getStringExtra("Ids");
        String Name = getIntent().getStringExtra("Name");
        String pic = getIntent().getStringExtra("Pic");



        binding.Name.setText(Name);
        Picasso.get().load(pic).into(binding.ProfilePic);


        binding.chattingRecyclerView.setHasFixedSize(true);
        binding.chattingRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        chatAdaptor = new ChatAdaptor(chatLists, ChatActivity.this, PID);
        binding.chattingRecyclerView.setAdapter(chatAdaptor);

        database.getReference().child("User").child(PID).child("Presence").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.online.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("User").child(PID).child("Chat")
                .child(Id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        count = count + 1;
                        if (snapshot.exists()) {
                            chatLists.clear();
                            for (DataSnapshot messageSnapShot : snapshot.getChildren()) {

                                ChatUser user = messageSnapShot.getValue(ChatUser.class);
                                long messgaTimeStamp = user.getTime();

                                Timestamp timestamp = new Timestamp
                                        (Long.parseLong(String.valueOf(messgaTimeStamp)));
                                Date date = new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat =
                                        new SimpleDateFormat("dd-MM-yyyy ", Locale.getDefault());
                                SimpleDateFormat simpleTimeFormat =
                                        new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                ChatList chatList = new ChatList(user.getUID(),
                                        Name, user.getMessage(), simpleTimeFormat.format(timestamp),
                                        simpleDateFormat.format(date));

                                chatLists.add(chatList);
                            }
                            LoadingFirstTime = false;
                            chatAdaptor.updateChatLists(chatLists);
                            binding.chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        Handler handler = new Handler();
        binding.MessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("User").child(Id).child("Presence").setValue("Typing....");
                handler.removeCallbacks(null);
                handler.postDelayed(userStoppedTyping, 1000);
            }
        });


        binding.SendbTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getMessageText = binding.MessageEditText.getText().toString();
                binding.MessageEditText.setText("");

                r1 = database.getReference().child("User").child(PID).child("Chat").child(Id);
                v1 = r1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        r1.removeEventListener(v1);
                        int lenght = (int) snapshot.getChildrenCount();
                        ChatUser user1 = new ChatUser(getMessageText, PID, true,
                                System.currentTimeMillis());

                        lenght = lenght + 1;

                        database.getReference().child("User").child(PID).child("Chat").child(Id)
                                .child(String.valueOf(lenght)).setValue(user1);

                        database.getReference().child("User").child(Id).child("Chat").child(PID)
                                .child(String.valueOf(lenght)).child("Check").setValue(true);

                        ChatUser user2 = new ChatUser(getMessageText, PID, false,
                                System.currentTimeMillis());

                        database.getReference().child("User").child(Id).child("Chat").child(PID)
                                .child(String.valueOf(lenght)).setValue(user2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}