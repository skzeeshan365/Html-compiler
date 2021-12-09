package com.reiserx.htmlcompiler.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.reiserx.htmlcompiler.R;
import com.reiserx.htmlcompiler.databinding.ActivityEditFilesBinding;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class EditFiles extends AppCompatActivity {

    ActivityEditFilesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String path = getIntent().getStringExtra("path");
        setTitle(getIntent().getStringExtra("filename"));

        binding.codeView.resetSyntaxPatternList();
        binding.codeView.setEnableLineNumber(true);
        binding.codeView.setText(readFile(path));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_file) {
            String data = String.valueOf(binding.codeView.getText());
            try {
                FileUtils.fileWrite(getIntent().getStringExtra("path"), data);
                Toast.makeText(this, "file saved", Toast.LENGTH_SHORT).show();
                binding.codeView.clearFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (item.getItemId() == R.id.run) {
            String path = getIntent().getStringExtra("path");
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("path", path);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String readFile(String path) {
        File jsonFile = new File(path);
        String json = null;
        if (jsonFile.exists()) {
            try {

                json = FileUtils.fileRead(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}