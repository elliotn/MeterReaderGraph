package com.nathanson.meterreader;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nathanson.meterreader.persistence.MeterReaderSharedPreferences;
import com.nathanson.meterreader.threshold.ThresholdAlarm;
import com.nathanson.meterreader.util.ToastHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsFragment extends BaseFragment
        implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "SettingsFragment";

    private MeterReaderSharedPreferences mSharedPrefs;

    private EditText mUrl;

    private EditText mAlertThreshold;
    private TextView mThresholdLabel;
    private CheckBox mAutoCheckBox;
    private TextView mAutoCheckTime;
    private int mAutoCheckHour = -1;
    private int mAutoCheckMin = -1;

    private EditText mUnits;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View settingsLayout = inflater.inflate(R.layout.fragment_settings, container, false);

        mUrl = (EditText) settingsLayout.findViewById(R.id.dataUrl);
        mAlertThreshold = (EditText) settingsLayout.findViewById(R.id.alertThreshold);

        mAutoCheckBox = (CheckBox) settingsLayout.findViewById(R.id.autoCheckBox);
        mAutoCheckBox.setOnClickListener(this);

        mAutoCheckTime = (TextView) settingsLayout.findViewById(R.id.autoCheckTime);
        mAutoCheckTime.setOnClickListener(this);

        mThresholdLabel = (TextView) settingsLayout.findViewById(R.id.alertThresholdLabel);

        mUnits = (EditText) settingsLayout.findViewById(R.id.units);

        Button okButton = (Button) settingsLayout.findViewById(R.id.settingsOKButton);
        okButton.setOnClickListener(this);

        mSharedPrefs = MeterReaderApplication.getInstance().getSharedPrefs();

        return settingsLayout;
    }

    @Override
    public void onStart() {
        super.onStart();

        restore();
    }

    private void saveNotification() {
        ToastHelper.showToast(getActivity().getApplicationContext(), R.string.toast_settings_saved);
    }

    private void save() {

        // TODO: data validation?
        mSharedPrefs.setUrl(mUrl.getText().toString());
        mSharedPrefs.setUsageAlertThreshold(Integer.valueOf(mAlertThreshold.getText().toString()));

        mSharedPrefs.setUnits(mUnits.getText().toString());

        Context context = getActivity().getApplicationContext();
        ThresholdAlarm alarm = new ThresholdAlarm();
        // cancel any previously set alarm.
        alarm.cancel(context);

        mSharedPrefs.setAutocheck(mAutoCheckBox.isChecked());
        if (mAutoCheckHour != -1 && mAutoCheckMin != -1) {
            mSharedPrefs.setAutocheckHour(mAutoCheckHour);
            mSharedPrefs.setAutocheckMin(mAutoCheckMin);

            // set new alarm.
            alarm.schedule(context, mAutoCheckHour, mAutoCheckMin);
        }

        saveNotification();
    }

    private void restore() {
        mUrl.setText(mSharedPrefs.getUrl());

        mUnits.setText(mSharedPrefs.getUnits());

        mAlertThreshold.setText(String.valueOf(mSharedPrefs.getUsageAlertThreshold()));

        boolean autocheck = mSharedPrefs.getAutocheck();
        mAutoCheckBox.setChecked(autocheck);

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
        mAutoCheckTime.setEnabled(enabled);
        mAlertThreshold.setEnabled(enabled);
        mThresholdLabel.setEnabled(enabled);
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

        mAutoCheckTime.setText(autoCheckTime);
    }



}