package com.praskum.parentcontrol;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class TimerActionActivity extends Activity {

    private TextView hour, min, sec;
    private int value = 0;
    private int alarmId;
    private Button createAction, deleteAtion;
    private ToggleButton lock, switch2Home, wifi, silentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeraction);
        hour = (TextView) findViewById(R.id.hours);
        min = (TextView) findViewById(R.id.mintues);
        sec = (TextView) findViewById(R.id.seconds);
        createAction = (Button) findViewById(R.id.createTimerAction);
        deleteAtion = (Button) findViewById(R.id.deleteTimerAction);
        lock = (ToggleButton) findViewById(R.id.lock);
        switch2Home = (ToggleButton) findViewById(R.id.switchtohome);
        wifi = (ToggleButton) findViewById(R.id.wifi);
        silentMode = (ToggleButton) findViewById(R.id.silent);

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToggleButton(lock);
            }
        });

        switch2Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToggleButton(switch2Home);
            }
        });

        silentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToggleButton(silentMode);
            }
        });

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToggleButton(wifi);
            }
        });

        alarmId = getIntent().getIntExtra("AlarmId", -1);
    }

    protected void UpdateToggleButton(ToggleButton toggleButton) {
        if (toggleButton.isChecked()) {
            toggleButton.setBackgroundResource(R.drawable.buttonshapeclicked);
            toggleButton.setTextColor(getResources().getColor(R.color.colorbackground));
        }
        else {
            toggleButton.setBackgroundResource(R.drawable.buttonshape);
            toggleButton.setTextColor(getResources().getColor(R.color.colorforeground));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Addtimer", "smsaction resume");
        UpdateTextView();

        if (alarmId == -1) {
            createAction.setVisibility(View.VISIBLE);
            deleteAtion.setVisibility(View.INVISIBLE);

            if (value == 0) {
                createAction.setEnabled(false);
            }
            else {
                createAction.setEnabled(true);
            }
        }
        else {
            createAction.setVisibility(View.INVISIBLE);
            deleteAtion.setVisibility(View.VISIBLE);

            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            Log.i("TimerAction", "Reading the data for " + alarmId);
            Cursor c = dbHelper.ReadData(alarmId);
            lock.setChecked(c.getInt(c.getColumnIndex("Lock")) == 1);
            switch2Home.setChecked(c.getInt(c.getColumnIndex("SwitchToHomeScreen")) == 1);
            wifi.setChecked(c.getInt(c.getColumnIndex("Wifi")) == 1);
            silentMode.setChecked(c.getInt(c.getColumnIndex("Silent")) == 1);
            Log.i("timeraction", "values read = " + c.getInt(1) + " , " + c.getInt(2));
        }

        UpdateToggleButton(lock);
        UpdateToggleButton(wifi);
        UpdateToggleButton(silentMode);
        UpdateToggleButton(switch2Home);
    }

    public void TriggerAddTimerActivity(View view) {
        Intent intent = new Intent(TimerActionActivity.this, AddTimerActivity.class);
        startActivityForResult(intent, 6);
    }

    public void CreateTimerAction(View view) {
        Log.i("smsAction", "createTimer");
        Random random = new Random();
        int randomnumber = random.nextInt(1000);

        TimerActionDataModel timerActionDataModel = new TimerActionDataModel();
        timerActionDataModel.AlarmId = randomnumber;
        timerActionDataModel.LockScreen = lock.isChecked();
        timerActionDataModel.SwitchToHome = switch2Home.isChecked();
        timerActionDataModel.SilentMode = silentMode.isChecked();
        timerActionDataModel.WifiMode = wifi.isChecked();

        DatabaseHelper dbhelper = new DatabaseHelper(getApplicationContext());
        boolean isSuccess = dbhelper.InsertData(timerActionDataModel);

        int i = 0;
        while (!isSuccess && i < 10) {
            randomnumber = random.nextInt(1000);
            timerActionDataModel.AlarmId = randomnumber;
            isSuccess = dbhelper.InsertData(timerActionDataModel);
            i++;
        }

        if (isSuccess) {
            AlarmActionHelper helper = new AlarmActionHelper();
            int h = Integer.parseInt(hour.getText().toString());
            int m = Integer.parseInt(min.getText().toString());
            int s = Integer.parseInt(sec.getText().toString());
            helper.CreateAlarm(getApplicationContext(), randomnumber, h, m, s);

            Toast.makeText(getApplicationContext(), "Timer Action Created", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Failed to create a Timer Action", Toast.LENGTH_LONG).show();
        }
    }

    public void DeleteTimerAction(View view){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.DeleteData(alarmId);

        AlarmActionHelper actionHelper = new AlarmActionHelper();
        actionHelper.StopAlarm(getApplicationContext(), alarmId);

        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("addtimer", "OnActivityResult");
        Log.i("addtimer", "req = " + requestCode + " resCode " + resultCode);
        if (requestCode == 6 && resultCode == 5) {
            value = data.getIntExtra("timer", 0);
        }
        else {
            value = 0;
        }
    }

    private void UpdateTextView(){
        Utils utils = new Utils();
        sec.setText(utils.getSecValue(value));
        min.setText(utils.getMinValue(value));
        hour.setText(utils.getHourValue(value));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
