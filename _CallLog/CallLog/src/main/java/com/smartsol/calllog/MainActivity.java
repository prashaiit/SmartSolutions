package com.smartsol.calllog;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private NotificationManager mNotificationManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ScrollView sv = (ScrollView) findViewById(R.id.scrollview);
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        createService();

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbarColor)));
        }

        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.actionbarColor));
    }

    private void createService() {
        // create the pending intent
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                intent, 0);
        // get the alarm manager, and scedule an alarm that calls the receiver
        ((AlarmManager) getSystemService(ALARM_SERVICE)).set(
                AlarmManager.RTC, System.currentTimeMillis() + 2
                        * 1000, pendingIntent);

        Intent serviceIntent = new Intent(MainActivity.this, AlarmService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.removeAllViews();

        Calendar calInstance = Calendar.getInstance();
        calInstance.set(Calendar.HOUR_OF_DAY, 0);
        calInstance.set(Calendar.MINUTE, 0);
        calInstance.set(Calendar.SECOND, 0);
        calInstance.set(Calendar.MILLISECOND, 0);

        //calInstance.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDateObj = new Date(calInstance.getTimeInMillis());
        calInstance.add(Calendar.DAY_OF_MONTH, -6);
        Date firstDateObj = new Date(calInstance.getTimeInMillis());

        Map<Date, DateLog> infoMap = this.getCallDetails(firstDateObj, lastDateObj);
        final SimpleDateFormat formater = new SimpleDateFormat("MMM dd, yyyy");

        for (final Map.Entry<Date, DateLog> entry : infoMap.entrySet()) {
            int totalduration = entry.getValue().IncomingCallDuration + entry.getValue().OutgoingCallDuration;
            totalduration = totalduration/60;

            final Date date = entry.getKey();
            String text = formater.format(date) + " : " + totalduration + " minutes \n";

            Button b = new Button(this.getApplicationContext());
            b.setTransformationMethod(null);

           // b.setPadding(5,5,5,5);

            b.setText(text);

            if (totalduration > 30) {
                //b.setBackgroundColor(Color.RED);
                b.setTextColor(Color.WHITE);
                b.setBackgroundResource(R.drawable.shape_red);
            } else {
                //b.setBackgroundColor(Color.GREEN);
                b.setTextColor(Color.WHITE);
                b.setBackgroundResource(R.drawable.shape);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 30;
            b.setLayoutParams(params);
            layout.addView(b);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DayLogAcitivy.class);
                    SimpleDateFormat formater2 = new SimpleDateFormat("EEEE, MMM dd, yyyy");
                    intent.putExtra("Date", formater2.format(date));
                    intent.putExtra("DateLog", (Serializable) entry.getValue());
                    startActivity(intent);
                }
            });
        }

        layout.invalidate();

    }

    private Map<Date, DateLog> getCallDetails(Date firstDate, Date lastDate) {
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
            return null;
        }
        Cursor managedCursor = getApplicationContext().getContentResolver().query(contacts, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        Map<Date, DateLog> infoMap = new HashMap<>();
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

            DateLog dLog;
            if (infoMap.containsKey(dateObj)) {
                dLog = infoMap.get(dateObj);
            } else {
                dLog = new DateLog();
                infoMap.put(dateObj, dLog);
            }

            String callDayTime = currentDateObj.toString();
            // long timestamp = convertDateToTimestamp(callDayTime);
            String callDuration = managedCursor.getString(duration);
            int callDurationInt = Integer.parseInt(callDuration);
            String dir = null;


            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    dLog.OutgoingCallDuration += callDurationInt;
                    this.updateNumberWiseLog(dLog, true, callDurationInt, phNumber, currentDateObj);
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    dLog.IncomingCallDuration += callDurationInt;
                    this.updateNumberWiseLog(dLog, false, callDurationInt, phNumber, currentDateObj);
                    break;
            }
            //sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            //sb.append("\n----------------------------------");


        }
        managedCursor.close();
        //System.out.println(sb);

       // return sb.toString();
        return infoMap;
    }

    public void updateNumberWiseLog(DateLog dLog, boolean isOutgoing, int duration, String phnumber, Date time) {
        NumberWiseLog numberWiseLog;
        if (dLog.numberLogInfo.containsKey(phnumber)) {
            numberWiseLog = dLog.numberLogInfo.get(phnumber);
        } else {
            numberWiseLog = new NumberWiseLog();
            dLog.numberLogInfo.put(phnumber, numberWiseLog);
        }

        if (isOutgoing) {
            numberWiseLog.OutgoingCallDuration += duration;
        } else {
            numberWiseLog.IncomingCallDuration += duration;
        }

        int minutes = duration/60;
        int seconds = duration % 60;
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
        String datetime = formatter.format(time);

        String callDurationTime = "";
        if (minutes > 0) {
            callDurationTime += minutes + " min";
        }

        callDurationTime += " " + seconds + " sec";
        numberWiseLog.details.add(datetime + " : " + callDurationTime);
    }

    public void addNotification(View view)
    {
        int icon = R.drawable.ic_launcher_background;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, "Custom Notification", when);

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher_background);
        contentView.setTextViewText(R.id.title, "Custom notification");
        long text1 = new Date().getTime();
        contentView.setTextViewText(R.id.text, "This is a custom layout " + text1);
        notification.contentView = contentView;
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound


        mNotificationManager.notify(1, notification);
    }

    @Override
    protected void onStop() {
        super.onStop();
        createService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        createService();
    }


}
