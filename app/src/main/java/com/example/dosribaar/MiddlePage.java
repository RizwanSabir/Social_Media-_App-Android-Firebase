package com.example.dosribaar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MiddlePage extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_page);
        auth = FirebaseAuth.getInstance();
        CurrentUser = auth.getCurrentUser();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser != null) {
            Intent intent = new Intent(MiddlePage.this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MiddlePage.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}