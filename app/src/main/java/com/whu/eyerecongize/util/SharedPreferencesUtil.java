

package com.whu.eyerecongize.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static final String TAG = "SharedPreferencesUtil";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static SharedPreferencesUtil mSharedPreferencesUtil;


    public SharedPreferencesUtil(Context context) {
        mPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static SharedPreferencesUtil getInstance(Context context) {
        if (mSharedPreferencesUtil == null) {
            synchronized (SharedPreferencesUtil.class) {
                if(mSharedPreferencesUtil == null) {
                    mSharedPreferencesUtil = new SharedPreferencesUtil(context);
                }
            }
        }
        return mSharedPreferencesUtil;
    }

    public void putIntValue(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public int getIntValue(String key) {
        return mPreferences.getInt(key, -1);
    }
}
