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

package com.nathanson.meterreader;

import static com.nathanson.meterreader.util.NotificationHelper.CHANNEL_ID;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.nathanson.meterreader.persistence.MeterReaderSharedPreferences;

public class MeterReaderApplication extends Application {

    private MeterReaderSharedPreferences mSharedPrefs;

    private static MeterReaderApplication sInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPrefs = MeterReaderSharedPreferences.getInstance(this);
        sInstance = this;
        setupNotification();
    }

    public static MeterReaderApplication getInstance() {
        return sInstance;
    }

    public MeterReaderSharedPreferences getSharedPrefs() {
        return mSharedPrefs;
    }

    private void setupNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO: how to make sound STOP!!!!!!
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                getString(R.string.notifications_category),
                NotificationManager.IMPORTANCE_DEFAULT);
        mChannel.enableVibration(false);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    /**
     * Check to see if notification channel/groups have been allowed by user.
     * @param notifyMgr
     * @return true if user needs to go to settings, false otherwise.
     */
    private static boolean sendToNotificationSettings(NotificationManager notifyMgr) {
        MeterReaderApplication application = MeterReaderApplication.getInstance();

        for (NotificationChannel notificationChannel : notifyMgr.getNotificationChannels()) {
            if (notificationChannel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                application.getSharedPrefs().setNotificationSettings(false);
                return true;
            }
        }

        // no need
        return false;
    }
}
