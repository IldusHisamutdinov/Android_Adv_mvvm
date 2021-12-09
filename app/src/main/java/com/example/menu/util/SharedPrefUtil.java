package com.example.menu.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

import static com.example.menu.Constants.GENERAL_PREFERENCES;

public class SharedPrefUtil {

    public static void save(Context context, String key, Object value) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        SharedPreferences pref = contextWeakReference.get().getSharedPreferences(GENERAL_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, value.toString());
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();


    }

    public static Object getData(Context context, String key, Object defaultValue) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        SharedPreferences pref = contextWeakReference.get().getSharedPreferences(GENERAL_PREFERENCES, Context.MODE_PRIVATE);

        if (defaultValue instanceof String) {
            return pref.getString(key, defaultValue.toString());
        } else if (defaultValue instanceof Integer) {
            return pref.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return pref.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Long) {
            return pref.getLong(key, (Long) defaultValue);
        }


        return defaultValue;

    }
}
