package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by fnokeke on 1/23/17.
 */

public class Store {

    private static final String PREF_NAME = "beehivePrefs";

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

    public static void setBoolean(Context context, String key, Boolean input) {
        getPrefs(context).edit().putBoolean(key, input).apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        return getPrefs(context).getBoolean(key, false); // use false as default value
    }

    public static void reset(Context context) {
        getPrefs(context).edit().clear().apply();
    }


    public static void save_user_features(Context context, JSONObject response, JSONObject user) {
        if (response.optString("user_response").contains("Successful")) {
            Store.setString(context, "code", user.optString("code"));
            Store.setString(context, "firstname", user.optString("firstname"));
            Store.setString(context, "lastname", user.optString("lastname"));
            Store.setString(context, "gender", user.optString("gender"));
            Store.setString(context, "email", user.optString("email"));
            Store.setString(context, "condition", user.optString("condition"));
            Store.setString(context, "code", user.optString("code"));
        }

    }

    @SuppressWarnings("unchecked")
    public static void printAll(Context context) {
        Map<String, ?> prefs = getPrefs(context).getAll();
        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal =  key + " : " + (Boolean) pref;
            }
            if (pref instanceof Float) {
                printVal =  key + " : " + (Float) pref;
            }
            if (pref instanceof Integer) {
                printVal =  key + " : " + (Integer) pref;
            }
            if (pref instanceof Long) {
                printVal =  key + " : " + (Long) pref;
            }
            if (pref instanceof String) {
                printVal =  key + " : " + (String) pref;
            }
            if (pref instanceof Set<?>) {
                printVal =  key + " : " + (Set<String>) pref;
            }

            Log.d("PrefValue", printVal);
            // create a TextView with printVal as text and add to layout
        }
    }
}
