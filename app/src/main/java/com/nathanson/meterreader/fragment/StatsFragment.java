package com.nathanson.meterreader.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nathanson.meterreader.MeterReaderApplication;
import com.nathanson.meterreader.R;
import com.nathanson.meterreader.activity.MainActivity;
import com.nathanson.meterreader.data.Meter;
import com.nathanson.meterreader.data.MeterReading;
import com.nathanson.meterreader.fetch.DataFetcher;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StatsFragment extends BaseFragment
        implements DataFetcher.OnDataFetchedListener, View.OnClickListener {

        private static final String TAG = "StatsFragment";
        private static final String USAGE_FORMAT = "%,d %s";


        @Bind(R.id.last30Days)
        TextView last30Days;
        @Bind(R.id.last30DaysUsage)
        TextView last30DaysUsage;
        @Bind(R.id.last30DaysUsageData)
        TextView last30DaysUsageData;
        @Bind(R.id.last30DaysDailyAveData)
        TextView last30DaysDailyAveData;
        @Bind(R.id.billComparison)
        TextView billComparison;
        @Bind(R.id.billComparisonStart)
        TextView billComparisonStart;
        @Bind(R.id.billComparisonStartDate)
        TextView billComparisonStartDate;
        @Bind(R.id.billComparisonEnd)
        TextView billComparisonEnd;
        @Bind(R.id.billComparisonEndDate)
        TextView billComparisonEndDate;
        @Bind(R.id.billComparisonCalculate)
        Button billComparisonCalculate;
        @Bind(R.id.billComparisonUsage)
        TextView billComparisonUsage;
        @Bind(R.id.billComparisonUsageData)
        TextView billComparisonUsageData;
        @Bind(R.id.billComparisonDailyAveData)
        TextView billComparisonDailyAveData;


        private List<Meter> mMeters;

        private final String UNITS = MeterReaderApplication.getInstance().getSharedPrefs().getUnits();


        public static StatsFragment newInstance() {
                return new StatsFragment();
        }

        public StatsFragment() {
                // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                setRetainInstance(true);
        }


        @Override
        public void onStart() {
                super.onStart();

                showProgress(true);
                fetchData();
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

                View statsLayout = inflater.inflate(R.layout.fragment_stats, container, false);

                ButterKnife.bind(this, statsLayout);

                billComparisonCalculate.setOnClickListener(this);

                return statsLayout;
        }

        @Override
        public void onDestroyView() {
                super.onDestroyView();
                ButterKnife.unbind(this);
        }


        private void showProgress(boolean show) {
                ((MainActivity) getActivity()).showProgress(show);
        }

        private void fetchData() {
                DataFetcher.getInstance(getActivity().getApplicationContext()).fetchData(this);
        }

        @Override
        public void onDataFetched(List<Meter> meters) {
                showProgress(false);

                mMeters = meters;

                // only reading one meter.
                // TODO: graph multiple meters?
                calcLast30Days(mMeters.get(0));
                setInitialDates(mMeters.get(0));

                billComparisonCalculate.setEnabled(true);
        }

        @Override
        public void onError(String error) {
                // TODO: implement
        }

        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                        case R.id.billComparisonCalculate:
                                calcBillComparison(mMeters.get(0));
                                break;

                        default:
                                // do nothing.
                                break;
                }
        }

        private void calcLast30Days(Meter meter) {

                List<MeterReading> readings = meter.getReadings();

                int readingCount = readings.size();
                if (readingCount < 30) {
                        return;
                }


                int usage = 0;
                for (int lv=readingCount - 30 + 1; lv < readingCount; lv++) {
                        MeterReading prevReading = readings.get(lv - 1);
                        MeterReading currReading = readings.get(lv);

                        usage += (currReading.getConsumption() - prevReading.getConsumption()) * 10;
                }

                int average = usage / 30;

                String usageString = String.format(USAGE_FORMAT, usage, UNITS);
                String averageString = String.format(USAGE_FORMAT, average, UNITS);


                last30DaysUsageData.setText(usageString);
                last30DaysDailyAveData.setText(averageString);
        }

        private void calcBillComparison(Meter meter) {

                String[] startDateArray = billComparisonStartDate.getText().toString()
                        .split("/");

                String[] endDateArray =  billComparisonEndDate.getText().toString()
                        .split("/");

                int startIndex = findIndex(meter, 0, billComparisonStartDate.getText().toString());
                int endIndex = findIndex(meter, startIndex, billComparisonEndDate.getText().toString());

                List<MeterReading> readings = meter.getReadings();

                int readingCount = endIndex - startIndex;

                int usage = 0;
                for (int lv=startIndex + 1; lv < endIndex; lv++) {
                        MeterReading prevReading = readings.get(lv - 1);
                        MeterReading currReading = readings.get(lv);

                        usage += (currReading.getConsumption() - prevReading.getConsumption()) * 10;
                }

                int average = usage / readingCount;

                String usageString = String.format(USAGE_FORMAT, usage, UNITS);
                String averageString = String.format(USAGE_FORMAT, average, UNITS);


                billComparisonUsageData.setText(usageString);
                billComparisonDailyAveData.setText(averageString);
        }

        private int findIndex(Meter meter, int startIndex, String findDate) {
                List<MeterReading> readings = meter.getReadings();

                int readingCount = readings.size();
                if (readingCount == 0) {
                        return -1;
                }

                for (int lv=startIndex; lv < readingCount; lv++) {
                        if (findDate.equals(readings.get(lv).getTimeStamp())) {
                                // success
                                return lv;
                        }
                }

                return -1;
        }


        private void setInitialDates(Meter meter) {
                List<MeterReading> readings = meter.getReadings();

                int readingCount = readings.size();
                if (readingCount == 0) {
                        return;
                }

                String firstReading = readings.get(0).getTimeStamp();
                String lastReading = readings.get(readingCount - 1).getTimeStamp();

                billComparisonStartDate.setText(firstReading);
                billComparisonEndDate.setText(lastReading);
        }

        private void reset() {
                // TODO: clean calculated stuff when calculate is called.
        }
}
