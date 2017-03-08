package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

//import android.support.v4.app.Fragment;

/**
 * Created by fnokeke on 1/20/17.
 * Handles all values from Settings fragment
 */


public class SettingsFragment extends PreferenceFragment {
    Activity mActivity;
    Context mContext;
    Experiment experiment;
    PreferenceScreen preferenceScreen;
    PreferenceCategory  calendarCategory, geofenceCategory, rescuetimeCategory, windowPrefCategory;
    Resources resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mActivity = getActivity();
        addPreferencesFromResource(R.xml.preferences);

        setResources();
        setOrRemovePrefCategories();
    }

    private void setResources() {
        mActivity.setTitle("Settings");
        resources = getResources();
        experiment = new Experiment(mContext);

        preferenceScreen = (PreferenceScreen) findPreference(resources.getString(R.string.allSettingsPref));
        calendarCategory = (PreferenceCategory) findPreference(resources.getString(R.string.calendarPrefCategory));
        geofenceCategory = (PreferenceCategory) findPreference(resources.getString(R.string.geofencePrefCategory));
        rescuetimeCategory = (PreferenceCategory) findPreference(resources.getString(R.string.rescuetimePrefCategory));
        windowPrefCategory = (PreferenceCategory) findPreference(resources.getString(R.string.userTimeWindowCategory));
    }

    private void setOrRemovePrefCategories() {
        if (!Store.getBoolean(mContext, Store.NOTIF_WINDOW_FEATURE)) {
            preferenceScreen.removePreference(windowPrefCategory);
        } else {
            String title = String.format("select %s minutes notif window", experiment.getWindowMintues());
            windowPrefCategory.setTitle(title);
        }

        if (!Store.getBoolean(mContext, Store.CALENDAR_FEATURE)) {
            preferenceScreen.removePreference(calendarCategory);
        }

        if (!Store.getBoolean(mContext, Store.RESCUETIME_FEATURE)) {
            preferenceScreen.removePreference(rescuetimeCategory);
        }

        if (!Store.getBoolean(mContext, Store.GEOFENCE_FEATURE)) {
            preferenceScreen.removePreference(geofenceCategory);
        }

    }

    public static boolean canShowRescuetimeInfo(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_rescuetime_pref", false);
    }

    public static boolean canShowCalendarInfo(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_calendar_pref", false);
    }

    public static long getStartTimeFromSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong("start_time_pref", 0);
    }
}
