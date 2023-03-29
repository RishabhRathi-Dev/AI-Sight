package com.example.aisight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class PermissionChecks extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private String[] Permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!checkPermission()){
            requestPermission();
        } else {
            getToSearchOrFreeroam();
        }

    }

    private void getToSearchOrFreeroam(){
        Intent searchOrFreeroam = new Intent(this, SearchOrFreeroam.class);
        startActivity(searchOrFreeroam);
        this.finish();
    }

    private boolean checkPermission() {
        for (String perm : Permissions){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), perm) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }

        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, Permissions, PERMISSION_REQUEST_CODE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean finelocationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarselocationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean backgroundlocationAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean recordaudioAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    // TODO:: Crashes here on Caused by: java.lang.ArrayIndexOutOfBoundsException: length=4; index=4
                    //        at com.example.aisight.PermissionChecks.onRequestPermissionsResult(PermissionChecks.java:83)


                    if (finelocationAccepted && coarselocationAccepted && backgroundlocationAccepted && cameraAccepted && recordaudioAccepted) {
                        Log.d("Permissions", "All accepted");
                    }
                    else {

                        showMessageOKCancel("You need to allow access to all the permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(Permissions,
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    }
                                },

                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getToSearchOrFreeroam();
                                        }
                                    }
                                    );
                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(PermissionChecks.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
    }
}