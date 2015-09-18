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

package com.nathanson.meterreader.fetch;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nathanson.meterreader.MeterReaderApplication;
import com.nathanson.meterreader.R;
import com.nathanson.meterreader.data.Meter;
import com.nathanson.meterreader.data.MeterReading;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataFetcher {

    private volatile static DataFetcher INSTANCE;

    private static final String TAG = "DataFetcher";

    private RequestQueue mQueue;

    private Resources mResources;

    private static final String ID_KEY = "id";
    private static final String TIMESTAMP_KEY = "time";
    private static final String CONSUMPTION_KEY = "consumption";
    private static final String MESSAGE_KEY = "message";

    private String mExternalPath;

    public interface OnDataFetchedListener {
        void onDataFetched(List<Meter> meters);
        void onError(String error);
    }

    private DataFetcher() {}


    public static DataFetcher getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DataFetcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataFetcher();
                    INSTANCE.mQueue = Volley.newRequestQueue(context);

                    INSTANCE.mExternalPath =
                            Environment.getExternalStorageDirectory().getPath() + "/" + context.getPackageName()+"/";

                    INSTANCE.mResources = context.getResources();
                }
            }
        }
        return INSTANCE;
    }

    public void fetchData(OnDataFetchedListener fetchListener) {
        fetch(fetchListener);
    }

    /**
     * Creates the package name directory, creating missing parent
     * directories if necessary.
     * @return {@code true} if the directory was created or already existed,
     *         {@code false} on failure
     * @return
     */
    private boolean mkdir() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            File dir = new File(mExternalPath);
            if (!dir.exists()) {
                return dir.mkdirs();
            } else {
                return true;
            }
        }
        return false;
    }

    private void save() {
        // TODO: impl.
    }

    private void restore() {
        // TODO: impl.
    }


    /**
     * Calculates current meter reading.
     * @param meter
     * @return consumption if two or more readings have occurred, 0 otherwise.
     */
    public static int getCurrentReading(Meter meter) {
        List<MeterReading> readings = meter.getReadings();
        int size = readings.size();

        // not enough entries to calculate consumption.
        if (size <2) {
            return 0;
        } else {
            int prevConsumption = readings.get(size - 2).getConsumption();
            int currConsumption = readings.get(size - 1).getConsumption();

            return (currConsumption - prevConsumption) * 10;
        }
    }



    /**
     * Determines if most current consumption exceeded threshold.
     * @param meter
     * @return true if threshold exceeded, false otherwise.
     */
    public static boolean isCurrentReadingAboveThreshold(Meter meter) {
        List<MeterReading> readings = meter.getReadings();
        int size = readings.size();

        // not enough entries to calculate consumption.
        if (size <2) {
            return false;
        } else {
            int threshold = MeterReaderApplication.getInstance().getSharedPrefs()
                    .getUsageAlertThreshold();

            return getCurrentReading(meter) >= threshold;
        }
    }

    private void fetch(final OnDataFetchedListener fetchListener) {
        String url = MeterReaderApplication.getInstance().getSharedPrefs().getUrl();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Meter> meters = parse(response);

                            if (fetchListener != null) {
                                fetchListener.onDataFetched(meters);
                            }
                        } catch (IOException ex) {
                            Log.e(TAG, "failed to parse data" + ex.getMessage());
                            if (fetchListener != null) {
                                fetchListener.onError(mResources.getString(R.string.data_fetch_parse_error));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Unable to get server response." + error.getMessage());
                if (fetchListener != null) {
                    fetchListener.onError(mResources.getString(R.string.data_fetch_server_error));
                }
            }
        });

        // Add the request to the RequestQueue.
        mQueue.add(stringRequest);
    }


    /**
     * Takes response from server, multiple entries, and creates a JSON array out of them.
     * @param response JSON response from server.
     * @return JSON array.
     * @throws IOException
     */
    private String makeJsonArray(String response) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        boolean firstLine = true;

        InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(response.getBytes()));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;

        while ( (line = bufferedReader.readLine()) != null) {
            sb.append(firstLine ? ' ' : ',')
                    .append(line);

            firstLine = false;
        }

        sb.append("]");

        return sb.toString();
    }

    // TODO: exception handling
    private List<Meter> parse(String response) throws IOException {
        List<Meter> meters = new ArrayList<>();

        // hold list of discovered meters. assuming one for now.
        HashMap<String, Meter> meterMap = new HashMap<>();
        Meter meter = null;

        response = makeJsonArray(response);

        InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(response.getBytes()));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JsonReader reader = new JsonReader(inputStreamReader);

        reader.beginArray();
        while (reader.hasNext()) {
            FetchedMeterReading reading = (FetchedMeterReading) readEntry(reader);
            String id = reading.getId();

            // if meter does not exist.
            if ( (meter = meterMap.get(id)) == null) {
                meter = new Meter(id);
                // update list of unique meter IDs.
                meterMap.put(id, meter);
                // update master list of meters.
                meters.add(meter);
            };

            // update reading for given meter.
            meter.addReading(reading);
        }
        reader.endArray();

        return meters;
    }


    private static final String DATE_FORMAT =
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZZZZZ";
    // 2015-09-04 T 15:47:59.170345114-06:00

    private SimpleDateFormat mDateFormatter = new SimpleDateFormat(DATE_FORMAT);
    private Calendar mCalendar = Calendar.getInstance();
    private StringBuilder mTimeStamp = new StringBuilder();


    /**
     * Update time stamp to be MM/DD.
     * @param timeStamp
     * @return
     */
    private String formatTimeStamp(String timeStamp) {
        mTimeStamp.setLength(0);

        try {
            Date date = mDateFormatter.parse(timeStamp);
            mCalendar.setTime(date);

            // go back a day to reflect previous day's usage
            mCalendar.add(Calendar.DATE, -1);

            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            int month = mCalendar.get(Calendar.MONTH) + 1;

            mTimeStamp.append(month)
                    .append("/")
                    .append(day);

        } catch (ParseException exc) {
            // TODO: implement
            Log.e(TAG, "date parsing error.");
        }

        return mTimeStamp.toString();
    }


    public MeterReading readEntry(JsonReader reader) throws IOException {
        String timeStamp = "";
        MeterReading meterReading = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equalsIgnoreCase(TIMESTAMP_KEY)) {
                timeStamp = formatTimeStamp(reader.nextString());
            } else if (name.equalsIgnoreCase(MESSAGE_KEY)) {
                meterReading = readMessage(timeStamp, reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return meterReading;
    }

    private MeterReading readMessage(String timeStamp, JsonReader reader) throws  IOException {
        String id = "";
        int consumption = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equalsIgnoreCase(CONSUMPTION_KEY)) {
                consumption = reader.nextInt();
            } else if (name.equalsIgnoreCase(ID_KEY)) {
                id = String.valueOf(reader.nextLong());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new FetchedMeterReading(id, timeStamp, consumption);
    }

    private class FetchedMeterReading extends MeterReading {
        private String mId;

        public FetchedMeterReading(String id, String timeStamp, int consumption) {
            super(timeStamp, consumption);
            mId = id;
        }

        public String getId() {
            return mId;
        }
    }


}
