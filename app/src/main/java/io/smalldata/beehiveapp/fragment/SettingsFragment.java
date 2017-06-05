package io.smalldata.beehiveapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.config.DailyReminder;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Store;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Handles all settings selected by user
 * Created by fnokeke on 1/20/17.
 * Handles all values from Settings fragment
 */


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Experiment experiment;
    private DailyReminder dailyReminder;

    private PreferenceScreen preferenceScreen;
    private PreferenceCategory calendarCategory, geofenceCategory, rescuetimeCategory, weekdayScheduleCategory, weekendScheduleCategory;

    private ListPreference weekdayReminderWindow;
    private ListPreference weekendReminderWindow;

    private static final String TAG = "SettingsFragment";
    private final String USERNAME_PREF = "username_pref";
    private final String WEEKDAY_WAKEUP = "weekday_wakeup_time_pref";
    private final String WEEKEND_WAKEUP = "weekend_wakeup_time_pref";
    private final String WEEKDAY_SLEEP = "weekday_sleep_time_pref";
    private final String WEEKEND_SLEEP = "weekend_sleep_time_pref";
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        addPreferencesFromResource(R.xml.preferences);

        initView();
        displayEnabledPrefCategories();
        removeOtherPrefCategories();
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
        Log.i(TAG, "onSharedPreferenceChanged: " + key);
        if (key.equals(USERNAME_PREF)) updateUsernameFromPref();
        if (key.contains("time_pref")) updateTimeWindowView();
        if (key.contains("reminder_window_pref")) applyGeneratedReminders(mContext, key);
    }

    private void applyGeneratedReminders(Context context, String prefKey) {
        if (!isTodayPrefUpdate(prefKey)) return;
        generateAndStoreReminders(context);
        JSONObject allReminders = getCurrentReminders(context);
        long bedTimeAlarmMillis = allReminders.optLong("bedtime_reminder");
        long dailyAlarmMillis = allReminders.optLong("daily_reminder");
        dailyReminder.setReminderBeforeBedTime(bedTimeAlarmMillis, true);
        dailyReminder.setTodayReminder(dailyAlarmMillis, true);
    }

    private boolean isTodayPrefUpdate(String prefKey) {
        return todayIsWeekend() ? prefKey.contains("weekend") : prefKey.contains("weekday");
    }

    private static boolean notReadyToGenerate(Context context) {
        return getSelectedWindowTime(context).equals("");
    }

    public static void generateAndStoreReminders(Context context) {
        if (notReadyToGenerate(context)) return;
        Store.setLong(context, Store.GEN_DAILY_REMINDER, generateDailyReminder(context));
        Store.setLong(context, Store.GEN_BEDTIME_REMINDER, generateBedTimeReminder(context));
    }

    private static long generateDailyReminder(Context context) {
        String userWindowTime = getSelectedWindowTime(context);
        String[] window = userWindowTime.split("-");
        int startHour = Integer.parseInt(window[0]);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, startHour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        int endHour = Integer.parseInt(window[1]);
        int diffInMinutes = (endHour - startHour) * 60;
        long millisFromStart = DateHelper.getRandomInt(0, diffInMinutes) * 60 * 1000;
        return cal.getTimeInMillis() + millisFromStart;
    }

    public static long generateBedTimeReminder(Context context) {
        int hoursBeforeSleep = getHoursBeforeSleep(context);
        String key = todayIsWeekend() ? "weekend_sleep_time_pref" : "weekday_sleep_time_pref";
        long sleepTimeInMillis = getDefaultSharedPreferences(context).getLong(key, 0);
        sleepTimeInMillis = extendToNextDayIfNeeded(sleepTimeInMillis);
        long millisBeforeSleep = DateHelper.getRandomInt(0, hoursBeforeSleep * 60) * 60 * 1000;
        return sleepTimeInMillis - millisBeforeSleep;
    }

    private static long extendToNextDayIfNeeded(long sleepTimeInMillis) {
        Calendar sleepTime = Calendar.getInstance();
        sleepTime.setTimeInMillis(sleepTimeInMillis);
        Calendar now = Calendar.getInstance();
        if (sleepTime.before(now)) sleepTime.add(Calendar.DAY_OF_MONTH, 1);
        return sleepTime.getTimeInMillis();
    }

    private static int getHoursBeforeSleep(Context context) {
        return Store.getInt(context, Store.INTV_FREE_HOURS_BEFORE_SLEEP);
    }

    public static String getSelectedWindowTime(Context context) {
        int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String key = (currentDayOfWeek == 1 || currentDayOfWeek == 7) ? "weekend_reminder_window_pref" : "weekday_reminder_window_pref";
        return getDefaultSharedPreferences(context).getString(key, "");
    }

    public static JSONObject getCurrentReminders(Context context) {
        JSONObject alarms = new JSONObject();
        JsonHelper.setJSONValue(alarms, context.getString(R.string.daily_reminder), Store.getLong(context, Store.GEN_DAILY_REMINDER));
        JsonHelper.setJSONValue(alarms, context.getString(R.string.bedtime_reminder), Store.getLong(context, Store.GEN_BEDTIME_REMINDER));
        return alarms;
    }

    private void initView() {
        getActivity().setTitle("Settings");
        experiment = new Experiment(mContext);
        dailyReminder = new DailyReminder(mContext);

        String username = getUsername(mContext);
        if (!username.equals("")) {
            findPreference(USERNAME_PREF).setSummary(username);
        }

        preferenceScreen = (PreferenceScreen) findPreference(getString(R.string.allSettingsPref));
        calendarCategory = (PreferenceCategory) findPreference(getString(R.string.calendarPrefCategory));
        geofenceCategory = (PreferenceCategory) findPreference(getString(R.string.geofencePrefCategory));
        rescuetimeCategory = (PreferenceCategory) findPreference(getString(R.string.rescuetimePrefCategory));
        weekdayScheduleCategory = (PreferenceCategory) findPreference("weekday_schedule");
        weekendScheduleCategory = (PreferenceCategory) findPreference("weekend_schedule");
        weekdayReminderWindow = (ListPreference) findPreference(getString(R.string.weekday_reminder_window_pref));
        weekendReminderWindow = (ListPreference) findPreference(getString(R.string.weekend_reminder_window_pref));
    }

    private void displayEnabledPrefCategories() {
        if (experiment.canShowSettings()) {
            preferenceScreen.setEnabled(true);
            updateTimeWindowView();
        }

        if (!Experiment.isNotifWindowEnabled(mContext)) {
            weekdayScheduleCategory.setEnabled(false);
            weekendScheduleCategory.setEnabled(false);
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

    private void updateTimeWindowView() {
        if (Experiment.isNotifWindowEnabled(mContext)) {
            int windowHours = experiment.getIntvUserWindowHours();
            int freeHoursBeforeSleep = experiment.getFreeHoursBeforeSleep();
            setListPreferenceData("weekday", windowHours, freeHoursBeforeSleep);
            setListPreferenceData("weekend", windowHours, freeHoursBeforeSleep);
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
            entries.add(getString(R.string.default_window_exact_hours));
            entryValues.add(getString(R.string.default_window_time));
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

    private void removeOtherPrefCategories() {
        preferenceScreen.removePreference(calendarCategory);
        preferenceScreen.removePreference(rescuetimeCategory);
        preferenceScreen.removePreference(geofenceCategory);
    }

    public static boolean canShowRescuetimeInfo(Context context) {
        return getDefaultSharedPreferences(context).getBoolean("show_rescuetime_pref", false);
    }

    public static boolean canShowCalendarInfo(Context context) {
        return getDefaultSharedPreferences(context).getBoolean("show_calendar_pref", false);
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

    public static void wipeAll(Context context) {
        getDefaultSharedPreferences(context).edit().clear().apply();
    }

    private static boolean todayIsWeekend() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == 1 || dayOfWeek == 7;
    }


}
