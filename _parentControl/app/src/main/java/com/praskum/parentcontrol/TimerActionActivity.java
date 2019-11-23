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

    private TextView timerSetting;
    private int value = 5;
    private int alarmId;
    private Button createAction, deleteAtion;
    private ToggleButton lock, switch2Home, wifi, silentMode, brightnessWritePermission, turnOffScreen, mediavolumeenable;
    private Button toggleButtonSecs, toggleButtonMins;
    private SeekBar mediaVolume, brightness;
    private static final int ADMIN_INTENT = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private boolean adminPermissionJustProvided = false;
    private boolean isTimeUnitsInMins = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeraction);

        timerSetting = (TextView) findViewById(R.id.timerSetting);
        createAction = (Button) findViewById(R.id.createTimerAction);
        deleteAtion = (Button) findViewById(R.id.deleteTimerAction);
        lock = (ToggleButton) findViewById(R.id.lock);
        switch2Home = (ToggleButton) findViewById(R.id.switchtohome);
        wifi = (ToggleButton) findViewById(R.id.wifi);
        silentMode = (ToggleButton) findViewById(R.id.silent);
        mediavolumeenable = (ToggleButton) findViewById(R.id.mediavolumeenable);
        mediaVolume = (SeekBar) findViewById(R.id.mediavolume);
        brightness = (SeekBar) findViewById(R.id.brightness);
        brightnessWritePermission = (ToggleButton) findViewById(R.id.brightnesswritepermissions);
        turnOffScreen = (ToggleButton) findViewById(R.id.turnoffscreen);
        toggleButtonMins = (Button) findViewById(R.id.togglebuttonmins);
        toggleButtonSecs = (Button) findViewById(R.id.togglebuttonseconds);

        toggleButtonMins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateTimeUnits(true);
            }
        });

        toggleButtonSecs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateTimeUnits(false);
            }
        });

        lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PermissionChecker.PromptForDeviceAdminPermission(TimerActionActivity.this, false);
                }
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

        mediavolumeenable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UpdateMediaVolumeControl(isChecked);
            }
        });

        brightnessWritePermission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PermissionChecker.PromptForWriteSettingPermission(TimerActionActivity.this, Constants.REQ_CODE_WRITE_PERM, false);
                }

                UpdateBrightnessControl();
            }
        });

        turnOffScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PermissionChecker.PromptForWriteSettingPermission(TimerActionActivity.this, Constants.REQ_CODE_WRITE_PERM, false);
                }
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

    private void UpdateTimeUnits(boolean isMins) {
        if (isMins) {
            toggleButtonSecs.setBackgroundResource(R.drawable.togglebuttonshapeunselected);
            toggleButtonSecs.setTextColor(getResources().getColor(R.color.colorforeground));
            toggleButtonMins.setBackgroundResource(R.drawable.togglebuttonshape);
            toggleButtonMins.setTextColor(getResources().getColor(R.color.tileforeground));
            isTimeUnitsInMins = true;
        }
        else {
            toggleButtonMins.setBackgroundResource(R.drawable.togglebuttonshapeunselected);
            toggleButtonMins.setTextColor(getResources().getColor(R.color.colorforeground));
            toggleButtonSecs.setBackgroundResource(R.drawable.togglebuttonshape);
            toggleButtonSecs.setTextColor(getResources().getColor(R.color.tileforeground));
            isTimeUnitsInMins = false;
        }

        UpdateTextView();
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

                int currentMediaVolume = c.getInt(c.getColumnIndex("MediaVolume"));
                if (currentMediaVolume != -1) {
                    mediaVolume.setProgress(currentMediaVolume);
                }

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

        UpdateTimeUnits(isTimeUnitsInMins);
        UpdateMediaVolume();
        UpdateBrightnessControl();
        UpdateScreenTurnOffControl();
    }

    public void UpdateMediaVolume() {
        if (!mediavolumeenable.isChecked()) {
            mediaVolume.setEnabled(false);
            return;
        }
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i("seekbar", "max vol " + am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        Log.i("seekbar", "curr vol " + currentVolume);
        mediaVolume.setProgress(currentVolume);
        UpdateMediaVolumeSeekbarColor(mediaVolume, currentVolume);
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

        if (mediavolumeenable.isChecked() && mediaVolume.isEnabled()) {
            timerActionDataModel.MediaVolume = mediaVolume.getProgress();
        }
        else {
            timerActionDataModel.MediaVolume = -1;
        }

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
            if (isTimeUnitsInMins) {
                int h = value / 60;
                int m = value % 60;
                int s = 0;
                helper.CreateAlarm(getApplicationContext(), randomnumber, h, m, s, -1);
            }
            else {
                int s = value % 60;
                int m = value / 60;
                int h = 0;

                if (m >= 60) {
                    h = m / 60;
                    m = m % 60;
                }

                helper.CreateAlarm(getApplicationContext(), randomnumber, h, m, s, -1);
            }

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

        String units = isTimeUnitsInMins ? "mins" : "secs";
        timerSetting.setText(value + "\n " + units);
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

    public void UpdateMediaVolumeControl(boolean enabled) {
           mediaVolume.setEnabled(enabled);
           UpdateMediaVolume();
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

    public void TimerPlusEvent(View view) {
        value = value + 5;
        UpdateTextView();
    }

    public void TimerMinusEvent(View view) {
        if (value > 5) {
            value = value - 5;
        }
        UpdateTextView();
    }
}
