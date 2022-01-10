package com.zkteco.silkiddemo.Utils;

import android.util.Log;

public class DebugLog {
    private static boolean isDebug = false;
    private static final String TAG = "Attendence";

    public static void i(String value){
        if (isDebug) Log.i(TAG, value+"");
    }

    public static void d(String value){
        if (isDebug) Log.d(TAG, value+"");
    }

    public static void e(String value){
        if (isDebug) Log.e(TAG, value+"");
    }

    public static void v(String value){
        if (isDebug) Log.v(TAG, value+"");
    }

}
