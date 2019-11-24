package com.praskum.parentcontrol;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionChecker {

    public static boolean CheckPermissionForWriteSettings(Context context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }

        return permission;
    }

    public static void PromptForWriteSettingPermission(Activity context, int reqCode, boolean force) {

        if (!force && CheckPermissionForWriteSettings(context.getApplicationContext())) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivityForResult(intent, reqCode);
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, reqCode);
        }
    }

    public static DevicePolicyManager GetDevPolMgrWithAdmnPerm(Context context) {
        final DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager)context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        ComponentName mComponentName = new ComponentName(context, Admin.class);
        if (mDevicePolicyManager != null && mDevicePolicyManager.isAdminActive(mComponentName)) {
            return mDevicePolicyManager;
        }

        return null;
    }

    public static void PromptForDeviceAdminPermission(Activity context, boolean toggle) {
        final DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager)context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        ComponentName mComponentName = new ComponentName(context, Admin.class);

        if ((mDevicePolicyManager != null && !mDevicePolicyManager.isAdminActive(mComponentName))) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Administrator description");
            context.startActivityForResult(intent, Constants.REQ_CODE_ADMIN_PERM);
        }
        else if (toggle) {
           mDevicePolicyManager.removeActiveAdmin(mComponentName);
        }
    }

    public static boolean HasReadSmsPermission(Activity context) {
        return ContextCompat.checkSelfPermission(context,Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean HasReadSmsPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }
}
