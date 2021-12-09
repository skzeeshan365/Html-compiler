package com.reiserx.htmlcompiler.Activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.htmlcompiler.BuildConfig;
import com.reiserx.htmlcompiler.Classes.checkUpdate;
import com.reiserx.htmlcompiler.Models.Users;
import com.reiserx.htmlcompiler.R;
import com.reiserx.htmlcompiler.databinding.ActivityProjectListBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectList extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityProjectListBinding binding;

    ProgressDialog prog;

    boolean results = false;

    FirebaseAuth auth;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkUpdate checkUpdate = new checkUpdate();
        checkUpdate.check(this);

        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_project_list);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        auth = FirebaseAuth.getInstance();

        File MainPath = new File(Environment.getExternalStorageDirectory() + "/ReiserX");
        if (!MainPath.exists()) {
            MainPath.mkdir();
        }

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(ProjectList.this, AddData.class);
            startActivity(intent);
        });

        String[] permissions = new String[]{
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE};
        Log.d("hvbdhvbdhbv", String.valueOf(checkPermissions(permissions)));
        if (checkPermissions(permissions)) {
            FirebaseUser User = auth.getCurrentUser();
            if (User == null) {
                Login();
            }
        } else {
            Intent i = new Intent(this, PermissionActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_project_list);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void Login() {

        prog = new ProgressDialog(this);
        prog.setMessage("Processing...");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.login_layout, null);
        EditText name = mView.findViewById(R.id.title_text);

        alert.setTitle("Login");
        alert.setMessage("Please enter your name");
        alert.setView(mView);

        alert.setPositiveButton("Add", (dialog, whichButton) -> {
            //What ever you want to do with the value
            if (!name.getText().toString().equals("")) {
                String nameStr = name.getText().toString();
                prog.show();
                LoginProcess(nameStr);
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    public void LoginProcess(String name) {
        prog.setMessage("Login in progress...");
        auth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        FirebaseUser Users = auth.getCurrentUser();
                        if (Users != null) {
                            UpdateUserData(Users.getUid(), name);
                        }
                    } else {
                        prog.dismiss();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(ProjectList.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void UpdateUserData(String UserID, String name) {
        prog.setMessage("Updating Login Info...");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    Map<String, Object> tokens = new HashMap<>();
                    tokens.put("Notification token", token);
                    CollectionReference document = firestore.collection("Main").document(UserID).collection("TokenUrl");
                    document.add(tokens)
                            .addOnSuccessListener(documentReference -> {
                                com.reiserx.htmlcompiler.Models.Users user = new Users(UserID, name);
                                db = FirebaseDatabase.getInstance();
                                db.getReference().child("UserData").setValue(user).addOnSuccessListener(unused -> {
                                    prog.dismiss();
                                    Log.d("Tokensssss", "DocumentSnapshot added with ID: " + documentReference.getId());
                                });
                            });
                })
                .addOnFailureListener(e -> {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        auth.signOut();
                    }
                    finishAffinity();
                    Toast.makeText(this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private boolean checkPermissions(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.bug) {
            Toast.makeText(this, "bug", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.contact) {
            Toast.makeText(this, "contact", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.about) {
            aboutApp();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("SetTextI18n")
    private void aboutApp() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.about_dialog,null);

        ImageView image = mView.findViewById(R.id.imageView4);
        TextView appName = mView.findViewById(R.id.app_name);
        TextView about = mView.findViewById(R.id.About_txt);

        alert.setView(mView);

        appName.setText("Html compiler".concat(" v"+BuildConfig.VERSION_NAME));
        about.setText("Design and developed by ReiserX, Html compiler a free service.\nIf you encounter any bug please report");


        alert.setPositiveButton("OK", null);
        alert.show();
    }
}