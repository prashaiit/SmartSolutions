package com.praskum.parentcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody().toLowerCase();


                        if (msgBody.equalsIgnoreCase("lock")) {
                            Intent intent1 = new Intent(context, LockActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        }
                        else if (msgBody.equalsIgnoreCase("chmod")) {
                            DatabaseActionHelper dbHelper = new DatabaseActionHelper(context);
                            Cursor c = dbHelper.ReadData(Constants.CHILDMODE_SMS_ACTION_ALARMID);
                            if (c != null && c.getCount() > 0) {
                                final Cursor settingsCursor = dbHelper.ReadData(Constants.CHILDMODE_ACTIONS);
                                if (settingsCursor != null && settingsCursor.getCount() > 0) {
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            ActionRunner actionRunner = new ActionRunner();
                                            actionRunner.ApplyActions(context, settingsCursor);
                                        }
                                    }, 10000);
                                }
                            }
                        }
                    }
                }
                catch(Exception e){
                    Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}
