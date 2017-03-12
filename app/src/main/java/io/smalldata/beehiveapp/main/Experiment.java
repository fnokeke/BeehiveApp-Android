package io.smalldata.beehiveapp.main;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import io.smalldata.beehiveapp.config.GeneralNotification;
import io.smalldata.beehiveapp.config.GoogleCalendar;
import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.config.Rescuetime;
import io.smalldata.beehiveapp.config.ScreenUnlock;
import io.smalldata.beehiveapp.config.Vibration;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

import static io.smalldata.beehiveapp.utils.Store.CALENDAR_FEATURE;
import static io.smalldata.beehiveapp.utils.Store.GEOFENCE_FEATURE;
import static io.smalldata.beehiveapp.utils.Store.IMAGE_FEATURE;
import static io.smalldata.beehiveapp.utils.Store.NOTIF_WINDOW_FEATURE;
import static io.smalldata.beehiveapp.utils.Store.RESCUETIME_FEATURE;
import static io.smalldata.beehiveapp.utils.Store.TEXT_FEATURE;

/**
 * Save experiment details for offline access
 * Created by fnokeke on 2/21/17.
 */

public class Experiment {

    private Context mContext;

    public Experiment(Context context) {
        mContext = context;
    }

    void saveToggles(JSONObject experiment) {
        if (experiment == null) return;
        Store.setBoolean(mContext, CALENDAR_FEATURE, experiment.optBoolean(CALENDAR_FEATURE));
        Store.setBoolean(mContext, GEOFENCE_FEATURE, experiment.optBoolean(GEOFENCE_FEATURE));
        Store.setBoolean(mContext, IMAGE_FEATURE, experiment.optBoolean(IMAGE_FEATURE));
        Store.setBoolean(mContext, NOTIF_WINDOW_FEATURE, experiment.optBoolean(NOTIF_WINDOW_FEATURE));
        Store.setBoolean(mContext, RESCUETIME_FEATURE, experiment.optBoolean(RESCUETIME_FEATURE));
        Store.setBoolean(mContext, TEXT_FEATURE, experiment.optBoolean(TEXT_FEATURE));
        Store.setString(mContext, "expTitle", experiment.optString("title", ""));
        Store.setString(mContext, "expStart", experiment.optString("start"));
        Store.setString(mContext, "expEnd", experiment.optString("end"));
    }

    public static JSONObject getExperimentInfo(Context context) {
        JSONObject experimentInfo = new JSONObject();
        Helper.setJSONValue(experimentInfo, "start", Store.getString(context, "expStart"));
        Helper.setJSONValue(experimentInfo, "end", Store.getString(context, "expEnd"));
        Helper.setJSONValue(experimentInfo, "title", Store.getString(context, "expTitle"));
        Helper.setJSONValue(experimentInfo, "code", Store.getString(context, "code"));
        return experimentInfo;
    }

    public void saveConfigs(JSONObject experiment) {
        if (experiment == null) return;

        saveToggles(experiment);

//        JSONArray calendarConfig = experiment.optJSONArray("calendar_config");
//        new GoogleCalendar(mContext).saveSettings(calendarConfig);
//        setConfigStatus(calendarConfig, "calendar_config_is_active");
//
//        JSONArray rescuetimeConfig = experiment.optJSONArray("rescuetime_config");
//        new Rescuetime(mContext).saveSettings(rescuetimeConfig);
//        setConfigStatus(rescuetimeConfig, "rescuetime_config_is_active");
//
//        JSONArray screenUnlockConfig = experiment.optJSONArray("screen_unlock_config");
//        new ScreenUnlock(mContext).saveSettings(screenUnlockConfig);
//        setConfigStatus(screenUnlockConfig, "screen_unlock_config_is_active");
//
//        JSONArray vibrationConfig = experiment.optJSONArray("vibration_config");
//        new Vibration(mContext).saveSettings(vibrationConfig);
//        setConfigStatus(vibrationConfig, "vibration_config_is_active");

        JSONArray interventions = experiment.optJSONArray("interventions");
        new Intervention(mContext).saveSettings(interventions);

        Store.printAll(mContext);
    }

    public void saveUserInfo(JSONObject user) {
        if (user == null) return;
        Store.setString(mContext, "code", user.optString("code"));
        Store.setString(mContext, "firstname", user.optString("firstname"));
        Store.setString(mContext, "lastname", user.optString("lastname"));
        Store.setString(mContext, "gender", user.optString("gender"));
        Store.setString(mContext, "email", user.optString("email"));
        Store.setString(mContext, "condition", user.optString("condition"));
    }

    public static JSONObject getUserInfo(Context context) {
        JSONObject userInfo = new JSONObject();
        Helper.setJSONValue(userInfo, "firstname", Store.getString(context, "firstname"));
        Helper.setJSONValue(userInfo, "lastname", Store.getString(context, "lastname"));
        Helper.setJSONValue(userInfo, "email", Store.getString(context, "email"));
        Helper.setJSONValue(userInfo, "gender", Store.getString(context, "gender"));
        Helper.setJSONValue(userInfo, "code", Store.getString(context, "code"));
        Helper.setJSONValue(userInfo, "condition", Store.getString(context, "condition"));
        return userInfo;
    }

    public static Integer getUserCondition(Context context) {
        return getUserInfo(context).optInt("condition");
    }

    public boolean notif_window_enabled() {
        return Store.getBoolean(mContext, Store.NOTIF_WINDOW_FEATURE);
    }

    public int getWindowMintues() {
        return Store.getInt(mContext, Store.INTV_USER_WINDOW_MINS);
    }

    public String getInterventionReminderTime() {
        return Store.getString(mContext, Store.INTV_WHEN);
    }

    private void setConfigStatus(JSONArray config, String configName) {
        if (config != null) {
            Store.setBoolean(mContext, configName, config.length() > 0);
        } else {
            Store.setBoolean(mContext, configName, false);
        }
    }
}
