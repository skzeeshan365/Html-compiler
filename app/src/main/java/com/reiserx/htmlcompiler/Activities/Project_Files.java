package com.reiserx.htmlcompiler.Activities;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.htmlcompiler.Adapters.fileListAdapter;
import com.reiserx.htmlcompiler.Models.singleItem;
import com.reiserx.htmlcompiler.databinding.FragmentFirst2Binding;

import java.io.File;
import java.util.ArrayList;

public class Project_Files extends Fragment {

    private FragmentFirst2Binding binding;

    fileListAdapter adapter;
    ArrayList<singleItem> data;

    Context context;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirst2Binding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.progressBar2.setVisibility(View.VISIBLE);

        context = view.getContext();

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new fileListAdapter(view.getContext(), data);
        binding.rec.setAdapter(adapter);
        RefreshListview();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (data!=null) {
            data.clear();
            binding.rec.setAdapter(adapter);
            RefreshListview();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("SetTextI18n")
    private void RefreshListview () {
        SharedPreferences save = context.getSharedPreferences("Download", MODE_PRIVATE);
        String projectID = String.valueOf(save.getLong("id", 0));

        String path = Environment.getExternalStorageDirectory().toString() + "/ReiserX/".concat(projectID);

        File folder = new File(path);
        File[] files = folder.listFiles();
        singleItem singleItem;
        if (folder.exists()) {
            for (File file : files != null ? files : new File[0]) {
                singleItem = new singleItem(file.getName());
                data.add(singleItem);
            }
        }
        if (data.isEmpty()) {
            binding.rec.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.textView9.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setText("No files found, please add your files");
        } else {
            binding.rec.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
        }
    }

}