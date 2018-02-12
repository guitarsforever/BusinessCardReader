package com.example.shreyassudheendrarao.businesscardreader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class GeoReminderActivity extends AppCompatActivity {
    float radius;
    EditText radiusText;
    boolean Service;
    Switch dragDropSwitch;
    int GPSoff = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_reminder);
        SharedPreferences sharedPrefs = getSharedPreferences("CardReader", Context.MODE_PRIVATE);
        radius = sharedPrefs.getFloat("Radius",1000);
        Service =sharedPrefs.getBoolean("ServiceStatus",false);
        radiusText = (EditText)findViewById(R.id.RadiusText);
        radiusText.setText(Double.toString(radius));
        dragDropSwitch = (Switch)findViewById(R.id.ServiceSwitch);
         dragDropSwitch.setChecked(Service);
        try {
            GPSoff = Settings.Secure.getInt(getContentResolver(),Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (GPSoff == 0) {
            showMessageOKCancel("You need to turn Location On",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(onGPS);
                        }
                    });
        }

        dragDropSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radius = Float.parseFloat(radiusText.getText().toString());
                    SharedPreferences sharedPrefs = getSharedPreferences("CardReader", Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                    Log.d ("GeoReminderActivity", "onChecked");
                    prefsEditor.putFloat("Radius", radius);
                    prefsEditor.putBoolean("ServiceStatus",true);
                    prefsEditor.apply();
                    Intent intent = new Intent(GeoReminderActivity.this, LocationService.class);
                    startService(intent);
                }
                else {
                    Log.d ("GeoReminderActivity", "onUnChecked");
                    SharedPreferences sharedPrefs = getSharedPreferences("CardReader", Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
                    prefsEditor.putBoolean("ServiceStatus",false);
                    prefsEditor.apply();
                    Intent intent = new Intent(GeoReminderActivity.this, LocationService.class);
                    stopService(intent);
                }
            }
        });

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
