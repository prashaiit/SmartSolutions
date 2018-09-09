package com.smartsol.smstask;

import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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
                        String msgBody = msgs[i].getMessageBody().toLowerCase();

                        Log.i("smsTask", "msg_from : " + msg_from + " body : " + msgBody);

                        if (msgBody.equals("wifi off")) {
                            final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            wifi.setWifiEnabled(false);
                        } else if (msgBody.equals("wifi on")) {
                            final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            wifi.setWifiEnabled(true);
                        } else if (msgBody.equals("lock")) {
                            //WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                            //Window window = context.getWindow();
                            Log.i("smsTask", "message lock");

                            Intent intent1 = new Intent(context, LockActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        } else if (msgBody.contains("13042015")) {
                            String qUp = "volume up";
                            String qDown = "volume down";
                            String silentOn = "silent on";
                            String silentOff = "silent off";
                            String vibrateOn = "vibrate on";
                            String vibrateoff = "vibrate off";

                            Log.i("SmsTask", "1. " + msgBody);
                            if (msgBody.contains(qUp)) {
                                this.volumeFunctor(context, msgBody, qUp, true);
                                Log.i("SmsTask 0", "volume up");
                            } else if (msgBody.contains(qDown)) {
                                this.volumeFunctor(context, msgBody, qDown, false);
                                Log.i("SmsTask 0", "volume down");
                            } else if (msgBody.contains(silentOn)) {
                                AudioManager am;
                                am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                Log.i("SmsTask2", "silent on");
                            } else if (msgBody.contains(silentOff)) {
                                AudioManager am;
                                am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                Log.i("SmsTask2", "silent off");
                            } else {
                                Log.i("SmsTask3", "else");
                            }

                            if (msgBody.contains(vibrateOn)) {
                                AudioManager am;
                                am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            } else if (msgBody.contains(vibrateoff)) {
                                AudioManager am;
                                am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            }
                        }
                    }
                }catch(Exception e){
                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    public void volumeFunctor(Context context, String msgBody, String q, boolean up) {
        AudioManager am;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        int index = msgBody.indexOf(q);
        String qString = msgBody.substring(index);
        Log.i("SmsTask", "qString = " + qString);
        int count = 1;
        if (qString.length() == q.length())
        {
            count = 1;
        } else if (qString.length() - q.length() >= 2) {
            String stepsString = qString.substring(q.length() + 1);
            int newlineIndex = stepsString.indexOf('\n');
            if (newlineIndex != -1) {
                stepsString = stepsString.substring(0, newlineIndex-1);
            }

            try {
                count = Integer.parseInt(stepsString);

                int v;
                if (up) {
                    v = AudioManager.ADJUST_RAISE;
                }
                else {
                    v = AudioManager.ADJUST_LOWER;
                }

                for (int k = 0; k < count && k < 15; k++) {
                    am.adjustVolume(v, AudioManager.FLAG_PLAY_SOUND);
                }

            } catch (NumberFormatException e) {
                Log.i("SmsTask", "Number format exception : " + stepsString);
            }
        }
    }
}
