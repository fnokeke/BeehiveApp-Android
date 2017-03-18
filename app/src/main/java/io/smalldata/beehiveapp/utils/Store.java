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

    public static final String CALENDAR_FEATURE = "calendar";
    public static final String GEOFENCE_FEATURE = "geofence";
    public static final String NOTIF_WINDOW_FEATURE = "notif_window";
    public static final String RESCUETIME_FEATURE = "rescuetime";
    public static final String TEXT_FEATURE = "text";
    public static final String IMAGE_FEATURE = "image";

    public static final String INTV_START = "iStart";
    public static final String INTV_END = "iEnd";
    public static final String INTV_EVERY = "every";
    public static final String INTV_REPEAT = "repeat";
    public static final String INTV_WHEN = "when";
    public static final String INTV_USER_WINDOW_MINS = "user_window_mins";
    public static final String INTV_TREATMENT_TEXT = "treatment_text";
    public static final String INTV_TREATMENT_IMAGE = "treatment_image";
    public static final String INTV_TYPE = "intv_type";
    public static final String INTV_NOTIF = "notif";

    public static final String LAST_SCHEDULED_REMINDER_TIME = "lastNotifTime";
    public final static String STATS_CAL = "statsCal";
    public static final String LAST_CHECKED_INTV_DATE = "lastCheckedDate";
    public static final String IS_EXIT_BUTTON = "isExitButton";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setString(Context context, String key, String input) {
        getPrefs(context).edit().putString(key, input).apply();
    }

    public static String getString(Context context, String key) {
        return getPrefs(context).getString(key, "");
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
