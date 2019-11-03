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

public class PermissionActivity extends AppCompatActivity {
    private Button sms, writeSettings, admin;

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

        sms = (Button) findViewById(R.id.readSmsPerm);
        writeSettings = (Button) findViewById(R.id.modifySystemSettingsPerm);
        admin = (Button) findViewById(R.id.activateDeviceAdminPerm);
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

        if (PermissionChecker.CheckPermissionForWriteSettings(getApplicationContext())) {
            writeSettings.setText("Allowed");
        }
        else {
            writeSettings.setText("Allow");
        }
    }

    public void TriggerSmsPermissionActivity(View view) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // or
        // PermissionChecker.PromtForReadSmsPermission(PermissionActivity.this, true);
    }

    public void TriggerModifySystemSettingsPermission(View view) {
        PermissionChecker.PromptForWriteSettingPermission(PermissionActivity.this, 501, true);
    }

    public void ActivateDeviceAdminPerm(View view) {
        PermissionChecker.PromptForDeviceAdminPermission(PermissionActivity.this);
    }
}
