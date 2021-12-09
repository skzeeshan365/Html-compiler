package com.reiserx.htmlcompiler.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.reiserx.htmlcompiler.Classes.FileUtil;
import com.reiserx.htmlcompiler.R;
import com.reiserx.htmlcompiler.databinding.ActivityAddFilesBinding;

import java.io.File;
import java.util.ArrayList;

public class AddFiles extends AppCompatActivity {

    ActivityAddFilesBinding binding;

    LinearLayout layout;
    EditText name;
    EditText ext;
    ImageView image;

    boolean isImage = false;

    Uri selectedImage;
    Uri importedFile;

    String tag = "gbtrhtr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Add files");

        String id = getIntent().getStringExtra("id");
        String code = getIntent().getStringExtra("code");

        if (code.equals("0")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Choose file");
            dialog.setMessage("Please choose your file type");
            dialog.setPositiveButton("file", (dialogInterface, i) -> alert(id));

            dialog.setNegativeButton("image", (dialogInterface, i) -> imagealert(id));
            dialog.setNeutralButton("cancel", (dialogInterface, i) -> finish());
            dialog.setCancelable(false);
            dialog.show();
        } else if (code.equals("1")) {
            String path = getIntent().getStringExtra("path");
            String fileName = getIntent().getStringExtra("fileName");

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.edittext_dialig,null);
            layout = mView.findViewById(R.id.edittext_holder);
            name = mView.findViewById(R.id.file_name);
            ext = mView.findViewById(R.id.extensions);
            image = mView.findViewById(R.id.imageView2);
            ext.setVisibility(View.GONE);

            alert.setTitle("Rename file");
            alert.setMessage("Rename and save the file");
            alert.setView(mView);

            if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")) {
                image.setVisibility(View.VISIBLE);
                Glide.with(this).load(path).into(image);
            } else {
                image.setVisibility(View.GONE);
            }

            name.setText(fileName);

            alert.setPositiveButton("Rename", (dialog, whichButton) -> {
                //What ever you want to do with the value
                if (!name.getText().toString().equals("") && !ext.getText().toString().equals("")) {
                    String nameStr = name.getText().toString();
                    String dir = Environment.getExternalStorageDirectory() + "/ReiserX/" + id + "/" + nameStr;
                    File from = new File(path);
                    File to = new File(dir);
                    if (from.renameTo(to)) {
                        finish();
                    }
                }
            });
            alert.setNeutralButton("cancel", (dialogInterface, i) -> finish());
            alert.setCancelable(false);
            alert.show();
        }
    }

    @SuppressLint("SetTextI18n")
    public void imagealert(String s) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View mView = getLayoutInflater().inflate(R.layout.edittext_dialig,null);
        layout = mView.findViewById(R.id.edittext_holder);
        name = mView.findViewById(R.id.file_name);
        ext = mView.findViewById(R.id.extensions);
        image = mView.findViewById(R.id.imageView2);
        alert.setView(mView);

        image.setVisibility(View.VISIBLE);
        image.setImageResource(R.drawable.ic_baseline_add_24);
        ext.setText(".jpg");

        image.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 25);
        });

        alert.setPositiveButton("ADD", (dialog, whichButton) -> {
            //What ever you want to do with the value

            if (!name.getText().toString().equals("") && !ext.getText().toString().equals("")) {
                String nameStr = name.getText().toString();
                String exts = ext.getText().toString();
                String dir = Environment.getExternalStorageDirectory() + "/ReiserX/" + s + "/" + nameStr + exts;
                String filess = FileUtil.convertUriToFilePath(this, selectedImage);
                FileUtil.copyFile(filess, dir);
                finish();
            }
        });

        alert.setNegativeButton("CANCEL", (dialog, whichButton) -> finish());
        alert.setCancelable(false);
        alert.show();
    }

    public void alert(String s) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.edittext_dialig,null);
        layout = mView.findViewById(R.id.edittext_holder);
        name = mView.findViewById(R.id.file_name);
        ext = mView.findViewById(R.id.extensions);
        image = mView.findViewById(R.id.imageView2);
        image.setVisibility(View.GONE);

        alert.setTitle("Add file");
        alert.setMessage("Enter file name and extension");
        alert.setView(mView);

        alert.setPositiveButton("ADD", (dialog, whichButton) -> {
            //What ever you want to do with the value
            if (!name.getText().toString().equals("") && !ext.getText().toString().equals("")) {
                String nameStr = name.getText().toString();
                String descStr = ext.getText().toString();
                try {
                    addEntryToJsonFile(nameStr, descStr, s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton("Import file", (dialog, whichButton) -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 225);
        });
        alert.setNeutralButton("cancel", (dialogInterface, i) -> finish());
alert.setCancelable(false);
        alert.show();
    }


    public void addEntryToJsonFile(String name, String extension, String s) {

        // parse existing/init new JSON
        String dir = Environment.getExternalStorageDirectory() + "/ReiserX/"+s;
        File folder = new File(dir);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                Intent intent = new Intent(this, EditFiles.class);
                intent.putExtra("path", dir+"/"+name+extension);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 25) {
            if(data != null) {
                if(data.getData() != null) {
                    selectedImage = data.getData();
                    image.setImageURI(selectedImage);
                    isImage = true;
                } else isImage = false;
            }
        } else if (requestCode == 225) {
            if(data != null) {
                if(data.getData() != null) {
                    ArrayList<String> filepath = new ArrayList<>();
                    importedFile = data.getData();
                    filepath.add(FileUtil.convertUriToFilePath(this, importedFile));
                    String name = Uri.parse(filepath.get(0)).getLastPathSegment();
                    Log.d(tag, name);
                    importFIle(name, FileUtil.convertUriToFilePath(this, importedFile), importedFile);
                    }
                }
            }
        }

    public void importFIle(String names, String source, Uri uri) {
        String id = getIntent().getStringExtra("id");
        Log.d(tag, names);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.edittext_dialig,null);
        layout = mView.findViewById(R.id.edittext_holder);
        name = mView.findViewById(R.id.file_name);
        ext = mView.findViewById(R.id.extensions);
        image = mView.findViewById(R.id.imageView2);
        ext.setVisibility(View.GONE);

        alert.setTitle("Import file");
        alert.setMessage("Save the file");
        alert.setView(mView);

        if (names.endsWith(".jpg") || names.endsWith(".jpeg") || names.endsWith(".png")) {
            image.setVisibility(View.VISIBLE);
            image.setImageURI(uri);
        } else image.setVisibility(View.GONE);

        name.setText(names);

        alert.setPositiveButton("Add", (dialog, whichButton) -> {
            //What ever you want to do with the value
            if (!name.getText().toString().equals("") && !ext.getText().toString().equals("")) {
                String nameStr = name.getText().toString();
                String dir = Environment.getExternalStorageDirectory() + "/ReiserX/" + id + "/" + nameStr;
                FileUtil.copyFile(source, dir);
                finish();
            }
        });
        alert.setNeutralButton("cancel", (dialogInterface, i) -> finish());
        alert.setCancelable(false);
        alert.show();
    }
    }