package com.prash.iciciassistant;

import android.content.Context;

/**
 * Created by praskum on 4/9/2017.
 */

public final class SmsData {
    private static String filepath = "";

    private SmsData() {

    }

    public static void setFilePath(String f) {
        filepath = f;
    }

    public static String getFilepath() {
        return filepath;
    }
}
