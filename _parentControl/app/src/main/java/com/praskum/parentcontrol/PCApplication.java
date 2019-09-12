package com.praskum.parentcontrol;

import android.app.Application;
import android.content.IntentFilter;

public class PCApplication extends Application {

    private AlarmReceiver receiver;

    public PCApplication() {
        receiver = new AlarmReceiver();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.TIMERINTENTACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(receiver);
        super.onTerminate();
    }
}
