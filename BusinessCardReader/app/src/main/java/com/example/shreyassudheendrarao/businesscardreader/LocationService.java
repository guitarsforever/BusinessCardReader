package com.example.shreyassudheendrarao.businesscardreader;

import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.Timestamp;
import java.util.ArrayList;

public class LocationService extends Service {
    private static final float LOCATION_DISTANCE = 100;
    static double mLatitude, mLongitude,lastLatitude=0.0, lastLongitude=0.0;
    public String TAG = "Location Servcies";

    long startTime, endTime, delay;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 1000 * 2 * 60;
    DBHelper dbHelper;

    ArrayList<LatLong> locationList ;

    float radius;



    public LocationService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {

        SharedPreferences sharedPrefs= getSharedPreferences("CardReader",Context.MODE_PRIVATE);
// SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);;
        radius = sharedPrefs.getFloat("Radius",100);
        //Toast.makeText(this, String.valueOf(radius),Toast.LENGTH_SHORT).show();
        dbHelper = new DBHelper();
        locationList = new ArrayList<LatLong>();
        locationList= dbHelper.getLocList(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            onLocationChanged(location);


                        }
                    }
                });
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        // startLocationUpdates();
        startLocationUpdates();

        return START_STICKY;


    }

    //setting the location requests criteria
    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //  mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setSmallestDisplacement(LOCATION_DISTANCE);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

    }

    //requesting location changes
    protected void startLocationUpdates() {



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    public void onLocationChanged(Location location) {

        String msg = "New Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Log.v("Latitude,Longitude", location.getLatitude() + location.getLongitude() + "");
        //  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        sendLocationBroadcast();
        // startLocationUpdates();


    }

    private void sendLocationBroadcast() {
        if(lastLatitude==0&&lastLongitude==0 ){
            lastLongitude=mLongitude;
            lastLatitude=mLatitude;
            Log.v("lastLatitde", "Everything 0, so setting");
            checkDistance(mLatitude, mLongitude);
        }

        else if (lastLatitude!=mLatitude && lastLongitude!=mLongitude) {
            // else if (distFrom(lastLatitude,lastLongitude,mLatitude,mLongitude)>30){
            Log.v("lastLatitde!=current", "So checking and changing, ");
            checkDistance(mLatitude, mLongitude);
        }
        else {
            //do nothing
            Log.v("lastLatitde=current", "So do nothing, ");
        }
        // checkDistance(mLatitude,mLongitude);


    }

    // checks any near by contacts depending on the radius configuration
    private void checkDistance(double latitude, double longitude) {

        String name="";

        Log.v("LocationListSize", "Size" + locationList.size());

        for (LatLong latLongObj: locationList){
            double tempLatitude = latLongObj.getLatitude();
            double tempLongitude = latLongObj.getLongitude();
            double distance = distFrom(latitude,longitude,tempLatitude,tempLongitude);
            if (distance <= radius){
                name  = latLongObj.getName() + "\n "+name;
                // showNotification(name);
                Log.v("notification", "Called Notification");
                // nearByContactList.add(name);
                showNotification(name);
            }

        }





    }


    //calculates the distance between 2 locations
    public static double distFrom(double latitude1, double longitude1, double latitude2, double longitude2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(latitude2 - latitude1);
        double dLng = Math.toRadians(longitude2 - longitude1);
        double acos = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLng / 2), 2)
                * Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2));
        double atan = 2 * Math.atan2(Math.sqrt(acos), Math.sqrt(1 - acos));
        double distance = earthRadius * atan;
        return distance;
    }

    ;
    public void showNotification(String names)
    {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

        Notification notification = builder.setContentTitle( "You are nearby the contacts ")
                .setSmallIcon(R.drawable.image)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(names))
                .setContentText( names)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);




    }


}







