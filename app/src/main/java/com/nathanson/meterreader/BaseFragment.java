package com.nathanson.meterreader;


import android.app.Activity;
import android.app.Fragment;

public class BaseFragment extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(this);
    }

}
