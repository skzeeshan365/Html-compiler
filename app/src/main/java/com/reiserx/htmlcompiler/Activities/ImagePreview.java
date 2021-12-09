package com.reiserx.htmlcompiler.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.reiserx.htmlcompiler.databinding.ActivityImagePreviewBinding;

public class ImagePreview extends AppCompatActivity {

    ActivityImagePreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String path = getIntent().getStringExtra("path");

        Glide.with(this).load(path).into(binding.imageView);
    }
}