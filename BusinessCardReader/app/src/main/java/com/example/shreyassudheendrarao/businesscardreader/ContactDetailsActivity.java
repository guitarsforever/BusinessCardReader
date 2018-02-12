package com.example.shreyassudheendrarao.businesscardreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ContactDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        Intent i = getIntent();
        String name = i.getStringExtra("name");
        DBHelper dbHelper = new DBHelper();
        Contact c = dbHelper.getContact(this, name);

        TextView tv_name = (TextView)findViewById(R.id.name);
        tv_name.setText(c.getName());

        TextView tv_addr = (TextView)findViewById(R.id.address);
        tv_addr.setText(c.getAddress());

        TextView tv_email = (TextView)findViewById(R.id.email);
        tv_email.setText(c.getEmail());

        TextView tv_ph = (TextView)findViewById(R.id.phone);
        tv_ph.setText(c.getPhone());
    }
}
