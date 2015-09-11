package com.nathanson.meterreader.data;


/**
 * Data for a given reading, everything except the meter ID.
 */
public class MeterReading {
    private String mTimeStamp;
    private int mConsumption;



    public MeterReading (String timeStamp, int consumption) {
        mTimeStamp = timeStamp;
        mConsumption = consumption;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public int getConsumption() {
        return mConsumption;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public void setConsumption(int consumption) {
        mConsumption = consumption;
    }
}
