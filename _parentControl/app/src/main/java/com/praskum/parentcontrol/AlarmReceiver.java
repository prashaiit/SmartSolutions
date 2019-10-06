package com.praskum.parentcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.UniversalTimeScale;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "Alarm Received");

        int id = intent.getIntExtra("id", -1);
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Cursor c = dbHelper.ReadData(id);

        AudioManager am;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (c.getCount() > 0) {
            c.moveToFirst();
            if (c.getInt(c.getColumnIndex("Wifi")) == 1) {
                final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                Log.i("AlarmReceiver", "Switching off wifi");
                wifi.setWifiEnabled(false);
            }

            if (c.getInt(c.getColumnIndex("Silent")) == 1) {
                Log.i("AlarmReceiver", "Silent mode turned on");
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }

            if (c.getInt(c.getColumnIndex("SwitchToHomeScreen")) == 1) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.i("AlarmReceiver", "Switching to home screen");
                context.startActivity(startMain);

                int defaultTurnOffTime =  Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, 60000);
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 100);
                //Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defaultTurnOffTime);
            }

            int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            int newVolume = c.getInt(c.getColumnIndex("MediaVolume"));

            int delta = Math.abs(newVolume - currentVolume);
            int v = currentVolume > newVolume ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE;

            while (delta > 0) {
                delta--;
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, v, AudioManager.FLAG_PLAY_SOUND);
            }

            int newBrightness = c.getInt(c.getColumnIndex("Brightness"));
            if (newBrightness >= 0) {
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, newBrightness);
            }
        }

        boolean isDeleted = dbHelper.DeleteData(id);
        if (!isDeleted) {
            Toast.makeText(context, "Failed to delete the record", Toast.LENGTH_LONG).show();
        }
    }
}
