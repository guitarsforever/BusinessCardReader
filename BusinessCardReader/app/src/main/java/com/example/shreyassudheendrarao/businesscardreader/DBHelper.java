package com.example.shreyassudheendrarao.businesscardreader;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
public class DBHelper {
    private static final String DATABASE_NAME = "card_readerDB.db";
    private static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_LOCATION = "LatLongTable";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";
    public static final String COLUMN_CONTACTID = "ContactId";
    static int id = 0;
    SQLiteDatabase db;
    ListView lv;
    ArrayList<Contact> contactList = new ArrayList<Contact>();
    ArrayList<LatLong> locList = new ArrayList<LatLong>();
    Cursor cursor, cursor1, cursor2;

    public void openOrCreateCardReaderDB(Context context){
        db = context.openOrCreateDatabase(DATABASE_NAME,0,null);
        try {
            //db.execSQL("DROP TABLE "+TABLE_LOCATIONS);
            //db.execSQL("DROP TABLE "+TABLE_CHECKINS);
            String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_CONTACTS + "("
                    + "id" + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + " VARCHAR(50)," +
                    COLUMN_ADDRESS + " VARCHAR(100)," +
                    COLUMN_EMAIL + " VARCHAR(50)," +
                    COLUMN_PHONE + " VARCHAR(15)" +")";
            db.execSQL(CREATE_CONTACTS_TABLE);
            String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_LOCATION + "("
                    + "LatLongid" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_LATITUDE + " double," +
                    COLUMN_LONGITUDE + " double," +
                    COLUMN_CONTACTID + " INTEGER" + ")";
            db.execSQL(CREATE_LOCATION_TABLE);
        }
        catch (Exception e) {
            Log.e("DBHelper", e.getMessage());
        }
    }

