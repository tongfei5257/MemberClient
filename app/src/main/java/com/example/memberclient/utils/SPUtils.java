package com.example.memberclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("xm_file", Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }
    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("xm_file", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).apply();
    }
}
