package com.nathanson.meterreader.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.nathanson.meterreader.MainActivity;
import com.nathanson.meterreader.R;
import com.nathanson.meterreader.data.Meter;
import com.nathanson.meterreader.fetch.DataFetcher;

public class NotificationHelper {

    private static final int THRESHOLD_ID = 1;

    private NotificationHelper() {}

    public static void showThresholdExceededNotification(Context context, Meter meter) {
        int usage = DataFetcher.getCurrentReading(meter);

        Resources res = context.getResources();
        String title = res.getString(R.string.threshold_notification_title);
        String message = String.format(res.getString(R.string.threshold_notification_message), usage);

        // code stolen from google sample.
        Notification.Builder mBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_warning_white_24dp)
                        .setContentTitle(title)
                        .setContentText(message);

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
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(THRESHOLD_ID, mBuilder.build());
    }
}
