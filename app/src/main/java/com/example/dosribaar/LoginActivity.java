package com.example.dosribaar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dosribaar.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseUser CurrentUser;
    FirebaseDatabase database;
    String Id;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        CurrentUser = auth.getCurrentUser();
        binding.goToSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.emailText.getText().toString().toLowerCase(Locale.ROOT);
                String password = binding.passwordText.getText().toString();

                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("Loading");
                dialog.setMessage("Please Wait.......");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                if (email.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please fill the form.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (!isEmailValid(email)){
                    Toast.makeText(LoginActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                   dialog.dismiss();
                    return;
                }

                auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        if(!task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Check internet Connection !", Toast.LENGTH_SHORT).show();

                        }else {

                            if (task.getResult().getSignInMethods().size() == 0) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "User doesn't exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            database = FirebaseDatabase.getInstance();
                                            auth = FirebaseAuth.getInstance();

                                            database.getReference().child("UID").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    Id = snapshot.getValue(String.class);
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    MemoryData.deleteName(getApplicationContext());
                                                    MemoryData.saveName(Id, getApplicationContext());
                                                    dialog.dismiss();
                                                    intent.putExtra("ID", Id);
                                                    startActivity(intent);
                                                    finish();

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please Try again !", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }
                                });

                            }
                        }
                    }
                });
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}