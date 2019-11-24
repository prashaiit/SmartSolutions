package com.praskum.parentcontrol;

import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.util.Log;

public class Utils {
    public String getHourValue(int value){
        int v = (value % 1000000)/10000;
        String vv = Integer.toString(v);
        if (v < 10) {
            vv = "0" + vv;
        }
        return vv;
    }

    public String getMinValue(int value){
        int v = (value % 10000)/100;
        String vv = Integer.toString(v);
        if (v < 10) {
            vv = "0" + vv;
        }
        return vv;
    }

    public String getSecValue(int value){
        int v = value % 100;
        String vv = Integer.toString(v);
        if (v < 10) {
            vv = "0" + vv;
        }
        return vv;
    }

    public static int getBrightness(Context context) {
        try {
            int currentBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);

            Log.i("seekbar", "cur brightness " + currentBrightness);
            return currentBrightness;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getNormalizedPhoneNumber(String phoneNo) {
        String nphoneNo = phoneNo.replaceAll("\\s+","");
        nphoneNo = nphoneNo.replace("+91", "");

        if (nphoneNo.startsWith("0") && nphoneNo.length() == 11) {
            nphoneNo = nphoneNo.substring(1);
        }

        return nphoneNo;
    }
}
