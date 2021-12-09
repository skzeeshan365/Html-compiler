package com.reiserx.htmlcompiler.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.reiserx.htmlcompiler.R;
import com.reiserx.htmlcompiler.databinding.ActivityProjectFilesBinding;

public class ProjectFiles extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityProjectFilesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_project_files);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(view -> {
            SharedPreferences save = view.getContext().getSharedPreferences("Download", MODE_PRIVATE);
            String s = String.valueOf(save.getLong("id", 0));

            Intent intent = new Intent(this, AddFiles.class);
            intent.putExtra("id", s);
            intent.putExtra("code", "0");
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_project_files);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}