package com.example.dosribaar.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dosribaar.Adaptor.NotificationAdaptor;
import com.example.dosribaar.Model.Notification;
import com.example.dosribaar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Notification2Fragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Notification> list;
    FirebaseDatabase database;
    String ID;

    public Notification2Fragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=FirebaseDatabase.getInstance();
        ID = getArguments().getString("ID");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notification2, container, false);

        recyclerView=view.findViewById(R.id.NotificationRec);
        list=new ArrayList<>();



        NotificationAdaptor adaptor=new NotificationAdaptor(list,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adaptor);


        database.getReference().child("Notf").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                  list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Notification notification=dataSnapshot.getValue(Notification.class);
                    notification.setNotfId(dataSnapshot.getKey());
                    list.add(notification);
                }
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return view;
    }
}