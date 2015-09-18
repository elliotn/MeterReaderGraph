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

import android.app.Application;

import com.nathanson.meterreader.persistence.MeterReaderSharedPreferences;

public class MeterReaderApplication extends Application {

    private MeterReaderSharedPreferences mSharedPrefs;

    private static MeterReaderApplication sInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPrefs = MeterReaderSharedPreferences.getInstance(this);
        sInstance = this;
    }

    public static MeterReaderApplication getInstance() {
        return sInstance;
    }

    public MeterReaderSharedPreferences getSharedPrefs() {
        return mSharedPrefs;
    }

}
