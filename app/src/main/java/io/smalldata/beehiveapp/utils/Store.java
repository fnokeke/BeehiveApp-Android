package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

import static android.R.id.input;

/**
 * Created by fnokeke on 1/23/17.
 * Store shared preferences
 */

public class Store {

    private static final String PREF_NAME = "beehivePrefs";

    public static final String IS_CALENDAR_ENABLED = "calendar";
    public static final String IS_GEOFENCE_ENABLED = "geofence";
    public static final String IS_RESCUETIME_ENABLED = "rescuetime";
    public static final String IS_DASHBOARD_TEXT_ENABLED = "text";
    public static final String IS_DASHBOARD_IMAGE_ENABLED = "image";

    public static final String INTV_START = "iStart";
    public static final String INTV_END = "iEnd";
    public static final String INTV_EVERY = "every";
    public static final String INTV_REPEAT = "repeat";
    public static final String INTV_WHEN = "when";
    public static final String INTV_USER_WINDOW_HOURS = "user_window_hours";
    public static final String INTV_FREE_HOURS_BEFORE_SLEEP = "free_hours_before_sleep";
    public static final String INTV_TREATMENT_TEXT = "treatment_text";
    public static final String INTV_TREATMENT_IMAGE = "treatment_image";
    public static final String INTV_TYPE = "intv_type";
    public static final String INTV_NOTIF = "notif";
    public static final String INTV_DAILY_NOTIF_DISABLED = "intv_daily_notif_disabled";

    public static final String LAST_CHECKED_INTV_DATE = "lastCheckedDate";
    public static final String IS_EXIT_BUTTON = "isExitButton";
    public final static String CAN_SHOW_SETTINGS = "canShowSettings";
    public static final String LAST_REMINDER_DATE = "lastReminderDate";
    public static final String LAST_SCHEDULED_DAILY_REMINDER = "lastDailyReminder";
    public final static String LAST_SCHEDULED_BEDTIME_REMINDER = "lastBedTimeReminder";
    public final static String PAM_ID = "io.smalldatalab.android.pam";
    public static final String GEN_DAILY_REMINDER = "genDailyReminder";
    public static final String GEN_BEDTIME_REMINDER = "genBedTimeReminder";
    public static final String FIRST_EVER_DAILY_REMINDER_SET = "isFirstEverDailyReminder";
    public static final String FIRST_EVER_BED_REMINDER_SET = "isFirstEverBedReminder";
    public static final String HAS_PROMPTED_MONITORING_APP = "hasSetMonitoringApp";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setString(Context context, String key, String input) {
        getPrefs(context).edit().putString(key, input).apply();
    }

    public static String getString(Context context, String key) {
        return getPrefs(context).getString(key, "");
    }

    public static void setLong(Context context, String key, Long input) {
        getPrefs(context).edit().putLong(key, input).apply();
    }

    public static Long getLong(Context context, String key) {
        return getPrefs(context).getLong(key, 0);
    }
    public static void setInt(Context context, String key, Integer input) {
        getPrefs(context).edit().putInt(key, input).apply();
    }

    public static Integer getInt(Context context, String key) {
        return getPrefs(context).getInt(key, 0);
    }

    public static void setFloat(Context context, String key, Float input) {
        getPrefs(context).edit().putFloat(key, input).apply();
    }

    public static Float getFloat(Context context, String key) {
        return getPrefs(context).getFloat(key, 0);
    }

    public static void setBoolean(Context context, String key, Boolean input) {
        getPrefs(context).edit().putBoolean(key, input).apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        return getPrefs(context).getBoolean(key, false); // use false as default value
    }

    public static void wipeAll(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    @SuppressWarnings("unchecked")
    public static void printAll(Context context) {
        Map<String, ?> prefs = getPrefs(context).getAll();
        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Float) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Integer) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Long) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof String) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Set<?>) {
                printVal = key + " : " + pref;
            }

            Log.d("PrefValue", printVal);
        }
    }
}
