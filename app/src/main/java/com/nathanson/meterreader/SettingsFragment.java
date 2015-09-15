package com.nathanson.meterreader;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nathanson.meterreader.persistence.MeterReaderSharedPreferences;

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
        int yOffset = getResources().getDimensionPixelSize(R.dimen.toast_y_offset);

        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                getResources().getString(R.string.toast_settings_saved),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, yOffset);
        toast.show();
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
                save();
                break;
            default:
                // do nothing.
        }
    }


}