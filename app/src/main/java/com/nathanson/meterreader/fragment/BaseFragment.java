package com.nathanson.meterreader.fragment;


import android.app.Activity;
import android.app.Fragment;

import com.nathanson.meterreader.activity.MainActivity;

public class BaseFragment extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(this);
    }

}
