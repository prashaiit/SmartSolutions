package com.praskum.parentcontrol;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

import static android.view.View.VISIBLE;

public class TimerActionActivity extends AppCompatActivity {

    private TextView hour, min, sec;
    private int value = 0;
    private int alarmId;
    private Button createAction, deleteAtion;
    private ToggleButton lock, switch2Home, wifi, silentMode, brightnessWritePermission, turnOffScreen;
    private SeekBar mediaVolume, brightness;
    private static final int ADMIN_INTENT = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private boolean adminPermissionJustProvided = false;

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
        mediaVolume = (SeekBar) findViewById(R.id.mediavolume);
        brightness = (SeekBar) findViewById(R.id.brightness);
        brightnessWritePermission = (ToggleButton) findViewById(R.id.brightnesswritepermissions);
        turnOffScreen = (ToggleButton) findViewById(R.id.turnoffscreen);

        lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PermissionChecker.PromptForDeviceAdminPermission(TimerActionActivity.this, false);
                }

                UpdateToggleButton(lock);
            }
        });

        switch2Home.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UpdateToggleButton(switch2Home);
            }
        });

        silentMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UpdateToggleButton(silentMode);
            }
        });

        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UpdateToggleButton(wifi);
            }
        });

        mediaVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("seekbar", "value " + progress);

                UpdateMediaVolumeSeekbarColor(mediaVolume, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                UpdateBrightnessSeekbarColor(brightness, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        brightnessWritePermission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PermissionChecker.PromptForWriteSettingPermission(TimerActionActivity.this, 501, false);
                }

                UpdateToggleButton(brightnessWritePermission);
                UpdateBrightnessControl();
            }
        });

        turnOffScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PermissionChecker.PromptForWriteSettingPermission(TimerActionActivity.this, 501, false);
                }
                UpdateToggleButton(turnOffScreen);
            }
        });

        alarmId = getIntent().getIntExtra("AlarmId", -1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tilebackground)));
        actionBar.setTitle(R.string.timer_actions);
        actionBar.show();

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.tilebackground));
        }
    }

    protected void UpdateToggleButton(ToggleButton toggleButton) {
        if (toggleButton.isChecked()) {
            toggleButton.setBackgroundResource(R.drawable.buttonshapeclicked);
            //toggleButton.setTextColor(getResources().getColor(R.color.colorbackground));
        }
        else {
            toggleButton.setBackgroundResource(R.drawable.buttonshape);
            //toggleButton.setTextColor(getResources().getColor(R.color.colorforeground));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Addtimer", "smsaction resume");
        UpdateTextView();

        int isLockEnabled = 0;

        if (alarmId != -1) {
            createAction.setVisibility(View.INVISIBLE);
            deleteAtion.setVisibility(VISIBLE);

            DatabaseActionHelper dbHelper = new DatabaseActionHelper(getApplicationContext());
            Log.i("TimerAction", "Reading the data for " + alarmId);
            Cursor c = dbHelper.ReadData(alarmId);

            if (c != null && c.getCount() > 0) {
                isLockEnabled = c.getInt(c.getColumnIndex("Lock"));
                switch2Home.setChecked(c.getInt(c.getColumnIndex("SwitchToHomeScreen")) == 1);
                wifi.setChecked(c.getInt(c.getColumnIndex("Wifi")) == 1);
                silentMode.setChecked(c.getInt(c.getColumnIndex("Silent")) == 1);
                turnOffScreen.setChecked(c.getInt(c.getColumnIndex("ScreenOff")) == 1);
                mediaVolume.setProgress(c.getInt(c.getColumnIndex("MediaVolume")));
                int brgness = c.getInt(c.getColumnIndex("Brightness"));
                if (brgness != -1) {
                    brightness.setProgress(brgness);
                }
                Log.i("timeraction", "values read = " + c.getInt(1) + " , " + c.getInt(2));
            }
        }
        else {
            createAction.setVisibility(VISIBLE);
            deleteAtion.setVisibility(View.INVISIBLE);

            if (value == 0) {
                createAction.setEnabled(false);
            }
            else {
                createAction.setEnabled(true);
            }
        }

        lock.setChecked((isLockEnabled == 1 && PermissionChecker.GetDevPolMgrWithAdmnPerm(getApplicationContext()) != null) || adminPermissionJustProvided);

        UpdateToggleButton(lock);
        UpdateToggleButton(wifi);
        UpdateToggleButton(silentMode);
        UpdateToggleButton(switch2Home);
        UpdateToggleButton(brightnessWritePermission);
        UpdateMediaVolume();
        UpdateBrightnessControl();
        UpdateScreenTurnOffControl();
    }

    public void UpdateMediaVolume() {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i("seekbar", "max vol " + am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        Log.i("seekbar", "curr vol " + currentVolume);
        mediaVolume.setProgress(currentVolume);
        UpdateMediaVolumeSeekbarColor(mediaVolume, currentVolume);
    }

    public void TriggerAddTimerActivity(View view) {
        Intent intent = new Intent(TimerActionActivity.this, AddTimerActivity.class);
        startActivityForResult(intent, 6);
    }

    public void CreateTimerAction(View view) {
        Log.i("smsAction", "createTimer");
        Random random = new Random();
        int randomnumber = random.nextInt(1000);

        DatabaseDataModel timerActionDataModel = new DatabaseDataModel();
        timerActionDataModel.AlarmId = randomnumber;
        timerActionDataModel.LockScreen = lock.isChecked();
        timerActionDataModel.SwitchToHome = switch2Home.isChecked();
        timerActionDataModel.SilentMode = silentMode.isChecked();
        timerActionDataModel.WifiMode = wifi.isChecked();
        timerActionDataModel.MediaVolume= mediaVolume.getProgress();

        if (brightness.isEnabled() && brightnessWritePermission.isChecked()) {
            timerActionDataModel.Brightness = brightness.getProgress();
        }
        else {
            timerActionDataModel.Brightness = -1;
        }

        timerActionDataModel.ScreenOff = turnOffScreen.isChecked();

        DatabaseActionHelper dbhelper = new DatabaseActionHelper(getApplicationContext());
        boolean isSuccess = dbhelper.InsertData(timerActionDataModel);

        int i = 0;
        while (!isSuccess && i < 10) {
            randomnumber = random.nextInt(1000);
            timerActionDataModel.AlarmId = randomnumber;
            isSuccess = dbhelper.InsertData(timerActionDataModel);
            i++;
        }

        if (isSuccess) {
            // Register the Common Service
            // Intent serviceIntent = new Intent(this, CommonService.class);
            // startService(serviceIntent);

            AlarmActionHelper helper = new AlarmActionHelper();
            int h = Integer.parseInt(hour.getText().toString());
            int m = Integer.parseInt(min.getText().toString());
            int s = Integer.parseInt(sec.getText().toString());
            helper.CreateAlarm(getApplicationContext(), randomnumber, h, m, s, -1);

            Toast.makeText(getApplicationContext(), "Timer Action Created", Toast.LENGTH_SHORT).show();

            this.finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Failed to create a Timer Action", Toast.LENGTH_LONG).show();
        }
    }

    public void DeleteTimerAction(View view){
        DatabaseActionHelper dbHelper = new DatabaseActionHelper(getApplicationContext());
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

        value = 0;

        if (requestCode == 6 && resultCode == 5) {
            value = data.getIntExtra("timer", 0);
        }

        if (requestCode == Constants.REQ_CODE_ADMIN_PERM) {
            if (resultCode == RESULT_OK) {
                adminPermissionJustProvided = true;
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Device Administrator Process Cancelled", Toast.LENGTH_SHORT).show();
            }
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

    public void UpdateMediaVolumeSeekbarColor(SeekBar sb, int progress) {
        if (progress > 10) {
            sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.mediavolumered), PorterDuff.Mode.MULTIPLY);
            sb.getThumb().setColorFilter(getResources().getColor(R.color.mediavolumered), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.tileforeground), PorterDuff.Mode.MULTIPLY);
            sb.getThumb().setColorFilter(getResources().getColor(R.color.tileforeground), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void UpdateBrightnessSeekbarColor(SeekBar sb, int progress) {
        if (progress > 10) {
            sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.mediavolumered), PorterDuff.Mode.MULTIPLY);
            sb.getThumb().setColorFilter(getResources().getColor(R.color.mediavolumered), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.tileforeground), PorterDuff.Mode.MULTIPLY);
            sb.getThumb().setColorFilter(getResources().getColor(R.color.tileforeground), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void UpdateBrightnessControl() {
        int currentBrightness = Utils.getBrightness(getApplicationContext());

        if (currentBrightness != -1) {
            brightness.setProgress(currentBrightness);

            boolean hasPermission = PermissionChecker.CheckPermissionForWriteSettings(this.getApplicationContext());
            brightness.setEnabled(hasPermission && brightnessWritePermission.isChecked());
            brightness.setVisibility(VISIBLE);

            if (!hasPermission) {
                brightnessWritePermission.setChecked(false);
            }
        }
        else {
            brightness.setVisibility(View.INVISIBLE);
            brightness.setEnabled(false);
            brightnessWritePermission.setChecked(false);
        }
    }

    public void UpdateScreenTurnOffControl() {
        if (!PermissionChecker.CheckPermissionForWriteSettings(this.getApplicationContext())) {
            turnOffScreen.setChecked(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
