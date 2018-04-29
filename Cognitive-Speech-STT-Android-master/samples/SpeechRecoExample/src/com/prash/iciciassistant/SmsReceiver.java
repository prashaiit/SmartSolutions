package com.prash.iciciassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by praskum on 4/8/2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    private Context _context;
    public static DatabaseHandler db;

    public SmsReceiver() {
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        _context = context;

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                Log.i("test", "pdus length " + pdusObj.length);

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("test", "senderNum: "+ senderNum + "; message: " + message);
                    //SmsDbApplication.addBill(message);
                    File f = new File(SmsData.getFilepath());
                    Log.i("test", "canWrite " + f.canWrite());

                    try
                    {
                        String filename= SmsData.getFilepath();
                        if (!filename.isEmpty()) {
                            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
                            fw.write("\n" + message);
                            fw.close();
                        }
                    }
                    catch(IOException ioe)
                    {
                        System.err.println("IOException: " + ioe.getMessage());
                    }

                }
            }

        } catch (Exception e) {
            Log.e("test", "Exception smsReceiver" +e);

        }
    }


}
