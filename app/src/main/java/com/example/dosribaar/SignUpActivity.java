package com.example.dosribaar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dosribaar.Model.Follow;
import com.example.dosribaar.Model.Following;
import com.example.dosribaar.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {
    static String ID1;
    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this);
        binding.goTologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailtext.getText().toString().toLowerCase(Locale.ROOT);
                String password = binding.passwordText.getText().toString();
                String Name=binding.nameText.getText().toString();
                String ID=binding.IDText.getText().toString().toLowerCase(Locale.ROOT);

                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("Setting Up Your Profile");
                dialog.setMessage("Please Wait.......");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                if (password.equals("")|| email.equals("")|| ID.equals("")||Name.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please fill all Field!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (!isEmailValid(email)){
                    Toast.makeText(SignUpActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                  dialog.dismiss();
                    return;
                }


                if (password.length() < 7) {
                    binding.passwordText.getText().clear();
                    binding.passwordText.setHint("Password (Greater than 7 character)");
                    dialog.dismiss();
                    return;
                }



               auth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                if(!task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Check internet Connection !", Toast.LENGTH_SHORT).show();
                                }else{
                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                    if (isNewUser) {
                                        auth.createUserWithEmailAndPassword(email, password).
                                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            String Id = binding.IDText.getText().toString();
                                                            ID1 = Id;

                                                            User user = new User(binding.nameText.getText().toString(),
                                                                    binding.IDText.getText().toString(), email, password,
                                                                    0, 0);
                                                            database.getReference().child("User").child(Id).setValue(user);
                                                            database.getReference().child("UID").child(auth.getUid()).setValue(Id);

                                                            Intent intent = new Intent(SignUpActivity.this, UpdateProfilePic.class);
                                                            MemoryData memoryData = new MemoryData();
                                                            memoryData.saveName(Id, getApplicationContext());
                                                            dialog.dismiss();
                                                            intent.putExtra("ID", Id);
                                                            startActivity(intent);
                                                            finish();

                                                        }
                                                    }
                                                });
                                    } else {
                                      //  dialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "User with email already exists!", Toast.LENGTH_SHORT).show();
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