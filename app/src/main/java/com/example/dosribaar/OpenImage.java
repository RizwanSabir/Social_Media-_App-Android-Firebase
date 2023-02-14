package com.example.dosribaar;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dosribaar.databinding.ActivityOpenImageBinding;
import com.squareup.picasso.Picasso;

public class OpenImage extends AppCompatActivity {
    ImageView imageView;
    ActivityOpenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imageView = findViewById(R.id.image11);

        String image = getIntent().getStringExtra("i");
        Picasso.get().load(image).placeholder(R.drawable.loading).into(imageView);
    }
}