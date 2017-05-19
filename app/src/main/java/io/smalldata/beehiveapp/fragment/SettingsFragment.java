package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.config.DailyReminder;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.Store;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

//import android.support.v4.app.Fragment;

/**
 * Created by fnokeke on 1/20/17.
 * Handles all values from Settings fragment
 */


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    Activity mActivity;
    Context mContext;
    Experiment experiment;
    PreferenceScreen preferenceScreen;
    PreferenceCategory calendarCategory, geofenceCategory, rescuetimeCategory;
    Resources resources;
    DailyReminder dailyReminder;

    private ListPreference weekdayReminderWindow;
    private ListPreference weekendReminderWindow;

    private final String USERNAME_PREF = "username_pref";
    private final String WEEKDAY_WAKEUP = "weekday_wakeup_time_pref";
    private final String WEEKEND_WAKEUP = "weekend_wakeup_time_pref";
    private final String WEEKDAY_SLEEP = "weekday_sleep_time_pref";
    private final String WEEKEND_SLEEP = "weekend_sleep_time_pref";
    private static final String WEEKEND_WINDOW = "weekend_reminder_window";
    private static final String WEEKDAY_WINDOW = "weekday_reminder_window";
    private static final int LAST_FREE_HOURS_B4_SLEEP = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mActivity = getActivity();
        addPreferencesFromResource(R.xml.preferences);

        setResources();
        setOrRemovePrefCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("username_pref")) updateUsernameFromPref();
        if (key.contains("time_pref")) {
            updateTimeWindowFromUserPrefs();
        }

    }

    private void updateUsernameFromPref() {
        String username = getUsername(mContext);
        String msg = String.format("Welcome %s!", username);

        if (!getUsername(mContext).equals("")) {
            findPreference(USERNAME_PREF).setSummary(username);
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }

    }

    private void setResources() {
        mActivity.setTitle("Settings");
        resources = getResources();
        experiment = new Experiment(mContext);
        dailyReminder = new DailyReminder(mContext);

        String username = getUsername(mContext);
        if (!username.equals("")) {
            findPreference("username_pref").setSummary(username);
        }

        preferenceScreen = (PreferenceScreen) findPreference(resources.getString(R.string.allSettingsPref));
        calendarCategory = (PreferenceCategory) findPreference(resources.getString(R.string.calendarPrefCategory));
        geofenceCategory = (PreferenceCategory) findPreference(resources.getString(R.string.geofencePrefCategory));
        rescuetimeCategory = (PreferenceCategory) findPreference(resources.getString(R.string.rescuetimePrefCategory));
        weekdayReminderWindow = (ListPreference) findPreference(resources.getString(R.string.weekday_reminder_window));
        weekendReminderWindow = (ListPreference) findPreference(resources.getString(R.string.weekend_reminder_window));
    }

    private void setOrRemovePrefCategories() {
        if (settingsEnabledFromUserConnection()) {
            preferenceScreen.setEnabled(true);
            updateTimeWindowFromUserPrefs();
            updateAppFeaturesFromUserPrefs();
        }
    }

    private void updateTimeWindowFromUserPrefs() {
        if (Store.getBoolean(mContext, Store.NOTIF_WINDOW_FEATURE)) {
            int windowHours = experiment.getAdminHourWindow();
            setListPreferenceData("weekday", windowHours, LAST_FREE_HOURS_B4_SLEEP);
            setListPreferenceData("weekend", windowHours, LAST_FREE_HOURS_B4_SLEEP);
        }
    }

    private void setListPreferenceData(String dayType, int windowHours, int last_free_hours) {
        HashMap<String, Integer> timePrefs = getAllTimePrefs();
        int windowStart = dayType.equals("weekday") ? timePrefs.get(WEEKDAY_WAKEUP) : timePrefs.get(WEEKEND_WAKEUP);
        int windowEnd = dayType.equals("weekday") ? timePrefs.get(WEEKDAY_SLEEP) : timePrefs.get(WEEKEND_SLEEP);
        windowEnd = windowEnd < 12 ? windowEnd + 24 : windowEnd;  // e.g. Midnight = 24:00, 1AM = 25:00, 2AM = 26:00
        windowEnd -= last_free_hours; // end time should be not involve last k hours before user sleep time.

        ArrayList<String> entries = new ArrayList<>();
        ArrayList<String> entryValues = new ArrayList<>();
        String key, value;
        String startWindow, endWindow;
        int k;

        for (int i = windowStart; i + windowHours <= windowEnd; i += windowHours) {
            k = i + windowHours;
            startWindow = toAmPm(i);
            endWindow = toAmPm(k);

            key = String.format("%s to %s", startWindow, endWindow);
            value = String.format("%s-%s", i, k);

            entries.add(key);
            entryValues.add(value);
        }

        if (entries.size() == 0) {
            entries.add("4pm to 7pm");
            entryValues.add("16-19");
        }

        CharSequence[] csEntries, csEntryValues;
        csEntries = entries.toArray(new CharSequence[entries.size()]);
        csEntryValues = entryValues.toArray(new CharSequence[entryValues.size()]);

        ListPreference lp = dayType.equals("weekday") ? weekdayReminderWindow : weekendReminderWindow;
        lp.setEntries(csEntries);
        lp.setEntryValues(csEntryValues);
    }

    private String toAmPm(int hourOfDay) {
        String result = String.format("%sam", hourOfDay);
        if (hourOfDay == 12) {
            result = "Noon";
        } else if (hourOfDay > 12 && hourOfDay < 24) {
            result = String.format("%spm", hourOfDay % 12);
        } else if (hourOfDay == 24) {
            result = "Midnight";
        } else if (hourOfDay > 24) {
            result = String.format("%sam", hourOfDay % 24);
        }
        return result;
    }

    private void updateAppFeaturesFromUserPrefs() {
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
        return getDefaultSharedPreferences(context).getBoolean("show_rescuetime_pref", false);
    }

    public static boolean canShowCalendarInfo(Context context) {
        return getDefaultSharedPreferences(context).getBoolean("show_calendar_pref", false);
    }

    public static String getSelectedWindowTime(Context context) {
        int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = currentDayOfWeek == 1 || currentDayOfWeek == 7;
        String key = isWeekend ? WEEKEND_WINDOW : WEEKDAY_WINDOW;
        return getDefaultSharedPreferences(context).getString(key, "");
    }

    public HashMap<String, Integer> getAllTimePrefs() {
        String[] listOfKeys = {
                WEEKDAY_WAKEUP,
                WEEKEND_WAKEUP,
                WEEKDAY_SLEEP,
                WEEKEND_SLEEP
        };
        HashMap<String, Integer> map = new HashMap<>();
        for (String key : listOfKeys) {
            map.put(key, getPreferenceHourValue(key));
        }
        return map;
    }

    /**
     * Extend wakeup time by one hour if minutes not zero but leave sleep time hour the same.
     * Example: wakeup time is 8am then hour is 8; wakeup time is 8:15am then hour is 9.
     * Example: sleep time is 11pm then hour is 23; sleep time is 23:15pm, hour is still 23.
     */
    private int getPreferenceHourValue(String key) {
        long timeInMillis = PreferenceManager.getDefaultSharedPreferences(mContext).getLong(key, 0);
        Calendar cal = Calendar.getInstance();
        if (timeInMillis != 0) {
            cal.setTimeInMillis(timeInMillis);
        }
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (key.toLowerCase().contains("wakeup")) {
            hour = cal.get(Calendar.MINUTE) > 0 ? hour + 1 : hour;
        }
        return hour;
    }

    public static String getUsername(Context context) {
        return getDefaultSharedPreferences(context).getString("username_pref", "");
    }

    public String getPrefString(String key) {
        return getDefaultSharedPreferences(mContext).getString(key, "");
    }

    public static void wipeAll(Context context) {
        getDefaultSharedPreferences(context).edit().clear().apply();
    }

    private boolean settingsEnabledFromUserConnection() {
        return Store.getBoolean(mContext, Store.SETTINGS_ENABLED);
    }
}
