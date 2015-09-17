package com.nathanson.meterreader.threshold;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;

public class ThresholdAlarm {

    private PendingIntent getPendingIntent(Context context) {
        Intent thresholdIntent = new Intent(context, ThresholdIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, thresholdIntent, 0);

        return  pendingIntent;
    }


    /**
     * Returns the offset between now and when the alarm should go off tomorrow.
     * @param hour
     * @param min
     * @return offset for running tomorrow.
     */
    private long runTimeDelay(int hour, int min) {
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();

        // tomorrow.
        cal.add(Calendar.DAY_OF_YEAR, 1);

        // hour:min:00.
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);

        long runTime = cal.getTimeInMillis();

        return runTime - now;
    }


    public void schedule(Context context, int hour, int min) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long runTimeDelay= runTimeDelay(hour, min);
        if (runTimeDelay <= 0) {
            return;
        }

        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + runTimeDelay,
                getPendingIntent(context));
    }

    public void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context));
    }


}