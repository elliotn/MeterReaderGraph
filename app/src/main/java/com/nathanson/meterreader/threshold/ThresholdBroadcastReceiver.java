package com.nathanson.meterreader.threshold;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nathanson.meterreader.MeterReaderApplication;

public class ThresholdBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // honor user's setting for auto-checking meter at boot up.
        if (MeterReaderApplication.getInstance().getSharedPrefs().getAutcheck()) {
            Intent thresholdIntent = new Intent(context, ThresholdIntentService.class);
            context.startService(thresholdIntent);
        }
    }

}

