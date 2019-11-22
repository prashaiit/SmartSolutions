package com.praskum.parentcontrol;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SmsActionList extends AppCompatActivity {

    private ToggleButton lockScreen, childMode;
    private ToggleButton switch2Home, wifi, silentMode, brightnessWritePermission, turnOffScreen;
    private RelativeLayout lockPanelInChildMode;
    private SeekBar mediaVolume, brightness;
    private TextView turnOffScreenPanel, switch2HomePanel, wifiPanel, silentModePanel, brightnessPanel, mediavolumePanel;
    private boolean adminPermissionJustProvided = false;
    private static final int ADMIN_INTENT = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activiy_smsactionlist);

        mDevicePolicyManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, Admin.class);
        lockScreen = (ToggleButton) findViewById(R.id.lockScreen);
        if(!( mDevicePolicyManager != null && mDevicePolicyManager.isAdminActive(mComponentName))) {
            //lockScreen.setChecked(false);
        }

        childMode = (ToggleButton) findViewById(R.id.childMode);
        lockPanelInChildMode = (RelativeLayout) findViewById(R.id.lockPanel);
        lockPanelInChildMode.setVisibility(View.GONE);
        switch2Home = (ToggleButton) findViewById(R.id.switchtohome);
        wifi = (ToggleButton) findViewById(R.id.wifi);
        silentMode = (ToggleButton) findViewById(R.id.silent);
        brightnessWritePermission = (ToggleButton) findViewById(R.id.brightnesswritepermissions);
        turnOffScreen = (ToggleButton) findViewById(R.id.turnoffscreen);
        turnOffScreenPanel = (TextView) findViewById(R.id.turnoffscreenPanel);
        switch2HomePanel = (TextView) findViewById(R.id.switchtohomePanel);
        wifiPanel = (TextView) findViewById(R.id.wifiPanel);
        silentModePanel = (TextView) findViewById(R.id.silentmodePanel);
        brightnessPanel = (TextView) findViewById(R.id.brightnessPanel);
        mediavolumePanel = (TextView) findViewById(R.id.mediavolumePanel);
        mediaVolume = (SeekBar) findViewById(R.id.mediavolume);
        brightness = (SeekBar) findViewById(R.id.brightness);

        lockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /*if (!PermissionChecker.HasReadSmsPermission(SmsActionList.this)) {
                        PermissionChecker.PromtForReadSmsPermission(SmsActionList.this, false);
                    }
                    else {
                        PermissionChecker.PromptForDeviceAdminPermission(SmsActionList.this, false);
                    }*/

                    if (!PermissionChecker.HasReadSmsPermission(SmsActionList.this)
                            || PermissionChecker.GetDevPolMgrWithAdmnPerm(SmsActionList.this) == null) {

                        LayoutInflater li = LayoutInflater.from(SmsActionList.this);
                        View promptsView = li.inflate(R.layout.dialog_permission, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                SmsActionList.this);

                        alertDialogBuilder.setView(promptsView);
                        alertDialogBuilder.setCancelable(false).
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent permIntent = new Intent(SmsActionList.this, PermissionActivity.class);
                                        permIntent.putExtra("reqCode", Constants.REQ_CODE_LOCK_SMS_ACTION_PERM);
                                        startActivityForResult(permIntent, Constants.REQ_CODE_LOCK_SMS_ACTION_PERM);
                                    }
                                }).
                                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        lockScreen.setChecked(false);
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setTitle("Permissions Required");
                        alertDialog.show();
                    }
                }

                UpdateToggleButton(lockScreen);
            }
        });

        childMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UpdateToggleButton(childMode);
                UpdateChildModeSettings();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tilebackground)));
        actionBar.setTitle(R.string.sms_actions);
        actionBar.show();

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.tilebackground));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        UpdateSettingsFromDatabase();
        UpdateToggleButton(lockScreen);
        UpdateToggleButton(childMode);
        UpdateChildModeSettings();
    }

    private void UpdateSettingsFromDatabase() {
        // Read Settings from Table
        DatabaseActionHelper dbHelper = new DatabaseActionHelper(getApplicationContext());
        Cursor c = dbHelper.ReadData(Constants.LOCKSCREEN_SMS_ACTION_ALARMID);
        if ((c != null && c.getCount() > 0) || adminPermissionJustProvided) {
            boolean hadDeviceAdminPermission = (PermissionChecker.GetDevPolMgrWithAdmnPerm(getApplicationContext()) != null);
            boolean hasReadSmsPermission = PermissionChecker.HasReadSmsPermission(SmsActionList.this);
            lockScreen.setChecked(hadDeviceAdminPermission && hasReadSmsPermission);
            adminPermissionJustProvided = false;
        }
        else {
            lockScreen.setChecked(false);
        }

        c = dbHelper.ReadData(Constants.CHILDMODE_SMS_ACTION_ALARMID);
        if (c != null && c.getCount() > 0) {
            childMode.setChecked(true);
        }
        else {
            childMode.setChecked(false);
        }

        c = dbHelper.ReadData(Constants.CHILDMODE_ACTIONS);
        if (c != null && c.getCount() > 0) {
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

    private void UpdateChildModeSettings() {
        boolean enabled = childMode.isChecked();

        turnOffScreenPanel.setEnabled(enabled);
        turnOffScreen.setEnabled(enabled);
        switch2HomePanel.setEnabled(enabled);
        switch2Home.setEnabled(enabled);
        wifiPanel.setEnabled(enabled);
        wifi.setEnabled(enabled);
        silentModePanel.setEnabled(enabled);
        silentMode.setEnabled(enabled);
        mediavolumePanel.setEnabled(enabled);
        mediaVolume.setEnabled(enabled);
        brightnessPanel.setEnabled(enabled);
        brightnessWritePermission.setEnabled(enabled);
        brightness.setEnabled(enabled && brightnessWritePermission.isChecked());
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
    public void onBackPressed() {
        super.onBackPressed();

        UpdateSmsActionsInTable();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void UpdateSmsActionsInTable() {

        DatabaseActionHelper dbHelper = new DatabaseActionHelper(getApplicationContext());

        // Lock Screen SMS Action
        if (lockScreen.isChecked()) {
            DatabaseDataModel dataModel = new DatabaseDataModel();
            dataModel.AlarmId = Constants.LOCKSCREEN_SMS_ACTION_ALARMID;
            dbHelper.InsertData(dataModel);
        }
        else {
            dbHelper.DeleteData(Constants.LOCKSCREEN_SMS_ACTION_ALARMID);
        }

        if (childMode.isChecked()) {
            DatabaseDataModel dataModel = new DatabaseDataModel();
            dataModel.AlarmId = Constants.CHILDMODE_SMS_ACTION_ALARMID;
            dbHelper.InsertData(dataModel);

            // Update actions
            dbHelper.DeleteData(Constants.CHILDMODE_ACTIONS);
            dataModel = new DatabaseDataModel();
            dataModel.AlarmId = Constants.CHILDMODE_ACTIONS;
            dataModel.Brightness = brightnessWritePermission.isChecked() ? brightness.getProgress() : -1;
            dataModel.MediaVolume = mediaVolume.getProgress();
            dataModel.ScreenOff = turnOffScreen.isChecked();
            dataModel.SilentMode = silentMode.isChecked();
            dataModel.SwitchToHome = switch2Home.isChecked();
            dataModel.WifiMode = wifi.isChecked();
            dbHelper.InsertData(dataModel);
        }
        else {
            dbHelper.DeleteData(Constants.CHILDMODE_SMS_ACTION_ALARMID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_CODE_ADMIN_PERM) {
            if (resultCode == RESULT_OK) {
                adminPermissionJustProvided = true;
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Device Administrator Process Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == Constants.REQ_CODE_LOCK_SMS_ACTION_PERM) {
            if (resultCode == Constants.RES_CODE_LOCK_SMS_ACTION_PERM_SUCCESS) {
                lockScreen.setChecked(true);
            }
            else {
                lockScreen.setChecked(false);
            }
        }
    }
}
