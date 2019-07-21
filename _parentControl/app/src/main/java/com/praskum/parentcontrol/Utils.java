package com.praskum.parentcontrol;

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
}
