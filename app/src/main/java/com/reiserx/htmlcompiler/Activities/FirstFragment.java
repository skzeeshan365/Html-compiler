package com.reiserx.htmlcompiler.Activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.htmlcompiler.Adapters.projectsAdapter;
import com.reiserx.htmlcompiler.Models.projects;
import com.reiserx.htmlcompiler.databinding.FragmentFirstBinding;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    ArrayList<projects> data;
    projectsAdapter adapter;
    String TAG = "ihgkjdnjfn";

    boolean results = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.progressBar2.setVisibility(View.VISIBLE);

        String[] permissions = new String[]{
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE};
        if (checkPermissions(permissions, view.getContext())) {
            data = new ArrayList<>();
            binding.rec.setLayoutManager(new LinearLayoutManager(view.getContext()));
            adapter = new projectsAdapter(view.getContext(), data);
            binding.rec.setAdapter(adapter);
            addItemsFromJSON();
        }
    }

    private boolean checkPermissions(String[] permissions, Context context) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    listPermissionsNeeded.add(p);
                    results = false;
                } else results = true;
            } else {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                    results = false;
                } else results = true;
            }
        }

        return results;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (data!=null) {
            data.clear();
            binding.rec.setAdapter(adapter);
            addItemsFromJSON();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("SetTextI18n")
    private void addItemsFromJSON() {
        JSONArray jsonArray;

        try {
            String jsonFiles = Environment.getExternalStorageDirectory() + "/ReiserX/db.json";
            File jsonFile = new File(jsonFiles);
            if (jsonFile.exists()) {

                String jsonDataString = readJSONDataFromFile();
                if (jsonDataString.startsWith("[") && jsonDataString.endsWith("]")) {
                    jsonArray = new JSONArray(jsonDataString);
                } else {
                    jsonArray = new JSONArray("[" + jsonDataString + "]");
                }

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); ++i) {

                        JSONObject itemObj = jsonArray.getJSONObject(i);

                        String name = itemObj.getString("name");
                        String desc = itemObj.getString("description");
                        long id = itemObj.getLong("id");

                        projects projects = new projects(name, desc, id);
                        data.add(projects);
                    }
                }
            }
            if (data.isEmpty()) {
                binding.rec.setVisibility(View.GONE);
                binding.progHolder.setVisibility(View.VISIBLE);
                binding.textView9.setVisibility(View.VISIBLE);
                binding.progressBar2.setVisibility(View.GONE);
                binding.textView9.setText("No projects found, please add your first project");
            } else {
                binding.rec.setVisibility(View.VISIBLE);
                binding.progHolder.setVisibility(View.GONE);
                binding.textView9.setVisibility(View.GONE);
                binding.progressBar2.setVisibility(View.GONE);
            }

            } catch(JSONException e){
                Log.d(TAG, "addItemsFromJSON: ", e);
            }
    }

    private String readJSONDataFromFile() {
        String jsonFiles = Environment.getExternalStorageDirectory() + "/ReiserX/db.json";
        File jsonFile = new File(jsonFiles);
        String json = null;
        if (jsonFile.exists()) {
            try {

                json = FileUtils.fileRead(jsonFiles);
                Log.e(TAG, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}