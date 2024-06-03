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


import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.nathanson.meterreader.R;
import com.nathanson.meterreader.fragment.BarChartFragment;
import com.nathanson.meterreader.fragment.NavigationDrawerFragment;
import com.nathanson.meterreader.fragment.SettingsFragment;
import com.nathanson.meterreader.fragment.StatsFragment;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, BarChartFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    public static final int SETTINGS_POSITION = 0;
    public static final int GRAPH_POSITION = 1;
    public static final int STATS_POSITION = 2;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enable progress bar in action bar.
        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        // use existing fragment, if possible.
        String fragmentTag = String.valueOf(position);
        Fragment frag = fragmentManager.findFragmentByTag(fragmentTag);

        fragmentManager.beginTransaction()
                .replace(R.id.container, frag != null ? frag : fragmentFactory(position), fragmentTag)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (!alarmManager.canScheduleExactAlarms()) {
            // Ask users to go to exact alarm page in system settings.
            ContextCompat.startActivity(this, new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM), new Bundle());
        }
    }

    private Fragment fragmentFactory (int position) {
        switch (position) {
            case SETTINGS_POSITION:
                return SettingsFragment.newInstance();

            case GRAPH_POSITION:
                return BarChartFragment.newInstance();

            case STATS_POSITION:
                return StatsFragment.newInstance();

            default:
                return new Fragment();
        }
    }

    public void onSectionAttached(Fragment fragment) {
        int number = Integer.valueOf(fragment.getTag());

        switch (number) {
            case SETTINGS_POSITION:
                mTitle = getString(R.string.title_settings);
                break;
            case GRAPH_POSITION:
                mTitle = getString(R.string.title_graph);
                break;

            case STATS_POSITION:
                mTitle = getString(R.string.title_stats);
                break;

            default:
                // do nothing.
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void showProgress(boolean show) {
        setProgressBarIndeterminateVisibility(show);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // launch about activity.
        if (id == R.id.action_about) {
            Intent aboutActivity = new Intent(this, AboutActivity.class);
            startActivity(aboutActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO: what to do here?
    }

}
