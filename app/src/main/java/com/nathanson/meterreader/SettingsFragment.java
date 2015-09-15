package com.nathanson.meterreader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nathanson.meterreader.persistence.MeterReaderSharedPreferences;
import com.nathanson.meterreader.util.ToastHelper;

public class SettingsFragment extends BaseFragment
        implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";

    private MeterReaderSharedPreferences mSharedPrefs;

    private EditText mUrl;
    private EditText mAlertThreshold; // for future use.

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

        saveNotification();
    }

    private void restore() {
        mUrl.setText(mSharedPrefs.getUrl());
        mAlertThreshold.setText(String.valueOf(mSharedPrefs.getUsageAlertThreshold()));
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.settingsOKButton:
                hideKeyboard();
                save();
                break;
            default:
                // do nothing.
        }
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

}