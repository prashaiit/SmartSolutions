package com.praskum.parentcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "parentcontrol.db";
    private static final String TABLE_NAME = "timerActionsTable";
    private static final String ALARMID = "AlarmId";
    private static final String LOCK = "Lock";
    private static final String SWITCH2HOME = "SwitchToHomeScreen";
    private static final String WIFI = "Wifi";
    private static final String SILENT = "Silent";
    private static final String COMMASPACE = ", ";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (AlarmId INTEGER PRIMARY KEY, Lock INTEGER DEFAULT 0, " +
                "SwitchToHomeScreen INTEGER DEFAULT 0, Wifi INTEGER DEFAULT 0, Silent INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean InsertData(int alarmId, boolean lock, boolean switchToHome, boolean wifi, boolean silentMode) {
        Cursor c = this.ReadData(alarmId);
        if (c != null && c.getCount() > 0) {
            c.close();
            return false;
        }

        if (c != null)
            c.close();

        ContentValues values = new ContentValues();
        values.put(ALARMID, alarmId);
        values.put(LOCK, lock ? 1 : 0);
        values.put(SWITCH2HOME, switchToHome ? 1 : 0);
        values.put(WIFI, wifi ? 1 : 0);
        values.put(SILENT, silentMode ? 1 : 0);

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_NAME, null, values);

        if (result == -1) {
            return false;
        }

        Log.i("db", "Inserted Alarm Id " + alarmId);
        Log.i("db", "values inserted " + lock + ", " + switchToHome);
        db.close();
        return true;
    }

    public Cursor ReadData(int alarmId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c =  db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ALARMID + " = '" + alarmId + "'", null);
            if (c != null && c.getCount() > 0) {
                 c.moveToFirst();
            }
            else {
                Log.i("db", "alarmdi = " + alarmId + " Not Found");
            }

            db.close();
            return c;
        }
        catch (Exception e) {
            Log.i("db", "readData failed");
        }

        db.close();
        return null;
    }

    public boolean DeleteData(int alarmId) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // db.rawQuery("DELETE FROM " + TABLE_NAME + " WHERE " + ALARMID + " = '" + alarmId + "'", null);
            // return true;
            db.delete(TABLE_NAME, ALARMID + " = ?", new String[]{String.valueOf(alarmId)});
            db.close();
            Log.i("db", "deleted " + alarmId);
            return true;
        }
        catch (Exception e) {
            Log.i("db", e.getMessage());
        }

        return false;
    }

    public Cursor ReadAllData() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c =  db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
            Log.i("db", "No of Records Found = " + c.getCount());
            db.close();
            return c;
        }
        catch (Exception e) {
            Log.i("db", "ReadAllData failed");
        }

        db.close();
        return null;
    }

    public void DeleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
