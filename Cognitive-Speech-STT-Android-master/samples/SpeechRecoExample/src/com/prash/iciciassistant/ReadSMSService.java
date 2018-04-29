package com.prash.iciciassistant;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by praskum on 4/8/2017.
 */

public class ReadSMSService extends Service {
    private SmsReceiver smsReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Test", "on start mehtod (service)");

        return START_NOT_STICKY; //killed when app is killed
        //return START_STICKY; //restarts when the app is killed
    }

    @Override
    public void onCreate() {
        // super.onCreate();

        Log.i("Test", "on create mehtod");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        smsReceiver = new SmsReceiver();
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        Log.i("Test", "on destroy mehtod");
        unregisterReceiver(smsReceiver);
    }
}
