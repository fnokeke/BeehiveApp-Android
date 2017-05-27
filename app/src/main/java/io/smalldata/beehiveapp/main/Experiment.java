package io.smalldata.beehiveapp.main;

import android.content.Context;

import org.json.JSONObject;

import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Store;

import static io.smalldata.beehiveapp.utils.Store.IS_CALENDAR_ENABLED;
import static io.smalldata.beehiveapp.utils.Store.IS_DASHBOARD_IMAGE_ENABLED;
import static io.smalldata.beehiveapp.utils.Store.IS_DASHBOARD_TEXT_ENABLED;
import static io.smalldata.beehiveapp.utils.Store.IS_GEOFENCE_ENABLED;
import static io.smalldata.beehiveapp.utils.Store.IS_RESCUETIME_ENABLED;

/**
 * Save experiment details for later access
 * Created by fnokeke on 2/21/17.
 */

public class Experiment {

    private Context mContext;

    public Experiment(Context context) {
        mContext = context;
    }

    public static JSONObject getExperimentInfo(Context context) {
        JSONObject experimentInfo = new JSONObject();
        JsonHelper.setJSONValue(experimentInfo, "start", Store.getString(context, "expStart"));
        JsonHelper.setJSONValue(experimentInfo, "end", Store.getString(context, "expEnd"));
        JsonHelper.setJSONValue(experimentInfo, "title", Store.getString(context, "expTitle"));
        JsonHelper.setJSONValue(experimentInfo, "code", Store.getString(context, "code"));
        return experimentInfo;
    }

    public void saveConfigs(JSONObject jsonExperimentInfo) {
        if (jsonExperimentInfo.length() == 0) return;
        saveToggles(jsonExperimentInfo);
        new Intervention(mContext).saveSettings(jsonExperimentInfo.optJSONArray("interventions"));
        Store.printAll(mContext);
    }

    private void saveToggles(JSONObject experiment) {
        if (experiment.length() == 0) return;
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
        JsonHelper.setJSONValue(userInfo, "firstname", Store.getString(context, "firstname"));
        JsonHelper.setJSONValue(userInfo, "lastname", Store.getString(context, "lastname"));
        JsonHelper.setJSONValue(userInfo, "email", Store.getString(context, "email"));
        JsonHelper.setJSONValue(userInfo, "gender", Store.getString(context, "gender"));
        JsonHelper.setJSONValue(userInfo, "code", Store.getString(context, "code"));
        JsonHelper.setJSONValue(userInfo, "condition", Store.getInt(context, "condition"));
        return userInfo;
    }

    public static JSONObject getFullUserDetails(Context context) {
        JSONObject fullUserDetails = Experiment.getUserInfo(context);
        JSONObject fromPhoneDetails = DeviceInfo.getPhoneDetails(context);
        JsonHelper.copy(fromPhoneDetails, fullUserDetails);
        return fullUserDetails;
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

    public boolean canShowSettings() {
        return Store.getBoolean(mContext, Store.CAN_SHOW_SETTINGS);
    }

    public void enableSettings(boolean status) {
        Store.setBoolean(mContext, Store.CAN_SHOW_SETTINGS, status);
    }

}
