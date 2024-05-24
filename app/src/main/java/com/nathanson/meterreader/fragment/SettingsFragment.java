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

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TimePicker;

import com.nathanson.meterreader.MeterReaderApplication;
import com.nathanson.meterreader.R;
import com.nathanson.meterreader.databinding.FragmentSettingsBinding;
import com.nathanson.meterreader.persistence.MeterReaderSharedPreferences;
import com.nathanson.meterreader.threshold.ThresholdAlarm;
import com.nathanson.meterreader.util.ToastHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SettingsFragment extends BaseFragment
        implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "SettingsFragment";

    private static final String STATE_LOCATION = "STATE_LOCATION";
    private static final String STATE_UNITS = "STATE_UNITS";
    private static final String STATE_DAILY_CHECK = "STATE_DAILY_CHECK";
    private static final String STATE_HOUR = "STATE_HOUR";
    private static final String STATE_MIN = "STATE_MIN";
    private static final String STATE_THRESHOLD = "STATE_THRESHOLD";

    private MeterReaderSharedPreferences mSharedPrefs;

    private FragmentSettingsBinding binding;

    private int mAutoCheckHour = -1;
    private int mAutoCheckMin = -1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater);

        binding.autoCheckBox.setOnClickListener(this);
        binding.autoCheckTime.setOnClickListener(this);
        binding.settingsOKButton.setOnClickListener(this);

        mSharedPrefs = MeterReaderApplication.getInstance().getSharedPrefs();

        if (savedInstanceState == null) {
            restore();
        } else {
            binding.dataUrl.setText(savedInstanceState.getString(STATE_LOCATION));
            binding.units.setText(savedInstanceState.getString(STATE_UNITS));

            boolean autocheck = savedInstanceState.getBoolean(STATE_DAILY_CHECK);
            binding.autoCheckBox.setChecked(autocheck);

            mAutoCheckHour = savedInstanceState.getInt(STATE_HOUR);
            mAutoCheckMin = savedInstanceState.getInt(STATE_MIN);
            if (mAutoCheckHour != -1 && mAutoCheckMin != -1) {
                onTimeSet(null, mAutoCheckHour, mAutoCheckMin);
            }

            binding.alertThreshold.setText(savedInstanceState.getString(STATE_THRESHOLD));

            setEnabledThresholdViews(autocheck);

        }

        return binding.getRoot();
    }

    private void saveNotification() {
        ToastHelper.showToast(getActivity().getApplicationContext(), R.string.toast_settings_saved);
    }

    private void save() {

        // TODO: data validation?
        mSharedPrefs.setUrl(binding.dataUrl.getText().toString());
        mSharedPrefs.setUsageAlertThreshold(Integer.valueOf(binding.alertThreshold.getText().toString()));

        mSharedPrefs.setUnits(binding.units.getText().toString());

        Context context = MeterReaderApplication.getInstance().getApplicationContext();
        ThresholdAlarm alarm = new ThresholdAlarm();
        // cancel any previously set alarm.
        alarm.cancel(context);

        mSharedPrefs.setAutocheck(binding.autoCheckBox.isChecked());
        if (mAutoCheckHour != -1 && mAutoCheckMin != -1) {
            mSharedPrefs.setAutocheckHour(mAutoCheckHour);
            mSharedPrefs.setAutocheckMin(mAutoCheckMin);

            // set new alarm.
            alarm.schedule(context, mAutoCheckHour, mAutoCheckMin);
        }

        saveNotification();
    }

    private void restore() {
        binding.dataUrl.setText(mSharedPrefs.getUrl());

        binding.units.setText(mSharedPrefs.getUnits());

        binding.alertThreshold.setText(String.valueOf(mSharedPrefs.getUsageAlertThreshold()));

        boolean autocheck = mSharedPrefs.getAutocheck();
        binding.autoCheckBox.setChecked(autocheck);

        if (autocheck) {
            mAutoCheckHour = mSharedPrefs.getAutocheckHour();
            mAutoCheckMin = mSharedPrefs.getAutocheckMin();
            if (mAutoCheckHour != -1 && mAutoCheckMin != -1) {
                onTimeSet(null, mAutoCheckHour, mAutoCheckMin);
            }
        }
        setEnabledThresholdViews(autocheck);
    }

    private void setEnabledThresholdViews(boolean enabled) {
        binding.autoCheckTime.setEnabled(enabled);
        binding.alertThreshold.setEnabled(enabled);
        binding.alertThresholdLabel.setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.settingsOKButton:
                hideKeyboard();
                save();
                break;

            case R.id.autoCheckBox:
                CheckBox cb = (CheckBox) v;
                setEnabledThresholdViews(cb.isChecked());
                break;

            case R.id.autoCheckTime:
                showTimePicker();
                break;

            default:
                // do nothing.
        }
    }

    private void showTimePicker() {
        // use current time if no previous time set.
        final Calendar cal = Calendar.getInstance();
        int hour =  mAutoCheckHour == -1 ? cal.get(Calendar.HOUR_OF_DAY) : mAutoCheckHour;
        int minute = mAutoCheckMin == -1 ? cal.get(Calendar.MINUTE) : mAutoCheckMin;

        // Create a new instance of TimePickerDialog and return it
        new TimePickerDialog(getActivity(),
                this, hour, minute,
                DateFormat.is24HourFormat(getActivity())).show();

    }

    // yanked from stackoverflow.
    private void hideKeyboard() {
        Activity activity = getActivity();
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // hang on to selection.
        mAutoCheckHour = hourOfDay;
        mAutoCheckMin = minute;

        SimpleDateFormat dateFormat;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        if (DateFormat.is24HourFormat(getActivity())) {
            dateFormat = new SimpleDateFormat("HH:mm");
        } else {
            dateFormat = new SimpleDateFormat("h:mm a");
        }

        String autoCheckTime = dateFormat.format(cal.getTime());

        binding.autoCheckTime.setText(autoCheckTime);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_LOCATION, binding.dataUrl.getText().toString());
        outState.putString(STATE_UNITS, binding.units.getText().toString());
        outState.putBoolean(STATE_DAILY_CHECK, binding.autoCheckBox.isEnabled());
        outState.putInt(STATE_HOUR, mAutoCheckHour);
        outState.putInt(STATE_MIN, mAutoCheckMin);
        outState.putString(STATE_THRESHOLD, binding.alertThreshold.getText().toString());
    }


}