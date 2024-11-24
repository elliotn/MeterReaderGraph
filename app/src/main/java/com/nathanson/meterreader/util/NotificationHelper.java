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

package com.nathanson.meterreader.util;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.core.app.NotificationCompat;

import com.nathanson.meterreader.MeterReaderApplication;
import com.nathanson.meterreader.R;
import com.nathanson.meterreader.activity.MainActivity;
import com.nathanson.meterreader.data.Meter;
import com.nathanson.meterreader.fetch.DataFetcher;

public class NotificationHelper {

    public static final String CHANNEL_ID = "2222";

    private NotificationHelper() {}

    private static void showNotification(Context context, String title, String message) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT | FLAG_MUTABLE
                );

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ic_warning_white_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int mNotificationId = 1111;

        mNotificationManager.cancel(mNotificationId);
        // Builds the notification and issues it.
        mNotificationManager.notify(mNotificationId, notification.build());

    }

    public static void showThresholdExceededNotification(Context context, Meter meter) {
        int usage = DataFetcher.getCurrentReading(meter);

        Resources res = context.getResources();
        String title = res.getString(R.string.threshold_notification_title);
        String units = MeterReaderApplication.getInstance().getSharedPrefs().getUnits();
        String message = String.format(res.getString(R.string.threshold_notification_message), usage, units);

        showNotification(context, title, message);
    }

    public static void showThresholdFailureNotification(Context context) {
        Resources res = context.getResources();
        String title = res.getString(R.string.threshold_failure_title);
        String message = res.getString(R.string.threshold_failure_message);

        showNotification(context, title, message);
    }

}
