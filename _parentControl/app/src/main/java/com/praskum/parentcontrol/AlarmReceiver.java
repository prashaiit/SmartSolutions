package com.praskum.parentcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "Alarm Received");

        if (intent.getAction().equalsIgnoreCase(Constants.TIMERINTENTACTION)) {
            int id = intent.getIntExtra("id", -1);

            if (id == Constants.RESET_SCREEN_OFF_TIMEOUT_ALARMID) {
                int screenOffTimeout = intent.getIntExtra("value", -1);
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screenOffTimeout);
            }
            else {
                DatabaseActionHelper dbHelper = new DatabaseActionHelper(context);
                Cursor c = dbHelper.ReadData(id);
                if (c != null && c.getCount() > 0) {
                    ActionRunner actionRunner = new ActionRunner();
                    actionRunner.ApplyActions(context, c);
                }

                boolean isDeleted = dbHelper.DeleteData(id);
                if (!isDeleted) {
                    Toast.makeText(context, "Failed to delete the record. Making second attempt", Toast.LENGTH_LONG).show();

                    isDeleted = dbHelper.DeleteData(id);
                    Toast.makeText(context, "Second attempt for delete : (success/fail) = " + isDeleted, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
