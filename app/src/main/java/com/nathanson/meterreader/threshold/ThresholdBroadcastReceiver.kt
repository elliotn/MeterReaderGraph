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
package com.nathanson.meterreader.threshold

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager


class ThresholdBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<ThresholdWorker>().build()
        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)

        // TODO: revisit the shared prefs stuff.
//        val sharedPrefs =
//            MeterReaderApplication.getInstance().sharedPrefs

//         honor user's setting for auto-checking meter at boot up.
//        if (sharedPrefs.getAutocheck()) {
//            Intent thresholdIntent = new Intent(context, ThresholdIntentService.class);
//            thresholdIntent.putExtra(ThresholdIntentService.BOOT_EVENT, true);
//            context.startForegroundService(thresholdIntent);
//        }
    }
}

