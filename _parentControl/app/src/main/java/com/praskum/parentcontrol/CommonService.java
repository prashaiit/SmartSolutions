package com.praskum.parentcontrol;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class CommonService extends Service {

    private CommonBroadCastReceiver commonBroadCastReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        commonBroadCastReceiver = new CommonBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(commonBroadCastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        if (commonBroadCastReceiver != null) {
            unregisterReceiver(commonBroadCastReceiver);
        }
    }
}
