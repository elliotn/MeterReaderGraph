package com.nathanson.meterreader.threshold;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.nathanson.meterreader.data.Meter;
import com.nathanson.meterreader.fetch.DataFetcher;
import com.nathanson.meterreader.util.NotificationHelper;

import java.util.List;

public class ThresholdIntentService extends IntentService
        implements DataFetcher.OnDataFetchedListener {

    public ThresholdIntentService() {
        super("ThresholdIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fetchData();
    }

    private void fetchData() {
        DataFetcher.getInstance(getApplicationContext()).fetchData(this);
    }

    @Override
    public void onDataFetched(List<Meter> meters) {
        // TODO: cache data and reuse when app started by user?

        // only reading one meter.
        // TODO: graph multiple meters?
        Meter meter = meters.get(0);

        if (DataFetcher.isCurrentReadingAboveThreshold(meter)) {
            NotificationHelper.showThresholdExceededNotification(getApplicationContext(), meter);
        }
    }

    @Override
    public void onError(String error) {
        NotificationHelper.showThresholdFailureNotification(getApplicationContext());
    }

}
