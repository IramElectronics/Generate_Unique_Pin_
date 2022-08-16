package com.example.generateuniquepin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {


    int CODE = 112;
    String TAG = "Permission_My_Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        findViewById(R.id.backButton).setOnClickListener(view -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });


        if (checkPermission()) {
            Log.d(TAG, "onCreate: Permission already granted....");
            createFolder();
        }
        else {
            Log.d(TAG, "onCreate: Permission was not granted, request....");
            requestPermission();
        }

    }

    void createFolder() {
        File file = new File(Environment.getExternalStorageDirectory() + "/mysheetiram");
        boolean folderCreated = file.mkdir();
        if (folderCreated) {
            Toast.makeText(this, "folder Created", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "folder not created", Toast.LENGTH_SHORT).show();
        }
    }


    void requestPermission(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) {
            // Android is R or above
            try {
                Log.d(TAG, "requestPermission: try");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            }
            catch (Exception e) {
                Log.e(TAG, "requestPermission: catch");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        }
        else {
            // Android is below R
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, CODE);
        }
    }

    boolean checkPermission(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) {
            // Android is R or above
            return Environment.isExternalStorageManager();
        }
        else {
            // Android is below R
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return (write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED);
        }

    }




    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    // here we will handle the result of our intent
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // Android is R or above
                        if (Environment.isExternalStorageManager()) {
                            // Manage External Storage Permission is granted
                            Log.d(TAG, "onActivityResult: Manage External Storage Permission is granted");
                            createFolder();
                        }
                        else {
                            // Manage External Storage Permission is denied
                            Log.d(TAG, "onActivityResult: Manage External Storage Permission is denied");
                            Toast.makeText(MainActivity2.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        // Android is below R

                    }
                }
            }
    );


    // handle the permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODE) {
            if (grantResults.length > 0) {
                // check each permission if granted
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (write && read) {
                    //External Storage Permission granted
                    Log.d(TAG, "onRequestPermissionsResult: External Storage Permission granted");
                    createFolder();
                }
                else {
                    //External Storage Permission denied
                    Log.d(TAG, "onRequestPermissionsResult: External Storage Permission denied");
                    Toast.makeText(this, "External Storage Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}