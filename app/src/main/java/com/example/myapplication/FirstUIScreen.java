package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FirstUIScreen extends AppCompatActivity {
    Button btnLoadImage, btnSaveImage, blankImage;
    ImageView imageResult;
    private static final int LOCATION_REQUEST = 222;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.first_ui_screen);
       ActionBar actionBar = getSupportActionBar();
       actionBar.hide();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    //Manifest.permission.ACCESS_FINE_LOCATION,
    @AfterPermissionGranted(LOCATION_REQUEST)
    private void checkLocationRequest() {
        String[] perms = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this,"Please grant permission",
                    LOCATION_REQUEST, perms);
        }
    }
        public void blankImageNow(View v){
            Intent intent = new Intent(FirstUIScreen.this, MainActivity.class);
            intent.putExtra("digit", 1);
            startActivity(intent);
        }
        public void loadImageNow(View v){
            Intent intent = new Intent(FirstUIScreen.this, MainActivity.class);
            intent.putExtra("digit", 2);
            startActivity(intent);

        }
}
