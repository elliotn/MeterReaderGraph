package com.nathanson.meterreader.activity;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.nathanson.meterreader.R;

public class AboutActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // using webview to display html (bold, breaks, etc)
        final WebView about =
            (WebView) findViewById(R.id.aboutWv);

        final CharSequence aboutChars = getResources().getText(R.string.about);

        // display it
        about.loadData(aboutChars.toString(), "text/html", "utf-8");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
