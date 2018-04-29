package com.praskum.iciciassistant;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by praskum on 4/8/2017.
 */

public final class SmsDbApplication {

    public static DatabaseHandler db;

    private SmsDbApplication() {

    }

    public static DatabaseHandler createDB(Context context) {
        if (db != null) return db;
        db = new DatabaseHandler(context);
        return db;
    }

    public static void addBill(String message) {

        db.addBill(new BillPayItem("airtel", "300", "28/04/17"));

        Log.i("test", "Reading all Bills..");
        List<BillPayItem> bills = db.getAllBills();

        for (BillPayItem cn : bills) {
            String log = "Id: " + cn.getID() + " ,Provider: " + cn.getProvider() + " ,Amount: " + cn.getAmount() + " ,duedate : " + cn.getDuedate();
            Log.i("test", "added to the DB " + log);
        }
    }

    public static DatabaseHandler getDb() {
        return db;
    }
}
