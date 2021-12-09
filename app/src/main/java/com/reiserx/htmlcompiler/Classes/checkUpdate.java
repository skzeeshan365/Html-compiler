package com.reiserx.htmlcompiler.Classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.htmlcompiler.Activities.updateApp;
import com.reiserx.htmlcompiler.BuildConfig;
import com.reiserx.htmlcompiler.Models.updateAppss;

public class checkUpdate {
    private FirebaseDatabase database;

    public void check(Context context) {
        database = FirebaseDatabase.getInstance();
        database.getReference("Administration").child("App").child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateAppss updateAp = snapshot.getValue(updateAppss.class);
                    if (!updateAp.version.equals(BuildConfig.VERSION_NAME)) {
                        Intent i = new Intent(context, updateApp.class);
                        i.putExtra("version", updateAp.version);
                        i.putExtra("url", updateAp.url);
                        context.startActivity(i);
                    } else {
                        Log.d("updatecheker", "latest version");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