    public long insertIntoContactsTable(Context context,Contact contact){
        long id =0;
        try {
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }
            ContentValues cvContact = new ContentValues();
            cvContact.put(COLUMN_NAME, contact.getName());
            cvContact.put(COLUMN_ADDRESS, contact.getAddress());
            cvContact.put(COLUMN_PHONE, contact.getPhone());
            cvContact.put(COLUMN_EMAIL, contact.getEmail());
            id = db.insert(TABLE_CONTACTS, null, cvContact);
            return id;
        }
        catch (Exception e){
            Log.e("DBHelper", e.getMessage());
        }
        finally{
            db.close();
        }
        return id;
    }

    public long insertIntoContactsTable(Context context, String name, String address, String phone, String email ){
        long id =0;
        try {
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }
            ContentValues cvContact = new ContentValues();
            cvContact.put(COLUMN_NAME, name);
            cvContact.put(COLUMN_ADDRESS, address);
            cvContact.put(COLUMN_PHONE, phone);
            cvContact.put(COLUMN_EMAIL, email);
            id = db.insert(TABLE_CONTACTS, null, cvContact);
            return id;
        }
        catch (Exception e){
            Log.e("DBHelper", e.getMessage());
        }
        finally{
            db.close();
        }
        return id;
    }

    public void deleteFromContactsTable(Context context){
        try{
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }
            String DELETE_CONTACTS = "DELETE FROM "+TABLE_CONTACTS;
            db.execSQL(DELETE_CONTACTS);
        }
        catch (Exception e){
            Log.e("DBHelper", e.getMessage());
        }

    }

    public ArrayList<String> getContactNames(Context context){
        ArrayList<String> list = new ArrayList<String>();
        try{
           // db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }
            String Names = "SELECT name FROM "+ TABLE_CONTACTS;
            Cursor c = db.rawQuery(Names,null);
            if(c.moveToFirst()){
                c.moveToFirst();
                do{
                    list.add(c.getString(0));
                }while (c.moveToNext());
                c.close();
            }
        }
        catch (Exception e){
            Log.e("DBHelper", e.getMessage());
        }
        finally{
            db.close();
        }
        return list;
    }

    public Contact getContact(Context c,String name){
        Contact contact = new Contact();
        try {
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(c);
            }
            String getContact = "SELECT "+COLUMN_NAME+","+COLUMN_ADDRESS+","+COLUMN_EMAIL+","+COLUMN_PHONE+ " FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_NAME + " = '" + name+"'";
            Cursor cursor = db.rawQuery(getContact, null);

            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                contact.setName(cursor.getString(0));
                contact.setAddress(cursor.getString(1));
                contact.setEmail(cursor.getString(2));
                contact.setPhone(cursor.getString(3));
                cursor.close();
            }
        }
        catch (Exception e){
            Log.e("DBHelper", e.getMessage());
        }
        finally{
            db.close();
        }

        return contact;
    }

    public long insertIntoLocationTable(Context context, double latitude, double longitude, int contactId) {
        long id = 0;
        try {
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }
            ContentValues cvContact = new ContentValues();
            cvContact.put(COLUMN_LATITUDE, latitude);
            cvContact.put(COLUMN_LONGITUDE, longitude);
            cvContact.put(COLUMN_CONTACTID, contactId);

            id = db.insert(TABLE_LOCATION, null, cvContact);
            return id;
        } catch (Exception e) {
            Log.e("DBHelper", e.getMessage());
        } finally {
            db.close();
        }
        return id;
    }



    public ArrayList<Contact> getContactList(Context context) {

        try {
            // db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }

            String query = "SELECT name,address,phone,email FROM " + TABLE_CONTACTS;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                c.moveToFirst();
                do {
                    Contact contact = new Contact();
                    contact.setName(c.getString(0));
                    contact.setAddress(c.getString(1));
                    contact.setEmail(c.getString(2));
                    contact.setPhone(c.getString(3));
                    contactList.add(contact);
                    // locList.add(contact);
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e) {
            Log.e("DBHelper", e.getMessage());
        } finally {
            db.close();
        }
        // return locList;
        return contactList;
    }


    public int getContactId(Context context, String address) {

        try {
            // db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }
            cursor = db.rawQuery("Select id from contacts where address = " + "'" + address + "'", null);

            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);

            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DBHelper", e.getMessage());
        } finally {
            db.close();
        }

        return id;
    }


    public ArrayList<LatLong> getLocList(Context context) {
        double latitude, longitude;
        int contactId;
        String name = "";
        try {

            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }
            String query = "SELECT latitude,longitude,contactId FROM " + TABLE_LOCATION;


            cursor2 = db.rawQuery(query, null);

            Log.v("MyDebug", "Starting to read cursor");
            while (cursor2.moveToNext()) {
                LatLong latLngObj = new LatLong();
                latitude = cursor2.getDouble(0);
                longitude = cursor2.getDouble(1);
                contactId = cursor2.getInt(2);
                //   cursor1 = MainActivity.db.rawQuery("Select name from contacts where Id =" + contactId, null);
                Log.v("MyDebug", "Location values: " + latitude + "," + longitude + "," + contactId);

                cursor1 = db.rawQuery("Select name from contacts where id = " + contactId, null);
                while (cursor1.moveToNext()) {
                    name = cursor1.getString(0);
                    Log.v("MyDebug", "Contact values:" + name);
                }
                latLngObj.setLatitude(latitude);
                latLngObj.setLongitude(longitude);
                latLngObj.setName(name);
                locList.add(latLngObj);
            }
            cursor.close();
            cursor1.close();
        } catch (Exception e) {
            Log.e("DBHelper", e.getMessage());
        } finally {
            db.close();
        }

        return locList;

    }

    public void deleteFromLocTable(Context context) {
        try {
            if (db == null || !db.isOpen()) {
                openOrCreateCardReaderDB(context);
            }

            String DELETE_LOCATIONS = "DELETE FROM " + TABLE_LOCATION;
            db.execSQL(DELETE_LOCATIONS);

        } catch (Exception e) {
            Log.e("DBHelper", e.getMessage());
        }

    }
}
