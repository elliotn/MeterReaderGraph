package com.nathanson.meterreader.util;


import com.github.mikephil.charting.utils.ValueFormatter;
import com.nathanson.meterreader.MeterReaderApplication;

import java.text.DecimalFormat;

public class MyValueFormatter implements ValueFormatter {

    private StringBuilder mStringBuilder = new StringBuilder();
    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
    }

    @Override
    public String getFormattedValue(float value) {

        mStringBuilder.setLength(0);

        String units = MeterReaderApplication.getInstance().getSharedPrefs().getUnits();

        mStringBuilder
                .append(mFormat.format(value))
                .append(' ')
                .append(units);

        return mStringBuilder.toString();
    }

}