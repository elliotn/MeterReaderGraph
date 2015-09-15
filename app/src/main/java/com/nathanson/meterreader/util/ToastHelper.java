package com.nathanson.meterreader.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
import android.support.annotation.StringRes;
import com.nathanson.meterreader.R;


public class ToastHelper {

    private ToastHelper() {}


    public static void showToast(Context context, String message) {
        int yOffset = context.getResources().getDimensionPixelSize(R.dimen.toast_y_offset);

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, yOffset);
        toast.show();
    }

    public static void showToast(Context context, @StringRes int resId) {
        showToast(context, context.getResources().getString(resId));
    }
}
