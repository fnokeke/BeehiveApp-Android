package io.smalldata.beehiveapp.config;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import io.smalldata.beehiveapp.utils.Store;

/**
 * Vibration configurations set on Beehive Platform will be handled here.
 * Created by fnokeke on 2/21/17.
 */

public class Vibration extends BaseConfig {
    private Context mContext;
    private final static String APP_ID = "app_id";
    private final static String TIME_LIMIT = "time_limit";
    private final static String OPEN_LIMIT = "open_limit";
    private final static String VIBRATION_STRENGTH = "vibration_strength";
    private final static String SHOW_STATS = "show_stats";

    public Vibration(Context context) {
        mContext = context;
    }

    public void saveSettings(JSONArray vibrationConfig) {
        if (vibrationConfig == null || vibrationConfig.length() == 0) return;

        JSONObject lastItem = vibrationConfig.optJSONObject(vibrationConfig.length() - 1);
        Store.setString(mContext, APP_ID, lastItem.optString(APP_ID));
        Store.setString(mContext, TIME_LIMIT, lastItem.optString(TIME_LIMIT));
        Store.setString(mContext, OPEN_LIMIT, lastItem.optString(OPEN_LIMIT));
        Store.setString(mContext, VIBRATION_STRENGTH, lastItem.optString(VIBRATION_STRENGTH));
        Store.setString(mContext, SHOW_STATS, lastItem.optString(SHOW_STATS));
    }

}
