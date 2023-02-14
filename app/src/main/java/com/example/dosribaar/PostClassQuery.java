package com.example.dosribaar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PostClassQuery {

    DatabaseReference database;

    public PostClassQuery(String Id) {
        database = FirebaseDatabase.getInstance().getReference().child("User").child(Id).child("NewPost");
    }

    public Query get(String Key) {

        if (Key == null) {
            return database.orderByKey().limitToLast(3);
        }

        return database.orderByKey().startAfter(Key).limitToLast(3);
    }
}
