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

package com.nathanson.meterreader.fragment;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.nathanson.meterreader.MeterReaderApplication;
import com.nathanson.meterreader.R;
import com.nathanson.meterreader.activity.MainActivity;
import com.nathanson.meterreader.data.Meter;
import com.nathanson.meterreader.data.MeterReading;
import com.nathanson.meterreader.databinding.FragmentStatsBinding;
import com.nathanson.meterreader.fetch.DataFetcher;
import com.nathanson.meterreader.util.ToastHelper;

import java.util.Calendar;
import java.util.List;

public class StatsFragment extends BaseFragment
        implements DataFetcher.OnDataFetchedListener, View.OnClickListener {

        private static final String TAG = "StatsFragment";
        private static final String USAGE_FORMAT = "%,d %s";

        private FragmentStatsBinding binding;

        private long mMinDate;
        private long mMaxDate;

//        @Bind(R.id.last30Days)
//        TextView last30Days;
//        @Bind(R.id.last30DaysUsage)
//        TextView last30DaysUsage;
//        @Bind(R.id.last30DaysUsageData)
//        TextView last30DaysUsageData;
//        @Bind(R.id.last30DaysDailyAveData)
//        TextView last30DaysDailyAveData;
//        @Bind(R.id.billComparison)
//        TextView billComparison;
//        @Bind(R.id.billComparisonStart)
//        TextView billComparisonStart;
//        @Bind(R.id.billComparisonStartDate)
//        TextView billComparisonStartDate;
//        @Bind(R.id.billComparisonEnd)
//        TextView billComparisonEnd;
//        @Bind(R.id.billComparisonEndDate)
//        TextView billComparisonEndDate;
//        @Bind(R.id.billComparisonCalculate)
//        Button billComparisonCalculate;
//        @Bind(R.id.billComparisonUsage)
//        TextView billComparisonUsage;
//        @Bind(R.id.billComparisonUsageData)
//        TextView billComparisonUsageData;
//        @Bind(R.id.billComparisonDailyAveData)
//        TextView billComparisonDailyAveData;


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


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

                binding = FragmentStatsBinding.inflate(inflater);
//                View statsLayout = inflater.inflate(R.layout.fragment_stats, container, false);
//
//                ButterKnife.bind(this, statsLayout);

                binding.billComparisonCalculate.setOnClickListener(this);
                binding.billComparisonStartDate.setOnClickListener(this);
                binding.billComparisonEndDate.setOnClickListener(this);

                return binding.getRoot();
        }

        @Override
        public void onDestroyView() {
                super.onDestroyView();
                binding = null;
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
                Meter firstMeter = mMeters.get(0);
                getMinMaxDates(firstMeter);
                calcLast30Days(firstMeter);
                setInitialDates(firstMeter);

                binding.billComparisonCalculate.setEnabled(true);
        }

        @Override
        public void onError(String error) {
                // TODO: implement
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                        case R.id.billComparisonCalculate:
                                calcBillComparison(mMeters.get(0));
                                break;

                        case R.id.billComparisonStartDate:
                                resetBillComparisonResults();
                                showDatePicker(binding.billComparisonStartDate.getText().toString(), mStartDateListener);
                                break;

                        case R.id.billComparisonEndDate:
                                resetBillComparisonResults();
                                showDatePicker(binding.billComparisonEndDate.getText().toString(), mEndDateListener);
                                break;

                        default:
                                // do nothing.
                                break;
                }
        }

        private Calendar mCal = Calendar.getInstance();

        private long getDateInMillis(String date) {
                String[] dateArray = date.split("/");

                mCal.set(Calendar.MONTH, Integer.valueOf(dateArray[0]) + 1);
                mCal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateArray[1]));
                mCal.set(Calendar.YEAR, Integer.valueOf(dateArray[2]));

                return mCal.getTimeInMillis();
        }

        private void getMinMaxDates(Meter meter) {
                List<MeterReading> readings = meter.getReadings();
                mMinDate = getDateInMillis(readings.get(0).getTimeStamp());
                mMaxDate = getDateInMillis(readings.get(readings.size() - 1).getTimeStamp());
        }

        private DatePickerDialog.OnDateSetListener mStartDateListener =
                new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String formattedTimeStamp = String.format(MeterReading.TIMESTAMP_FORMAT, monthOfYear + 1, dayOfMonth, year);
                                long selectedStartDate = getDateInMillis(formattedTimeStamp);

                                // show toast if selected date is before first meter reading.
                                if (selectedStartDate < mMinDate) {
                                        String minDate = mMeters.get(0).getReadings().get(0).getTimeStamp();
                                        String error = String.format(getResources().getString(R.string.start_date_error), minDate);

                                        ToastHelper.showToast(getActivity().getApplicationContext(), error);
                                } else {
                                        binding.billComparisonStartDate.setText(formattedTimeStamp);
                                }
                        }
                };


        private DatePickerDialog.OnDateSetListener mEndDateListener =
                new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String formattedTimeStamp = String.format(MeterReading.TIMESTAMP_FORMAT, monthOfYear + 1, dayOfMonth, year);
                                long selectedEndDate = getDateInMillis(formattedTimeStamp);

                                if (selectedEndDate > mMaxDate) {
                                        List<MeterReading> readings = mMeters.get(0).getReadings();

                                        String maxDate = readings.get(readings.size() - 1).getTimeStamp();
                                        String error = String.format(getResources().getString(R.string.end_date_error), maxDate);

                                        ToastHelper.showToast(getActivity().getApplicationContext(), error);
                                } else if (selectedEndDate <= getDateInMillis(binding.billComparisonStartDate.getText().toString())) {
                                        // ensure end date is after start date.
                                        ToastHelper.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.end_date_before_start_date_error));
                                } else {
                                        binding.billComparisonEndDate.setText(formattedTimeStamp);
                                }
                        }
                };

        private void showDatePicker(String date, DatePickerDialog.OnDateSetListener dateListener) {
                String[] dateArray = date.split("/");

                new DatePickerDialog(getActivity(),
                        dateListener,
                        Integer.valueOf(dateArray[2]),
                        Integer.valueOf(dateArray[0]) - 1 ,
                        Integer.valueOf(dateArray[1]))
                .show();

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


                binding.last30DaysUsageData.setText(usageString);
                binding.last30DaysDailyAveData.setText(averageString);
        }

        private void calcBillComparison(Meter meter) {

                String[] startDateArray = binding.billComparisonStartDate.getText().toString()
                        .split("/");

                String[] endDateArray =  binding.billComparisonEndDate.getText().toString()
                        .split("/");

                int startIndex = findIndex(meter, 0, binding.billComparisonStartDate.getText().toString());
                int endIndex = findIndex(meter, startIndex, binding.billComparisonEndDate.getText().toString());

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


                binding.billComparisonUsageData.setText(usageString);
                binding.billComparisonDailyAveData.setText(averageString);
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

                binding.billComparisonStartDate.setText(firstReading);
                binding.billComparisonEndDate.setText(lastReading);
        }

        private void resetBillComparisonResults() {
                binding.billComparisonUsageData.setText("");
                binding.billComparisonDailyAveData.setText("");
        }
}
