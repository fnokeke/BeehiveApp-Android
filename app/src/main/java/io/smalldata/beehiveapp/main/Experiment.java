package io.smalldata.beehiveapp.main;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

import static io.smalldata.beehiveapp.utils.Store.IS_CALENDAR_ENABLED;
import static io.smalldata.beehiveapp.utils.Store.IS_GEOFENCE_ENABLED;
import static io.smalldata.beehiveapp.utils.Store.IS_DASHBOARD_IMAGE_ENABLED;
import static io.smalldata.beehiveapp.utils.Store.IS_RESCUETIME_ENABLED;
import static io.smalldata.beehiveapp.utils.Store.IS_DASHBOARD_TEXT_ENABLED;

/**
 * Save experiment details for offline access
 * Created by fnokeke on 2/21/17.
 */

public class Experiment {

    private static boolean showSettings;
    private Context mContext;

    public Experiment(Context context) {
        mContext = context;
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
        if (experiment.length() == 0) return;

        saveToggles(experiment);
        new Intervention(mContext).saveSettings(experiment.optJSONArray("interventions"));
        Store.printAll(mContext);
    }

    private void saveToggles(JSONObject experiment) {
        if (experiment.length() == 0) return;

        Experiment.enableSettings(true);
        Store.setBoolean(mContext, IS_CALENDAR_ENABLED, experiment.optBoolean(IS_CALENDAR_ENABLED));
        Store.setBoolean(mContext, IS_GEOFENCE_ENABLED, experiment.optBoolean(IS_GEOFENCE_ENABLED));
        Store.setBoolean(mContext, IS_DASHBOARD_IMAGE_ENABLED, experiment.optBoolean(IS_DASHBOARD_IMAGE_ENABLED));
        Store.setBoolean(mContext, IS_RESCUETIME_ENABLED, experiment.optBoolean(IS_RESCUETIME_ENABLED));
        Store.setBoolean(mContext, IS_DASHBOARD_TEXT_ENABLED, experiment.optBoolean(IS_DASHBOARD_TEXT_ENABLED));
        Store.setString(mContext, "expTitle", experiment.optString("title", ""));
        Store.setString(mContext, "expStart", experiment.optString("start"));
        Store.setString(mContext, "expEnd", experiment.optString("end"));
    }

    public void saveUserInfo(JSONObject user) {
        if (user == null) return;
        Store.setString(mContext, "code", user.optString("code"));
        Store.setString(mContext, "firstname", user.optString("firstname"));
        Store.setString(mContext, "lastname", user.optString("lastname"));
        Store.setString(mContext, "gender", user.optString("gender"));
        Store.setString(mContext, "email", user.optString("email"));
        Store.setInt(mContext, "condition", user.optInt("condition"));
    }

    public static JSONObject getUserInfo(Context context) {
        JSONObject userInfo = new JSONObject();
        Helper.setJSONValue(userInfo, "firstname", Store.getString(context, "firstname"));
        Helper.setJSONValue(userInfo, "lastname", Store.getString(context, "lastname"));
        Helper.setJSONValue(userInfo, "email", Store.getString(context, "email"));
        Helper.setJSONValue(userInfo, "gender", Store.getString(context, "gender"));
        Helper.setJSONValue(userInfo, "code", Store.getString(context, "code"));
        Helper.setJSONValue(userInfo, "condition", Store.getInt(context, "condition"));
        return userInfo;
    }

    public static Integer getUserCondition(Context context) {
        return getUserInfo(context).optInt("condition");
    }

    public static boolean isNotifWindowEnabled(Context context) {
        return Store.getInt(context, Store.INTV_USER_WINDOW_HOURS) > 0;
    }

    public int getIntvUserWindowHours() {
        int hours = Store.getInt(mContext, Store.INTV_USER_WINDOW_HOURS);
        hours = hours > 0 ? hours : 1;
        return hours;
    }

    public int getFreeHoursBeforeSleep() {
        int hours = Store.getInt(mContext, Store.INTV_FREE_HOURS_BEFORE_SLEEP);
        hours = hours > 0 ? hours : 1;
        return hours;
    }
    public static String getInterventionReminderTime(Context context) {
        return Store.getString(context, Store.INTV_WHEN);
    }

    private void setConfigStatus(JSONArray config, String configName) {
        if (config != null) {
            Store.setBoolean(mContext, configName, config.length() > 0);
        } else {
            Store.setBoolean(mContext, configName, false);
        }
    }

    public static boolean canShowUserSettings() {
//        return !getUserInfo(context).equals("");
        return showSettings;
    }

    public static void enableSettings(boolean status) {
        showSettings = status;
    }
}
