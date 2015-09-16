package com.nathanson.meterreader.threshold;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nathanson.meterreader.MeterReaderApplication;
import com.nathanson.meterreader.persistence.MeterReaderSharedPreferences;

public class ThresholdBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MeterReaderSharedPreferences sharedPrefs =
                MeterReaderApplication.getInstance().getSharedPrefs();

        // honor user's setting for auto-checking meter at boot up.
        if (sharedPrefs.getAutocheck()) {
            Intent thresholdIntent = new Intent(context, ThresholdIntentService.class);
            context.startService(thresholdIntent);
        }
    }

}

