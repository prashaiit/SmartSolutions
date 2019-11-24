package com.praskum.parentcontrol;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class PermissionActivity extends AppCompatActivity {
    private ToggleButton sms, writeSettings, admin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acitivity_permission);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tilebackground)));
        actionBar.setTitle(R.string.permissions_title);
        actionBar.show();

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.tilebackground));
        }

        sms = (ToggleButton) findViewById(R.id.readSmsPerm);
        writeSettings = (ToggleButton) findViewById(R.id.modifySystemSettingsPerm);
        admin = (ToggleButton) findViewById(R.id.activateDeviceAdminPerm);

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriggerSmsPermissionActivity();
            }
        });

        writeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriggerModifySystemSettingsPermission();
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivateDeviceAdminPerm();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        writeSettings.setChecked(PermissionChecker.CheckPermissionForWriteSettings(getApplicationContext()));
        sms.setChecked(PermissionChecker.HasReadSmsPermission(PermissionActivity.this));
        admin.setChecked(PermissionChecker.GetDevPolMgrWithAdmnPerm(getApplicationContext()) != null);
    }

    public void TriggerSmsPermissionActivity() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // or
        // PermissionChecker.PromtForReadSmsPermission(PermissionActivity.this, true);
    }

    public void TriggerModifySystemSettingsPermission() {
        PermissionChecker.PromptForWriteSettingPermission(PermissionActivity.this, 501, true);
    }

    public void ActivateDeviceAdminPerm() {
        PermissionChecker.PromptForDeviceAdminPermission(PermissionActivity.this, true);
    }

    public void TriggerSmsPolicyActivity(View view) {
        Intent intent = new Intent(PermissionActivity.this, SmsPolicyActivity.class);
        startActivity(intent);
    }
}
