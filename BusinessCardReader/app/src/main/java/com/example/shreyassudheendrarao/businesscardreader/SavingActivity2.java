package com.example.shreyassudheendrarao.businesscardreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SavingActivity2 extends AppCompatActivity {

    String Name;
    String Address;
    String Email;
    String Phone;
    DBHelper dbhelper;
    boolean dragDropAppendsText = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving2);
        boolean flag =true;
        String Phone = "",Email="",Address1="",Address2="",Name="";
        dbhelper = new DBHelper();
        Intent intent = getIntent();
        String extra = intent.getExtras().getString("name");
        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
        ArrayList<String> test = getIntent().getStringArrayListExtra("test");
        EditText tv_name = (EditText) findViewById(R.id.name);
        EditText tv_addr = (EditText) findViewById(R.id.address);
        EditText tv_email = (EditText) findViewById(R.id.email);
        EditText tv_ph = (EditText) findViewById(R.id.phone);
        for (String t: test) {
            if (t.contains("Phone")|| t.contains("PHONE") || t.contains("c:")||t.contains("T:") ||t.contains("M:")|| t.contains("tel") || t.contains("Mob") || t.contains("+1") || t.contains("Tel") || t.contains("P:")|| t.contains("p.")
                    ||t.contains("Store:")||t.contains("Cell:")|| t.matches("(.*)^(\\d{10})|(([\\(]?([0-9]{3})[\\)]?)?[ \\.\\-]?([0-9]{3})[ \\.\\-]([0-9]{4}))$(.*)")) {
                Phone = t.replaceAll("[^0-9]", "");
                if (Phone.length() > 10) {
                    if(Phone.charAt(0) == '1') {
                        Phone = Phone.substring(0, 11);

                    } else {
                        Phone = Phone.substring(0, 10);
                    }
                }
                Log.d("Shreyas Phone = ", Phone);
            }
            else if (t.contains("Email") || t.contains("@")  ||
                    t.matches(".*^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$*.")) {
                Email = t.replaceAll("[^^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$]","");
                Log.d("Shreyas Email = ", Email);
            }

            else if ((t.contains("Address") ||t.contains("NJ")|| t.contains("NU")|| t.matches(".*^\\d{5}$*.") || t.matches("/^\\d+$/(.*)") ||t.contains("CA")
                    ||t.contains("CT")||t.contains("DE")||t.contains("FL")||t.contains("GA")||t.contains("IL")||t.contains("IN")||t.contains("MD")
                    ||t.contains("MI")||t.contains("NY")||t.contains("OH")||t.contains("PA")||t.contains("TX")||t.contains("WA")||t.contains("VA")) && t.matches(".*\\d+.*")) {
                Address2 = t;
                Log.d("Shreyas Address = ", Address2);
            } else if (Character.isDigit(t.charAt(0))) {
                Address1 = t;
                Log.d("Shreyas Address1 = ", Address1);
            } else if (t.contains(".com")) {
                Log.d("Shreyas website = ", t);
            }
            else {
                String[] words = t.split(" ");
                if (words.length == 2 && flag && !t.contains("Labs") && !t.contains("School") && !t.contains("Technology")&&  isAlpha(t) ) {
                    flag=false;
                    Name = t;
                    Log.d("Shreyas Name = ", Name);
                }
            }

        }

        tv_name.setText(Name);
        tv_ph.setText(Phone);
        tv_email.setText(Email);
        tv_addr.setText(Address1 + " "+Address2);


        // Creates a new drag event listener
        MyDragListener mDragListen_name = new MyDragListener();
        MyDragListener mDragListen_addr = new MyDragListener();
        MyDragListener mDragListen_email = new MyDragListener();
        MyDragListener mDragListen_phone = new MyDragListener();

        ListView lv = (ListView)findViewById(R.id.list);
        tv_name.setOnDragListener(mDragListen_name);
        tv_email.setOnDragListener(mDragListen_email);
        tv_addr.setOnDragListener(mDragListen_addr);
        tv_ph.setOnDragListener(mDragListen_phone);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,test);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                TextView txt = (TextView)v;

                ClipData data = ClipData.newPlainText("list text", txt.getText()+"");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        v);
                v.startDrag(data, shadowBuilder, v, 0);
                //v.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact newContact = new Contact();
                EditText tv_name = (EditText) findViewById(R.id.name);
                EditText tv_addr = (EditText) findViewById(R.id.address);
                EditText tv_email = (EditText) findViewById(R.id.email);
                EditText tv_ph = (EditText) findViewById(R.id.phone);

//        newContact.setName(tv_name.getText()+"");
//        newContact.setAddress(tv_addr.getText()+"");
//        newContact.setPhone(tv_ph.getText()+"");
//        newContact.setEmail(tv_email.getText()+"");

                dbhelper.insertIntoContactsTable(getApplicationContext(),tv_name.getText()+"",tv_addr.getText()+"",
                        tv_ph.getText()+"",tv_email.getText()+"");

                Intent intent = new Intent(SavingActivity2.this, ContactListActivity.class);
                startActivity(intent);
                SavingActivity2.this.finish();

            }
        });

        Switch dragDropSwitch = (Switch)findViewById(R.id.textSwitch);
        dragDropSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dragDropAppendsText = true;
                }
                else {
                    dragDropAppendsText= false;
                }
            }
        });
    }

    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c) && !(c==' ')) {
                return false;
            }
        }

        return true;
    }

    class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        v.invalidate();

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    EditText target = (EditText) v;
                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    // Gets the text data from the item.
                    String dragData = item.getText()+"";
                    //View dragg = (View) event.getLocalState();
                    //target.setText(target.getText()+ dragData);
                    // Invalidates the view to force a redraw
                    if(dragDropAppendsText)
                        target.setText(target.getText()+ dragData);
                    else
                        target.setText(dragData);
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Does a getResult(), and displays what happened.
//                    if (event.getResult()) {
//                        Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "The drop didn't work.", Toast.LENGTH_LONG);
//
//                    }

                    // returns true; the value is ignored.
                    return true;
                default:
                    break;
            }
            return true;
        }


    }
    public void onClickcancel (View view) {
        Intent newIntent = new Intent (this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }
}
