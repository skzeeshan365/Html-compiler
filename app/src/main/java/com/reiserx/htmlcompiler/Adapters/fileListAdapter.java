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

import com.reiserx.htmlcompiler.Activities.AddFiles;
import com.reiserx.htmlcompiler.Activities.EditFiles;
import com.reiserx.htmlcompiler.Activities.ImagePreview;
import com.reiserx.htmlcompiler.Classes.FileUtil;
import com.reiserx.htmlcompiler.Models.singleItem;
import com.reiserx.htmlcompiler.R;
import com.reiserx.htmlcompiler.databinding.ProjectListBinding;

import java.util.ArrayList;

public class fileListAdapter extends RecyclerView.Adapter<fileListAdapter.SingleViewHolder> {
    Context context;
    ArrayList<singleItem> data;

    public fileListAdapter(Context context, ArrayList<singleItem> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public fileListAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_list, parent, false);
        return new fileListAdapter.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull fileListAdapter.SingleViewHolder holder, int position) {
        singleItem projects = data.get(position);

        holder.binding.name.setText(projects.getName());
        holder.binding.description.setVisibility(View.GONE);

        SharedPreferences save = context.getSharedPreferences("Download", MODE_PRIVATE);
        String projectID = String.valueOf(save.getLong("id", 0));
        String path = Environment.getExternalStorageDirectory().toString() + "/ReiserX/".concat(projectID);

        holder.itemView.setOnClickListener(view -> {

            Intent intent;
            if (projects.getName().endsWith(".jpg") || projects.getName().endsWith(".jpeg") || projects.getName().endsWith(".png")) {
                intent = new Intent(context, ImagePreview.class);
                intent.putExtra("path", path + "/" + projects.getName());
            } else {
                intent = new Intent(context, EditFiles.class);
                intent.putExtra("path", path + "/" + projects.getName());
                intent.putExtra("filename", projects.getName());
            }
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Delete file");
            alert.setMessage("Are you sure you want to delete this file");
            alert.setPositiveButton("delete", (dialogInterface, i) -> {
                int pos = holder.getAdapterPosition();
                if (position==pos) {
                    data.remove(pos);
                    notifyDataSetChanged();
                    FileUtil.deleteFile(path + "/" + projects.getName());
                }
            });
            alert.setNegativeButton("Rename", (dialogInterface, i) -> {
                Intent intent = new Intent(context, AddFiles.class);
                intent.putExtra("id", projectID);
                intent.putExtra("code", "1");
                intent.putExtra("path", path + "/" + projects.getName());
                intent.putExtra("fileName", projects.getName());
                context.startActivity(intent);
            });
            alert.setNeutralButton("cancel", null);
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
