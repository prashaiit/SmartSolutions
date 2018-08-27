package com.smartsol.smstask;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class LockActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_lock);

        final DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        ComponentName mComponentName = new ComponentName(this, Admin.class);

        boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);

        Log.i("smsTask", "is Admin " + isAdmin);
        if (isAdmin) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mDevicePolicyManager.lockNow();
                }
            }, 10000);
        }

        this.finish();
    }
}
