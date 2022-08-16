package com.example.generateuniquepin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class All_External_ReadWrite_Permission {

    public static final int ReadWrite_CODE = 112;
    public static final String ReadWrite_TAG = "Permission_My_Tag";

    Context context;
    Activity activity;
    MainActivity_I mainActivity_i;


    public All_External_ReadWrite_Permission() {}

    public All_External_ReadWrite_Permission(Context context, Activity activity, MainActivity_I mainActivity_i) {
        this.context = context;
        this.activity = activity;
        this.mainActivity_i = mainActivity_i;
    }


    public void createFolder() {
        File file = new File(Environment.getExternalStorageDirectory() + "/mySheetIram");
        boolean folderCreated = file.mkdir();
        if (folderCreated) {
            Toast.makeText(context, "folder Created", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "folder not created", Toast.LENGTH_SHORT).show();
        }
    }


    void requestPermission(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) {
            // Android is R or above
            try {
                Log.d(ReadWrite_TAG, "requestPermission: try");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);

                //mainActivity_i.storageActivityResultLauncher.launch(intent);
                mainActivity_i.use_storageActivityResultLauncher(intent);
            }
            catch (Exception e) {
                Log.e(ReadWrite_TAG, "requestPermission: catch");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);

                //mainActivity_i.storageActivityResultLauncher.launch(intent);
                mainActivity_i.use_storageActivityResultLauncher(intent);
            }
        }
        else {
            // Android is below R
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, ReadWrite_CODE);
        }
    }

    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) {
            // Android is R or above
            return Environment.isExternalStorageManager();
        }
        else {
            // Android is below R
            int write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

            return (write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED);
        }

    }


    public void check_Or_RequestPermission__Then__createFolder() {
        if (checkPermission()) {
            Log.d(ReadWrite_TAG, "onCreate: Permission already granted....");
            createFolder();
        }
        else {
            Log.d(ReadWrite_TAG, "onCreate: Permission was not granted, request....");
            requestPermission();
        }
    }





}
