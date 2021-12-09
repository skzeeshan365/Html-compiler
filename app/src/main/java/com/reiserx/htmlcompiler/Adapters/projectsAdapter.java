package com.reiserx.htmlcompiler.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.reiserx.htmlcompiler.Activities.ProjectFiles;
import com.reiserx.htmlcompiler.Models.projects;
import com.reiserx.htmlcompiler.R;
import com.reiserx.htmlcompiler.databinding.ProjectListBinding;

import org.apache.commons.io.FileUtils;

import java.util.ArrayList;

public class projectsAdapter extends RecyclerView.Adapter<projectsAdapter.SingleViewHolder> {
    Context context;
    ArrayList<projects> data;

    public projectsAdapter(Context context, ArrayList<projects> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public projectsAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_list, parent, false);
        return new projectsAdapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull projectsAdapter.SingleViewHolder holder, int position) {
        projects projects = data.get(position);

        holder.binding.name.setText(projects.getName());
        holder.binding.description.setText(projects.getDescription());

        holder.itemView.setOnClickListener(view -> {
            SharedPreferences save = context.getSharedPreferences("Download", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = save.edit();
            myEdit.putString("name", projects.getName());
            myEdit.putLong("id", projects.getId());
            myEdit.apply();
            Intent i = new Intent(context, ProjectFiles.class);
            context.startActivity(i);
        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Delete project");
            alert.setMessage("Are you sure you want to delete this project");
            alert.setPositiveButton("delete", (dialogInterface, i) -> {
                int pos = holder.getAdapterPosition();
                if (position==pos) {
                    data.remove(pos);
                    notifyDataSetChanged();
                    String jsonFiles = Environment.getExternalStorageDirectory() + "/ReiserX/db.json";
                    String dat = new Gson().toJson(data);
                    try {
                        FileUtils.fileWrite(jsonFiles, dat);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (pos==0) {
                        try {
                            FileUtils.fileWrite(jsonFiles, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            alert.setNegativeButton("cancel", null);
            alert.show();
            return false;
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        ProjectListBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProjectListBinding.bind(itemView);
        }
    }
}
