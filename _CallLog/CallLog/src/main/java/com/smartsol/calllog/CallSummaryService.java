package com.smartsol.calllog;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CallSummaryService extends Service {

    private CallLogEventReceiver cr;
    private TelephonyManager telephonyManager;
    private PhoneStateListener listener;
    private boolean isOnCall;
    private int prevState;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter ifilter = new IntentFilter();
        cr = new CallLogEventReceiver();
        registerReceiver(cr, ifilter);

        PhoneStateListener listener = new PhoneStateListener();

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter ifilter = new IntentFilter();
        cr = new CallLogEventReceiver();
        registerReceiver(cr, ifilter);

        // Create a new PhoneStateListener
        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (isOnCall) {
                            //showToast("Call state: idle");
                            if (prevState == TelephonyManager.CALL_STATE_RINGING ||
                                    prevState == TelephonyManager.CALL_STATE_OFFHOOK) {
                                addNotification(getApplicationContext(), false);
                            }
                            isOnCall = false;
                            prevState = TelephonyManager.CALL_STATE_IDLE;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        //showToast("Call state: offhook");
                        isOnCall = true;
                        prevState = TelephonyManager.CALL_STATE_OFFHOOK;
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        //showToast("call state: ringing");
                        prevState = TelephonyManager.CALL_STATE_RINGING;
                        break;
                }
            }
        };

        // Register the listener with the telephony manager
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cr);
        createService();
    }

    private int getTotalCallDuration(Date firstDate, Date lastDate) {
        StringBuffer sb = new StringBuffer();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return -1;
        }
        Cursor managedCursor = getApplicationContext().getContentResolver().query(contacts, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        int totalDuration = 0;
        while (managedCursor.moveToNext()) {

            HashMap rowDataCall = new HashMap<String, String>();

            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);

            Calendar currentTimeObj = Calendar.getInstance();
            currentTimeObj.setTimeInMillis(Long.valueOf(callDate));
            Date currentDateObj = new Date(Long.valueOf(callDate));
            currentTimeObj.set(Calendar.HOUR_OF_DAY, 0);
            currentTimeObj.set(Calendar.MINUTE, 0);
            currentTimeObj.set(Calendar.SECOND, 0);
            currentTimeObj.set(Calendar.MILLISECOND, 0);
            Date dateObj = new Date(currentTimeObj.getTimeInMillis());

            if (dateObj.compareTo(firstDate) < 0 || dateObj.compareTo(lastDate) > 0) {
                continue;
            }

            int dircode = Integer.parseInt(callType);
            if (dircode == CallLog.Calls.MISSED_TYPE)
                continue;

            String callDuration = managedCursor.getString(duration);
            int callDurationInt = Integer.parseInt(callDuration);
            totalDuration += callDurationInt;
        }
        managedCursor.close();
        //System.out.println(sb);

        return totalDuration;
    }

    public void addNotification(Context context, boolean isIncoming)
    {
        int icon = R.drawable.ic_launcher_background;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, "Call Summary", when);

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher_background);

        Calendar calInstance = Calendar.getInstance();
        calInstance.set(Calendar.HOUR_OF_DAY, 0);
        calInstance.set(Calendar.MINUTE, 0);
        calInstance.set(Calendar.SECOND, 0);
        calInstance.set(Calendar.MILLISECOND, 0);
        Date dateObj = new Date(calInstance.getTimeInMillis());
        int duration = getTotalCallDuration(dateObj, dateObj);
        int durationInMins = duration/60;

        if (durationInMins <= 30) {
            contentView.setTextViewText(R.id.title, "Call Summary");
            contentView.setTextViewText(R.id.text, "Check Todays Call Summary");
        } else {
            contentView.setTextViewText(R.id.title, "Call Summary");
            contentView.setTextViewText(R.id.text, "You exceeded maximum number of minutes");
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


    private void createService() {
        // create the pending intent
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                intent, 0);
        // get the alarm manager, and scedule an alarm that calls the receiver
        ((AlarmManager) getSystemService(ALARM_SERVICE)).set(
                AlarmManager.RTC, System.currentTimeMillis() + 5
                        * 1000, pendingIntent);

        Intent serviceIntent = new Intent(getApplicationContext(), AlarmService.class);
        startService(serviceIntent);
    }
}
