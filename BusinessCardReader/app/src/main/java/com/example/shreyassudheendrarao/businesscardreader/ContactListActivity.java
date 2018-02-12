package com.example.shreyassudheendrarao.businesscardreader;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.location.Address;

public class ContactListActivity extends AppCompatActivity {
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    ListView lv;
    DBHelper dbhelper;
    int contactId;
    static double latitude, longitude;
    List<Address> addressList;
    String address;
    ArrayList<Contact> contactArrayList;
    ImageButton map,Geo;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    map.setEnabled(false);
                    Geo.setEnabled(false);
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        lv = (ListView) findViewById(R.id.listView);
        dbhelper = new DBHelper();
        map = (ImageButton) findViewById(R.id.mapview);
        Geo = (ImageButton) findViewById(R.id.GeoReminder);
        ArrayList<String> test = dbhelper.getContactNames(this);
        contactArrayList = new ArrayList<Contact>();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, test);
        lv.setAdapter(adapter);
        Thread thread = new Thread (new Runnable() {
            @Override
            public void run() {
                createTable();
                getLatLong();
            }
        });
        thread.start();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                Intent newIntent = new Intent (ContactListActivity.this, ContactDetailsActivity.class);
                newIntent.putExtra("name", value);
                startActivity(newIntent);
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    public void onClickDelete (View v) {
        dbhelper.deleteFromContactsTable(this);
    }

    public void onClickMap (View view) {
        Intent newIntent = new Intent (this, MapActivity.class);
        startActivity(newIntent);
    }

    public void onClickGeo (View view) {
        Intent newIntent = new Intent (this, GeoReminderActivity.class);
        startActivity(newIntent);
    }


    public void getLatLong() {

        contactArrayList = dbhelper.getContactList(getApplicationContext());
        Geocoder gc = new Geocoder(this, Locale.getDefault());

        for (Contact contact : contactArrayList) {
            address = contact.getAddress();
            try {
                addressList = gc.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address currentAddress = addressList.get(0);
                    latitude = currentAddress.getLatitude();
                    longitude = currentAddress.getLongitude();
                    contactId = dbhelper.getContactId(getApplicationContext(), address);
                    insertLatLongTable(latitude, longitude, contactId);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
    public void createTable() {

        dbhelper.deleteFromLocTable(getApplicationContext());
        dbhelper.openOrCreateCardReaderDB(getApplicationContext());
    }

    public void insertLatLongTable(Double latitude, Double longitude, int id) {

        dbhelper.insertIntoLocationTable(getApplicationContext(), latitude, longitude, id);
    }

}
