package com.nathanson.meterreader;

import android.app.Application;

import com.nathanson.meterreader.persistence.MeterReaderSharedPreferences;

public class MeterReaderApplication extends Application {

    private MeterReaderSharedPreferences mSharedPrefs;

    private static MeterReaderApplication sInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPrefs = MeterReaderSharedPreferences.getInstance(this);
        sInstance = this;
    }

    public static MeterReaderApplication getInstance() {
        return sInstance;
    }

    public MeterReaderSharedPreferences getSharedPrefs() {
        return mSharedPrefs;
    }

}
