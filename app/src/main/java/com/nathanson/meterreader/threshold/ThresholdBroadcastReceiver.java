package com.nathanson.meterreader.threshold;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ThresholdBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent thresholdIntent= new Intent(context, ThresholdIntentService.class);
        context.startService(thresholdIntent);
    }

}

