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

package com.nathanson.meterreader.threshold;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.nathanson.meterreader.R;

import java.util.Calendar;

public class ThresholdAlarm {

    private PendingIntent getPendingIntent(Context context) {
        Intent thresholdIntent = new Intent(context, ThresholdIntentService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, thresholdIntent, PendingIntent.FLAG_IMMUTABLE);

        return  pendingIntent;
    }


    /**
     * Returns when the alarm should go off tomorrow.
     * @param hour
     * @param min
     * @return offset for running tomorrow.
     */
    private long runTime(int hour, int min) {
        Calendar cal = Calendar.getInstance();

        // tomorrow.
        cal.add(Calendar.DAY_OF_YEAR, 1);

        // hour:min:00.
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);

        long runTime = cal.getTimeInMillis();

        return runTime;
    }


    public void schedule(Context context, int hour, int min) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long runTime= runTime(hour, min);
        if (runTime <= 0) {
            return;
        }

        // setup repeating alarm for tomorrow.
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                runTime,
                AlarmManager.INTERVAL_DAY,
                getPendingIntent(context));

        Log.i(context.getResources().getString(R.string.app_name),
                "Threshold alarm scheduled for tomorrow at " + hour + ":" + min);
    }

    public void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context));

        Log.i(context.getResources().getString(R.string.app_name), "Threshold alarm canceled. ");
    }


}
