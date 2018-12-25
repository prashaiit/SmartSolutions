package com.smartsol.softlock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private static final int ADMIN_INTENT = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private Button enableButton;
    private Button doneButton;
    private LinearLayout permReqLayout, postPermGrantLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, Admin.class);
        if( mDevicePolicyManager != null && mDevicePolicyManager.isAdminActive(mComponentName)) {
            //this.setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lock);
            mDevicePolicyManager.lockNow();
            this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.enableButton = (Button)findViewById(R.id.enableButton);
        this.doneButton = (Button) findViewById(R.id.doneButton);
        this.permReqLayout = (LinearLayout)findViewById(R.id.permRequestLayout);
        this.postPermGrantLayout = (LinearLayout)findViewById(R.id.postPermGrantedLayout);
        this.postPermGrantLayout.setVisibility(View.GONE);

        this.enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
               // intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Administrator description");
                startActivityForResult(intent, ADMIN_INTENT);
            }
        });

        this.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mDevicePolicyManager != null && mDevicePolicyManager.isAdminActive(mComponentName)) {
            this.postPermGrantLayout.setVisibility(View.VISIBLE);
            this.permReqLayout.setVisibility(View.GONE);
        } else {
            this.postPermGrantLayout.setVisibility(View.GONE);
            this.permReqLayout.setVisibility(View.VISIBLE);
        }
    }

    public void exitApp() {
        this.finish();
    }
}
