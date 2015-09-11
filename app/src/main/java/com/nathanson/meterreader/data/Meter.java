package com.nathanson.meterreader.data;


import java.util.ArrayList;
import java.util.List;

/**
 * Meter information.
 */
public class Meter {
    private String mId;
    private List<MeterReading> mReadings = new ArrayList<>();

    public Meter(String id) {
        mId = id;
    }

    public Meter addReading(MeterReading reading) {
        mReadings.add(reading);
        return this;
    }

    public List<MeterReading> getReadings() {
        return mReadings;
    }

}
