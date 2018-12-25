package com.smartsol.calllog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DayLogAcitivy extends AppCompatActivity {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daylog_activiy);

        TextView textView = (TextView) findViewById(R.id.tittle);
        Intent intent = getIntent();
        String date = intent.getStringExtra("Date");
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(date);
        textView.setTextColor(getResources().getColor(R.color.actionbarColor));
        DateLog dLog = (DateLog) intent.getSerializableExtra("DateLog");

        LinearLayout layout = (LinearLayout) findViewById(R.id.daySummary);
        for (final Map.Entry<String, NumberWiseLog> entry : dLog.numberLogInfo.entrySet()) {
            Button tv = new Button(this.getApplicationContext());
            int callDurationWithANumber = entry.getValue().IncomingCallDuration + entry.getValue().OutgoingCallDuration;
            callDurationWithANumber = callDurationWithANumber/60;

            String phNumber = entry.getKey();
            String contactName = this.getContactName(phNumber);
            if (contactName.equals("")) {
                tv.setText(phNumber + " : " + callDurationWithANumber + " mins");
            } else {
                tv.setText(contactName + " (" + phNumber + ")"  + " : " + callDurationWithANumber + " mins");
            }

            tv.setTextColor(Color.WHITE);
            tv.setTransformationMethod(null);
            tv.setBackgroundResource(R.drawable.shape);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 30;
            tv.setLayoutParams(params);

            layout.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DayLogAcitivy.this, NumberLogActivity.class);
                    intent.putExtra("numberLog", (Serializable) entry.getValue());
                    startActivity(intent);
                }
            });
        }

        layout.invalidate();

        android.support.v7.app.ActionBar bar = this.getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbarColor)));
        }

        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.actionbarColor));
    }

    public String getContactName(final String phoneNumber)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=getApplicationContext().getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }
}
