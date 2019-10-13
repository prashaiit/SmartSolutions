package com.praskum.parentcontrol;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class AlarmActionHelper {

    public boolean CreateAlarm(Context context, int requestCode, int hours, int minutes, int seconds, int intentExtraValue) {
        if (hours == 0 && minutes == 0 && seconds == 0) {
            return false;
        }

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hours);
        calendar.add(Calendar.MINUTE, minutes);
        calendar.add(Calendar.SECOND, seconds);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(Constants.TIMERINTENTACTION);
        intent.putExtra("id", requestCode);

        if (intentExtraValue != -1) {
            intent.putExtra("value", intentExtraValue);
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Log.i("AlarmActionHelper", "ms = " + calendar.getTimeInMillis());
        Log.i("AlarmActionHelper", "current = " + System.currentTimeMillis());

        return true;
    }

    public void StopAlarm(Context context, int requestCode) {
        try {
            Log.i("AlarmActionHelper", "Cancel");
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
            Intent intent2 = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent2, PendingIntent.FLAG_NO_CREATE);
            Log.i("AlarmActionHelper", "null ? = " + (pendingIntent != null));
            alarmManager.cancel(pendingIntent);
        }
        catch (Exception e) {
            Log.d("AlarmActionHelper", "StopAlarm Failed with exception " + e.getMessage());
        }
    }
}
