package com.reiserx.htmlcompiler.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.reiserx.htmlcompiler.Classes.fileDownloader;
import com.reiserx.htmlcompiler.databinding.ActivityUpdateAppBinding;

public class updateApp extends AppCompatActivity {

    ActivityUpdateAppBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String version = getIntent().getStringExtra("version");
        String url = getIntent().getStringExtra("url");

        binding.version.setText("Version ".concat(version));

        binding.button5.setOnClickListener(view -> {
            fileDownloader asyncTask = new fileDownloader(this);
            asyncTask.execute(String.valueOf(url));
        });
    }
}