package com.example.dosribaar.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dosribaar.Adaptor.SearchAdaptor;
import com.example.dosribaar.User;
import com.example.dosribaar.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    ArrayList<User> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    FragmentSearchBinding binding;
    String ID;
    ValueEventListener v1;
    ValueEventListener v2;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");
    SearchAdaptor searchAdaptor;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ID = getArguments().getString("ID");
        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Please Wait...");
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);



        beforeTextChange();

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.equals("")) {
                    beforeTextChange();
                } else {
                    TextSearch(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                TextSearch(s);
                return false;
            }
        });

        return binding.getRoot();

    }

    private void beforeTextChange() {

        DatabaseReference reference1 = database.getReference().child("User");
        v2 = reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                reference1.removeEventListener(v2);
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);

                    if (!snapshot1.getKey().equals(ID)) {
                        user.setUserId(snapshot1.getKey());
                        list.add(user);
                    }
                }
                searchAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchAdaptor = new SearchAdaptor(getContext(), list, ID);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.usersRv.setLayoutManager(linearLayoutManager);
        binding.usersRv.setAdapter(searchAdaptor);
        dialog.dismiss();
    }


    private void TextSearch(String str) {
        v1 = reference.orderByChild("name").startAt(str).endAt(str + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    reference.removeEventListener(v1);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (!dataSnapshot.getKey().equals(ID)) {
                            User users = dataSnapshot.getValue(User.class);
                            users.setUserId(dataSnapshot.getKey());
                            list.add(users);
                        }
                    }
                    searchAdaptor = new SearchAdaptor(getContext(), list, ID);
                    binding.usersRv.setAdapter(searchAdaptor);
                    searchAdaptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}