package com.nathanson.meterreader.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class MeterReaderSharedPreferences {
    private static final String TAG = "MRSharedPreferences";

    private static final String SETTINGS_URL = "SETTINGS_URL";
    private static final String SETTINGS_ALERT_THRESHOLD = "SETTINGS_ALERT_THRESHOLD";



    private volatile static MeterReaderSharedPreferences INSTANCE;
    private SharedPreferences mSharedPrefs;

    private MeterReaderSharedPreferences() {}

    public static MeterReaderSharedPreferences getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MeterReaderSharedPreferences.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MeterReaderSharedPreferences();
                    INSTANCE.mSharedPrefs =  PreferenceManager.getDefaultSharedPreferences(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Determines if shared preferences are populated.
     * @return true if shared prefs are populated, false otherwise.
     */
    public boolean isPopulated() {
        return !TextUtils.isEmpty(getUrl());
    }


    public void setUrl(String url) {
        INSTANCE.mSharedPrefs
                .edit()
                .putString(SETTINGS_URL, url)
                .apply();

    }

    public String getUrl() {
        return INSTANCE.mSharedPrefs
                .getString(SETTINGS_URL, "");
    }


    // TODO: for future usage.
    public void setUsageAlertThreshold(int threshold) {
        INSTANCE.mSharedPrefs
                .edit()
                .putInt(SETTINGS_ALERT_THRESHOLD, threshold)
                .apply();
    }

    // TODO: for future usage.
    public int getUsageAlertThreshold() {
        return INSTANCE.mSharedPrefs
                .getInt(SETTINGS_ALERT_THRESHOLD, -1);
    }



}

