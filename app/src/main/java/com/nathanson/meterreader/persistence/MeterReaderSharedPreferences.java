/* 
 * Copyright (C) 2015 Elliot Nathanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nathanson.meterreader.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.nathanson.meterreader.MeterReaderApplication;
import com.nathanson.meterreader.R;

public class MeterReaderSharedPreferences {
    private static final String TAG = "MRSharedPreferences";

    private static final String SETTINGS_URL = "SETTINGS_URL";
    private static final String SETTINGS_ALERT_THRESHOLD = "SETTINGS_ALERT_THRESHOLD";
    private static final String SETTINGS_AUTOCHECK = "SETTINGS_AUTOCHECK";
    private static final String SETTINGS_AUTOCHECK_HOUR = "SETTINGS_AUTOCHECK_HOUR";
    private static final String SETTINGS_AUTOCHECK_MIN = "SETTINGS_AUTOCHECK_MIN";
    private static final String SETTINGS_UNITS = "SETTINGS_UNITS";


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


    public void setUsageAlertThreshold(int threshold) {
        INSTANCE.mSharedPrefs
                .edit()
                .putInt(SETTINGS_ALERT_THRESHOLD, threshold)
                .apply();
    }

    public int getUsageAlertThreshold() {
        return INSTANCE.mSharedPrefs
                .getInt(SETTINGS_ALERT_THRESHOLD, -1);
    }

    public void setAutocheck(boolean autocheck) {
        INSTANCE.mSharedPrefs
                .edit()
                .putBoolean(SETTINGS_AUTOCHECK, autocheck)
                .apply();
    }

    public boolean getAutocheck() {
        return INSTANCE.mSharedPrefs
                .getBoolean(SETTINGS_AUTOCHECK, false);
    }

    public void setAutocheckHour(int hour) {
        INSTANCE.mSharedPrefs
                .edit()
                .putInt(SETTINGS_AUTOCHECK_HOUR, hour)
                .apply();
    }

    public int getAutocheckHour() {
        return INSTANCE.mSharedPrefs
                .getInt(SETTINGS_AUTOCHECK_HOUR, -1);
    }


    public void setAutocheckMin(int min) {
        INSTANCE.mSharedPrefs
                .edit()
                .putInt(SETTINGS_AUTOCHECK_MIN, min)
                .apply();
    }

    public int getAutocheckMin() {
        return INSTANCE.mSharedPrefs
                .getInt(SETTINGS_AUTOCHECK_MIN, -1);
    }

    public void setUnits(String units) {
        INSTANCE.mSharedPrefs
                .edit()
                .putString(SETTINGS_UNITS, units)
                .apply();
    }

    public String getUnits() {
        return INSTANCE.mSharedPrefs
                .getString(SETTINGS_UNITS, MeterReaderApplication.getInstance().getResources()
                        .getString(R.string.default_units));
    }
}

