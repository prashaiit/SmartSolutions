package com.praskum.parentcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "Alarm Received");

        int id = intent.getIntExtra("id", -1);
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Cursor c = dbHelper.ReadData(id);
        if (c.getCount() > 0) {
            c.moveToFirst();
            if (c.getInt(c.getColumnIndex("Wifi")) == 1) {
                final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                Log.i("AlarmReceiver", "Switching off wifi");
                wifi.setWifiEnabled(false);
            }

            if (c.getInt(c.getColumnIndex("Silent")) == 1) {
                AudioManager am;
                am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                Log.i("AlarmReceiver", "Silent mode turned on");
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }

            if (c.getInt(c.getColumnIndex("SwitchToHomeScreen")) == 1) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.i("AlarmReceiver", "Switching to home screen");
                context.startActivity(startMain);
            }
        }

        boolean isDeleted = dbHelper.DeleteData(id);
        if (!isDeleted) {
            Toast.makeText(context, "Failed to delete the record", Toast.LENGTH_LONG).show();
        }
    }
}
