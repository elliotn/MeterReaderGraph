/*
 * Copyright (C) 2024 Elliot Nathanson
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

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import com.nathanson.meterreader.MeterReaderApplication
import com.nathanson.meterreader.data.Meter
import com.nathanson.meterreader.fetch.DataFetcher
import com.nathanson.meterreader.fetch.DataFetcher.OnDataFetchedListener
import com.nathanson.meterreader.util.NotificationHelper

class ThresholdWorker (ctx: Context, params: WorkerParameters) : ListenableWorker(ctx, params) {
    companion object {
        private const val TAG = "ThresholdWorker"
    }

    private val mBootEvent = false

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->

            val callback2 = object : OnDataFetchedListener {
                override fun onDataFetched(meters: MutableList<Meter>?) {
                    Log.d(TAG, "onDataFetched() called with: meters = [$meters]")


                    // TODO: cache data and reuse when app started by user?

                    // only reading one meter.
                    // TODO: graph multiple meters?
                    val meter = meters!![0]

                    if (DataFetcher.isCurrentReadingAboveThreshold(meter)) {
                        NotificationHelper.showThresholdExceededNotification(applicationContext, meter)
                    }


                    // TODO: any reason this would NOT be needed anymore?
                    val sharedPrefs =
                        MeterReaderApplication.getInstance().sharedPrefs


                    // setup alarm, if applicable.
                    if (mBootEvent && sharedPrefs.autocheck) {
                        val hour = sharedPrefs.autocheckHour
                        val min = sharedPrefs.autocheckMin
                        if (hour != -1 && min != -1) {
                            val context = applicationContext
                            val alarm = ThresholdAlarm()
                            alarm.schedule(context, hour, min)
                        }
                    }

                    completer.set(Result.success())
                }

                override fun onError(error: String?) {
                    completer.setException(Exception())
                }

            }
            DataFetcher.getInstance(applicationContext).fetchData(callback2)
            callback2
        }
    }

}