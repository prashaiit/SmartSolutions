package com.prash.iciciassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by praskum on 4/8/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "smsBills";

    // Contacts table name
    private static final String TABLE_NAME = "bills";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PROVIDER = "provider";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DUEDATE = "duedate";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PROVIDER + " TEXT,"
                + KEY_AMOUNT + " TEXT," + KEY_DUEDATE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void addBill(BillPayItem billPayItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID, billPayItem.getID());
        values.put(KEY_PROVIDER, billPayItem.getProvider());
        values.put(KEY_AMOUNT, billPayItem.getAmount());
        values.put(KEY_DUEDATE, billPayItem.getDuedate());

        // Inserting Row
        Log.i("test", "insert");
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public BillPayItem getBillItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_PROVIDER, KEY_AMOUNT, KEY_DUEDATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        BillPayItem billPayItem = new BillPayItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));

        return billPayItem;
    }

    public List<BillPayItem> getAllBills() {
        List<BillPayItem> billPayItemList = new ArrayList<BillPayItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BillPayItem billPayItem = new BillPayItem();
                billPayItem.setId(Integer.parseInt(cursor.getString(0)));
                billPayItem.setProvider(cursor.getString(1));
                billPayItem.setAmount(cursor.getString(2));
                billPayItem.setAmount(cursor.getString(3));
                // Adding contact to list
                billPayItemList.add(billPayItem);
            } while (cursor.moveToNext());
        }

        return billPayItemList;
    }

    public int getBillsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int updateBill(BillPayItem billPayItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROVIDER, billPayItem.getProvider());
        values.put(KEY_AMOUNT, billPayItem.getAmount());
        values.put(KEY_DUEDATE, billPayItem.getDuedate());

        // updating row
        return db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(billPayItem.getID()) });
    }

    public void deleteBill(BillPayItem billPayItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(billPayItem.getID()) });
        db.close();
    }
}