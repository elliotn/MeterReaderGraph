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
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.nathanson.meterreader.activity.MainActivity;
import com.nathanson.meterreader.data.Meter;
import com.nathanson.meterreader.data.MeterReading;
import com.nathanson.meterreader.databinding.FragmentBarChartBinding;
import com.nathanson.meterreader.fetch.DataFetcher;
import com.nathanson.meterreader.util.MyValueFormatter;
import com.nathanson.meterreader.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BarChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BarChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarChartFragment extends BaseFragment
        implements OnChartValueSelectedListener, DataFetcher.OnDataFetchedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "BarChartFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FragmentBarChartBinding binding;

    private List<Meter> mMeters;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment BarChartFragment.
     */
    public static BarChartFragment newInstance() {
        BarChartFragment fragment = new BarChartFragment();
        return fragment;
    }

    public BarChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        showProgress(true);
        fetchData();
    }


    private void fetchData() {
        DataFetcher.getInstance(getActivity().getApplicationContext()).fetchData(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBarChartBinding.inflate(inflater);

        binding.chartSwipeRefreshLayout.setOnRefreshListener(this);

        binding.chart1.setOnChartValueSelectedListener(this);

        binding.chart1.setDrawBarShadow(false);
        binding.chart1.setDrawValueAboveBar(true);

        binding.chart1.setDescription("");

        // TODO: do we care about this?
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        binding.chart1.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        binding.chart1.setPinchZoom(false);

        binding.chart1.setDrawGridBackground(false);

        XAxis xAxis = binding.chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        ValueFormatter custom = new MyValueFormatter();

        YAxis leftAxis = binding.chart1.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = binding.chart1.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);

        Legend l = binding.chart1.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        return binding.getRoot();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showProgress(boolean show) {
        ((MainActivity) getActivity()).showProgress(show);
    }

    @Override
    public void onDataFetched(List<Meter> meters) {
        mMeters = meters;

        binding.chartSwipeRefreshLayout.setRefreshing(false);
        showProgress(false);

        // only reading one meter.
        // TODO: graph multiple meters?
        Meter meter = mMeters.get(0);
        setData(meter.getReadings().size(), 1000);
    }

    @Override
    public void onError(String error) {
        showProgress(false);
        binding.chartSwipeRefreshLayout.setRefreshing(false);

        ToastHelper.showToast(getActivity().getApplicationContext(), error);
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    private void setData(int count, float range) {

        // only using first meter for now.
        // TODO: graph multiple meters?
        Meter meter = mMeters.get(0);
        List<MeterReading> readings = meter.getReadings();

        ArrayList<Entry> averageEntries = new ArrayList<Entry>();

        // build up X-axis labels; e.g., dates.
        // build up Y-axis with values;
        // skip first date as we can't determine consumption.
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        float runningTotal = 0;
        float dailyAverage;
        for (int lv = 1; lv < count; lv++) {
            MeterReading prevReading = readings.get(lv - 1);
            MeterReading currReading = readings.get(lv);

            xVals.add(removeYearFromDate(currReading.getTimeStamp()));
            float val = (currReading.getConsumption() - prevReading.getConsumption()) * 10;
            yVals1.add(new BarEntry(val, lv -1));


            // calc & store daily average.
            runningTotal += val;
            dailyAverage = runningTotal / lv;
            averageEntries.add(new Entry(dailyAverage, lv -1));
        }

        // TODO: allow name to be updated.
        BarDataSet set1 = new BarDataSet(yVals1, "mine");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        // bar chart data
        BarData barData = new BarData(xVals, dataSets);
        barData.setValueFormatter(new MyValueFormatter());
        barData.setValueTextSize(10f);


        // line chart data
        LineData lineData = new LineData();
        LineDataSet set = new LineDataSet(averageEntries, "average");
        int lineColor = Color.rgb(251, 169, 165);
        set.setColor(lineColor);
        set.setLineWidth(1.5f);
        set.setDrawCircles(false);
        set.setFillColor(lineColor);
        set.setDrawCubic(true);
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineData.addDataSet(set);


        CombinedData combinedData = new CombinedData(getDates(meter.getReadings()));
        combinedData.setData(barData);
        combinedData.setData(lineData);

        binding.chart1.setData(combinedData);
        binding.chart1.invalidate();
    }

    private String removeYearFromDate(String date) {
        int currLen = date.length();
        return date.substring(0, currLen - 5);
    }

    private ArrayList<String> getDates(List<MeterReading> readings) {
        ArrayList<String> xVals = new ArrayList<String>();

        int count = readings.size();
        // skip first reading.
        for (int lv = 1; lv < count; lv++) {
            MeterReading currReading = readings.get(lv);

            xVals.add(removeYearFromDate(currReading.getTimeStamp()));
        }

        return xVals;
    }

    @SuppressLint("NewApi")
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        // do nothing.
    }

    public void onNothingSelected() {
        // do nothing.
    };


}
