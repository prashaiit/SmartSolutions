package com.praskum.parentcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("smsReceived", "intent = " + intent.getAction());
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    DatabaseActionHelper db = new DatabaseActionHelper(context);
                    Cursor cursor = db.ReadAllContacts();
                    HashSet<String> validMobileNumbers = new HashSet<>();

                    if (cursor != null && cursor.getCount() > 0) {
                        if (cursor.moveToFirst()) {
                            do {
                                String mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
                                if (!validMobileNumbers.contains(mobile)) {
                                    validMobileNumbers.add(mobile);
                                }
                            }
                            while (cursor.moveToNext());
                        }

                        cursor.close();
                    }

                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        Utils utils = new Utils();
                        String nMobile = utils.getNormalizedPhoneNumber(msg_from);

                        if (!validMobileNumbers.contains(nMobile)) {
                            continue;
                        }

                        if (!PermissionChecker.HasReadSmsPermission(context)) {
                            continue;
                        }

                        String msgBody = msgs[i].getMessageBody().toLowerCase();
                        DatabaseActionHelper dbHelper = new DatabaseActionHelper(context);
                        Cursor lockCursor = dbHelper.ReadData(Constants.LOCKSCREEN_SMS_ACTION_ALARMID);
                        Cursor kidModeCursor = dbHelper.ReadData(Constants.CHILDMODE_SMS_ACTION_ALARMID);
                        if (lockCursor != null && lockCursor.getCount() > 0 && msgBody.equalsIgnoreCase("lock")) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    ActionRunner actionRunner = new ActionRunner();
                                    actionRunner.ApplyLockAction(context);
                                }
                            }, 10000);
                        }
                        else if (kidModeCursor != null && kidModeCursor.getCount() > 0 && msgBody.equalsIgnoreCase("kidmode")) {
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
                catch(Exception e){
                    Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}
