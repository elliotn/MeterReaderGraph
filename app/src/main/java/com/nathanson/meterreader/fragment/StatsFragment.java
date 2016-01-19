package com.nathanson.meterreader.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nathanson.meterreader.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StatsFragment extends BaseFragment {
    private static final String TAG = "StatsFragment";
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View statsLayout = inflater.inflate(R.layout.fragment_stats, container, false);

        ButterKnife.bind(this, statsLayout);


        return statsLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
