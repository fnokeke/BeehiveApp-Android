package io.smalldata.beehiveapp.config;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import io.smalldata.beehiveapp.utils.Store;

/**
 * ScreenUnlock configurations set on Beehive Platform will be handled here.
 * Created by fnokeke on 2/21/17.
 */

public class ScreenUnlock extends BaseConfig {

    private Context mContext;
    private final static String TIME_LIMIT = "time_limit";
    private final static String UNLOCKED_LIMIT = "unlocked_limit";
    private final static String VIBRATION_STRENGTH = "vibration_strength";
    private final static String SHOW_STATS = "show_stats";
    private final static String ENABLE_USER_PREF = "enable_user_pref";
    private final static String START_TIME = "start_time";
    private final static String END_TIME = "end_time";

     public ScreenUnlock(Context context) {
        mContext = context;
    }

     public void saveSettings(JSONArray unlockConfig) {
        if (unlockConfig == null || unlockConfig.length() == 0) return;

        JSONObject lastItem = unlockConfig.optJSONObject(unlockConfig.length() - 1);
         Store.setString(mContext, TIME_LIMIT, lastItem.optString(TIME_LIMIT));
         Store.setString(mContext, UNLOCKED_LIMIT, lastItem.optString(UNLOCKED_LIMIT));
         Store.setString(mContext, VIBRATION_STRENGTH, lastItem.optString(VIBRATION_STRENGTH));
         Store.setString(mContext, SHOW_STATS, lastItem.optString(SHOW_STATS));
         Store.setString(mContext, ENABLE_USER_PREF, lastItem.optString(ENABLE_USER_PREF));
         Store.setString(mContext, START_TIME, lastItem.optString(START_TIME));
         Store.setString(mContext, END_TIME, lastItem.optString(END_TIME));
    }

}
