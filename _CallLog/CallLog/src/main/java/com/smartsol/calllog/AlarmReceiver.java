package com.smartsol.calllog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent i = new Intent(context, ReminderActivity.class);
        //context.startActivity(i);
        Log.i("Test", "working");
        Intent i = new Intent(context.getApplicationContext(), CallSummaryService.class);
        context.getApplicationContext().startService(i);
    }
}
