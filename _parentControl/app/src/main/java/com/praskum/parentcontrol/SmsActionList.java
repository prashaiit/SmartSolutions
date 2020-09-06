package com.praskum.parentcontrol;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SmsActionList extends AppCompatActivity {

    private ToggleButton lockScreen, childMode;
    private ToggleButton switch2Home, wifi, silentMode, brightnessWritePermission, turnOffScreen, mediaVolumeEnable;
    private RelativeLayout lockPanelInChildMode;
    private SeekBar mediaVolume, brightness;
    private TextView turnOffScreenPanel, switch2HomePanel, wifiPanel, silentModePanel, brightnessPanel, mediavolumePanel;
    private boolean writePermissionJustProvided = false;
    private static final int ADMIN_INTENT = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    boolean lockSmsActionJustEnabled = false, kidModeSmsActionJustEnabled = false;

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
        mediaVolumeEnable = (ToggleButton) findViewById(R.id.mediavolumeenable);

        lockScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockScreen.isChecked()) {
                    /*if (!PermissionChecker.HasReadSmsPermission(SmsActionList.this)) {
                        PermissionChecker.PromtForReadSmsPermission(SmsActionList.this, false);
                    }
                    else {
                        PermissionChecker.PromptForDeviceAdminPermission(SmsActionList.this, false);
                    }*/

                    if (!PermissionChecker.HasReadSmsPermission(SmsActionList.this)
                            || PermissionChecker.GetDevPolMgrWithAdmnPerm(SmsActionList.this) == null) {

                        TriggerAlertDialog(true, true);
                    }
                    else if (GetRegisteredContacts() == 0) {
                        TriggerAlertDialog(true, false);
                    }
                }
            }
        });

        childMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (childMode.isChecked()) {
                    if (!PermissionChecker.HasReadSmsPermission(SmsActionList.this)) {

                        TriggerAlertDialog(false, true);
                    }
                    else if (GetRegisteredContacts() == 0) {
                        TriggerAlertDialog(false, false);
                    }
                    else {
                        UpdateChildModeSettings();
                    }
                }
                else {
                    UpdateChildModeSettings();
                }
            }
        });

        mediaVolumeEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaVolume.setEnabled(mediaVolumeEnable.isChecked());
                UpdateMediaVolume();
            }
        });

        brightnessWritePermission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PermissionChecker.PromptForWriteSettingPermission(SmsActionList.this, Constants.REQ_CODE_WRITE_PERM, false);
                }

                UpdateBrightnessControl();
            }
        });

        turnOffScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PermissionChecker.PromptForWriteSettingPermission(SmsActionList.this, Constants.REQ_CODE_WRITE_PERM, false);
                }
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

    private void TriggerAlertDialog(final boolean isLock, final boolean isPerm) {
        LayoutInflater li = LayoutInflater.from(SmsActionList.this);
        View promptsView = li.inflate(R.layout.alert_dialog, null);
        TextView dialogContent = (TextView) promptsView.findViewById(R.id.dialogContent);

        boolean isPermTitle = true;
        if (isLock && isPerm) {
            dialogContent.setText("Read Sms/Receive Sms\nDevice Admin");
        }
        else if (isLock && !isPerm) {
            dialogContent.setText("Atleast one Contact has to be registered to allow Sms Action 'lock'");
            isPermTitle = false;
        }
        else if (!isLock && isPerm) {
            dialogContent.setText("Read Sms/Receive Sms\nModify System Settings");
        }
        else {
            isPermTitle = false;
            dialogContent.setText("Atleast one Contact has to be registered to allow Sms Action 'kidmode'");
        }

        TextView permTitle = (TextView) promptsView.findViewById(R.id.dialogtitlePerm);
        TextView contactsTitle = (TextView) promptsView.findViewById(R.id.dialogtitleContacts);
        if (isPermTitle) {
            contactsTitle.setVisibility(View.GONE);
        }
        else {
            permTitle.setVisibility(View.GONE);
        }


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SmsActionList.this);

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false).
                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isLock && isPerm) {
                            Intent permIntent = new Intent(SmsActionList.this, PermissionActivity.class);
                            permIntent.putExtra("reqCode", Constants.REQ_CODE_LOCK_ACTION);
                            startActivityForResult(permIntent, Constants.REQ_CODE_LOCK_ACTION);
                        }
                        else if (!isLock && isPerm) {
                            Intent permIntent = new Intent(SmsActionList.this, PermissionActivity.class);
                            permIntent.putExtra("reqCode", Constants.REQ_CODE_KidMode_ACTION);
                            startActivityForResult(permIntent, Constants.REQ_CODE_KidMode_ACTION);
                        }
                        else if (isLock && !isPerm) {
                            Intent contactsIntent = new Intent(SmsActionList.this, ContactsActivity.class);
                            contactsIntent.putExtra("reqCode", Constants.REQ_CODE_LOCK_ACTION);
                            startActivityForResult(contactsIntent, Constants.REQ_CODE_LOCK_ACTION);
                        }
                        else {
                            Intent contactsIntent = new Intent(SmsActionList.this, ContactsActivity.class);
                            contactsIntent.putExtra("reqCode", Constants.REQ_CODE_KidMode_ACTION);
                            startActivityForResult(contactsIntent, Constants.REQ_CODE_KidMode_ACTION);
                        }
                    }
                }).
                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (isLock) {
                            lockScreen.setChecked(false);
                        }
                        else {
                            childMode.setChecked(false);
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(getResources().getColor(R.color.tilebackground));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(getResources().getColor(R.color.tilebackground));
    }

    public void UpdateBrightnessControl() {
        boolean hasPermission = PermissionChecker.CheckPermissionForWriteSettings(this.getApplicationContext());

        if (writePermissionJustProvided) {
            brightnessWritePermission.setChecked(hasPermission);
        }

        boolean value = hasPermission && brightnessWritePermission.isChecked();
        brightness.setEnabled(value);

        if (value) {
            int currentBrightness = Utils.getBrightness(getApplicationContext());
            brightness.setProgress(currentBrightness);
        }
    }

    public void UpdateScreenTurnOffControl() {
        if (!PermissionChecker.CheckPermissionForWriteSettings(this.getApplicationContext())) {
            turnOffScreen.setChecked(false);
        }
    }

    public void UpdateMediaVolume() {
            if (!mediaVolumeEnable.isChecked()) {
                return;
            }
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.i("seekbar", "max vol " + am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            Log.i("seekbar", "curr vol " + currentVolume);
            mediaVolume.setProgress(currentVolume);
            UpdateMediaVolumeSeekbarColor(mediaVolume, currentVolume);
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

    @Override
    protected void onResume() {
        super.onResume();

        if (writePermissionJustProvided) {
            UpdateBrightnessControl();
            UpdateScreenTurnOffControl();
            writePermissionJustProvided = false;
        }
        else if (lockSmsActionJustEnabled) {
            lockScreen.setChecked(PermissionChecker.HasReadSmsPermission(SmsActionList.this)
                    && PermissionChecker.GetDevPolMgrWithAdmnPerm(SmsActionList.this) != null
                    && GetRegisteredContacts() > 0);
            lockSmsActionJustEnabled = false;
        }
        else if (kidModeSmsActionJustEnabled) {
            childMode.setChecked(PermissionChecker.HasReadSmsPermission(SmsActionList.this) && GetRegisteredContacts() > 0);
            kidModeSmsActionJustEnabled = false;
            UpdateChildModeSettings();
        }
        else {
            UpdateSettingsFromDatabase();
            UpdateChildModeSettings();
        }
    }

    private void UpdateSettingsFromDatabase() {
        // Read Settings from Table
        DatabaseActionHelper dbHelper = new DatabaseActionHelper(getApplicationContext());
        Cursor c = dbHelper.ReadData(Constants.LOCKSCREEN_SMS_ACTION_ALARMID);
        boolean hasReadSmsPermission = PermissionChecker.HasReadSmsPermission(SmsActionList.this);
        if ((c != null && c.getCount() > 0)) {
            boolean hadDeviceAdminPermission = (PermissionChecker.GetDevPolMgrWithAdmnPerm(getApplicationContext()) != null);
            lockScreen.setChecked(hadDeviceAdminPermission && hasReadSmsPermission && GetRegisteredContacts() > 0);
        }
        else {
            lockScreen.setChecked(false);
        }

        c = dbHelper.ReadData(Constants.CHILDMODE_SMS_ACTION_ALARMID);
        if (c != null && c.getCount() > 0) {
            childMode.setChecked(hasReadSmsPermission && GetRegisteredContacts() > 0);
        }
        else {
            childMode.setChecked(false);
        }

        c = dbHelper.ReadData(Constants.CHILDMODE_ACTIONS);
        if (c != null && c.getCount() > 0) {
            switch2Home.setChecked(c.getInt(c.getColumnIndex("SwitchToHomeScreen")) == 1);
            wifi.setChecked(c.getInt(c.getColumnIndex("Wifi")) == 1);
            silentMode.setChecked(c.getInt(c.getColumnIndex("Silent")) == 1);

            boolean hasWritePerm = PermissionChecker.CheckPermissionForWriteSettings(SmsActionList.this);
            turnOffScreen.setChecked(c.getInt(c.getColumnIndex("ScreenOff")) == 1 && hasWritePerm);
            int currentMediaVolume = c.getInt(c.getColumnIndex("MediaVolume"));
            mediaVolume.setProgress(currentMediaVolume);
            mediaVolumeEnable.setChecked(currentMediaVolume != -1);

            int brgness = c.getInt(c.getColumnIndex("Brightness"));
            brightnessWritePermission.setChecked(brgness != -1 && hasWritePerm);
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
        mediaVolumeEnable.setEnabled(enabled);
        mediavolumePanel.setEnabled(enabled);
        mediaVolume.setEnabled(enabled && mediaVolumeEnable.isChecked());
        brightnessPanel.setEnabled(enabled);
        brightnessWritePermission.setEnabled(enabled);
        brightness.setEnabled(enabled && brightnessWritePermission.isChecked());
    }

    @Override
    protected void onPause() {
        super.onPause();
        UpdateSmsActionsInTable();
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

            if (mediaVolumeEnable.isChecked() && mediaVolume.isEnabled()) {
                dataModel.MediaVolume = mediaVolume.getProgress();
            }
            else {
                dataModel.MediaVolume = -1;
            }
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
        if (requestCode == Constants.REQ_CODE_LOCK_ACTION) {
            lockSmsActionJustEnabled = true;
        }
        else if (requestCode == Constants.REQ_CODE_KidMode_ACTION || requestCode == Constants.REQ_CODE_KidMode_ACTION) {
            kidModeSmsActionJustEnabled = true;
        }
        else if (requestCode == Constants.REQ_CODE_WRITE_PERM) {
            writePermissionJustProvided = true;
        }
    }

    public void TriggerContactsActivity(View view) {
        Intent intent = new Intent(SmsActionList.this, ContactsActivity.class);
        startActivity(intent);
    }

    private int GetRegisteredContacts() {
        DatabaseActionHelper db = new DatabaseActionHelper(getApplicationContext());
        Cursor c = db.ReadAllContacts();
        return  c != null ? c.getCount() : 0;
    }
}