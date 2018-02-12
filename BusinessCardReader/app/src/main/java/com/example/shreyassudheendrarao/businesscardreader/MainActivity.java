package com.example.shreyassudheendrarao.businesscardreader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;
    CardView Gallery;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Gallery.setEnabled(false);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Gallery = (CardView) findViewById(R.id.gallerycard);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public void onClickCamera (View view) {
        Intent newIntent = new Intent (this, CameraActivity.class);
        startActivity(newIntent);
    }
    public void onClickGallery (View view) {
        Intent newIntent = new Intent (this, GalleryActivity.class);
        startActivity(newIntent);
    }

    public void onClickContact (View view) {
        Intent newIntent = new Intent (this, ContactListActivity.class);
        startActivity(newIntent);
    }

    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPrefs = getSharedPreferences("CardReader", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putBoolean("ServiceStatus",false);
        prefsEditor.apply();
    }

    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPrefs = getSharedPreferences("CardReader", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putBoolean("ServiceStatus",false);
        prefsEditor.apply();
    }
}
