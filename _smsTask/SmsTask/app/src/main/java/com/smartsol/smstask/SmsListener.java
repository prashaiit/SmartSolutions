package com.smartsol.smstask;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SmsListener extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        Log.i("smsTask", "msg_from : " + msg_from + " body : " + msgBody);

                        if (msgBody.toLowerCase().equals("wifi off")) {
                            final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            wifi.setWifiEnabled(false);
                        } else if (msgBody.toLowerCase().equals("wifi on")) {
                            final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            wifi.setWifiEnabled(true);
                        } else if (msgBody.toLowerCase().equals("lock")) {
                            //WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                            //Window window = context.getWindow();
                            Log.i("smsTask", "message lock");

                            Intent intent1 = new Intent(context, LockActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        }
                    }
                }catch(Exception e){
                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}
