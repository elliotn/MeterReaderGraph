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

package com.nathanson.meterreader.activity;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.nathanson.meterreader.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends Activity{

    @Bind(R.id.aboutWv) WebView mAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final CharSequence aboutChars = getResources().getText(R.string.about);

        // using webview to display html (bold, breaks, etc)
        // display it
        mAbout.loadData(aboutChars.toString(), "text/html", "utf-8");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
