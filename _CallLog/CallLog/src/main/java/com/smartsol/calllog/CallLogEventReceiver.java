package com.smartsol.calllog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CallLogEventReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        addNotification(context, false);
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    //Derived classes should override these to respond to specific events of interest
    public void onIncomingCallReceived(Context ctx, String number, Date start) {

    }
    public  void onIncomingCallAnswered(Context ctx, String number, Date start) {

    }
    public void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        addNotification(ctx, true);
    }

    public void onOutgoingCallStarted(Context ctx, String number, Date start) {

    }
    public void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        addNotification(ctx, false);
    }

    public void onMissedCall(Context ctx, String number, Date start) {

    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallReceived(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                else
                {
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallAnswered(context, savedNumber, callStartTime);
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                }
                else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                else{
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }

    public void addNotification(Context context, boolean isIncoming)
    {
        int icon = R.drawable.ic_launcher_background;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, "Call Summary", when);

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher_background);
        contentView.setTextViewText(R.id.title, "Call Summary");
        if (!isIncoming) {
            contentView.setTextViewText(R.id.text, "time " + System.currentTimeMillis());
        } else {
            contentView.setTextViewText(R.id.text, "time " + System.currentTimeMillis());
        }

        notification.contentView = contentView;
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound


        mNotificationManager.notify(1, notification);



    }
}
