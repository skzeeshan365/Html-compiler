package com.reiserx.htmlcompiler.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.reiserx.htmlcompiler.Models.projects;
import com.reiserx.htmlcompiler.databinding.ActivityAddDataBinding;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class AddData extends AppCompatActivity {

    ActivityAddDataBinding binding;

    String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Add Project");

        binding.button2.setOnClickListener(view -> {
            if (!binding.nameTxt.getText().toString().trim().equals("") && !binding.descTt.getText().toString().trim().equals("")) {
                long id = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                try {
                    addEntryToJsonFile(id, binding.nameTxt.getText().toString(), binding.descTt.getText().toString());
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addEntryToJsonFile(long id, String name, String desc) throws Exception {


        projects projects = new projects(name, desc, id);
        // parse existing/init new JSON
        String jsonFiles = Environment.getExternalStorageDirectory() + "/ReiserX/db.json";
        File jsonFile = new File(jsonFiles);
        String previousJson;
        if (jsonFile.exists()) {
            try {
                previousJson = FileUtils.fileRead(jsonFiles);
                if (!previousJson.equals("")) {
                    Log.e("getfilesdata", previousJson);
                    jsonString = previousJson.concat(",".concat(new Gson().toJson(projects)));
                    FileUtils.fileWrite(jsonFiles, jsonString);
                } else {
                    previousJson = new Gson().toJson(projects);
                    FileUtils.fileWrite(jsonFiles, previousJson);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            previousJson = new Gson().toJson(projects);
            FileUtils.fileWrite(jsonFiles, previousJson);
        }
    }
}