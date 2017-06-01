package io.smalldata.beehiveapp.config;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.fragment.SettingsFragment;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Store;

import static io.smalldata.beehiveapp.utils.Store.INTV_END;
import static io.smalldata.beehiveapp.utils.Store.INTV_EVERY;
import static io.smalldata.beehiveapp.utils.Store.INTV_NOTIF;
import static io.smalldata.beehiveapp.utils.Store.INTV_REPEAT;
import static io.smalldata.beehiveapp.utils.Store.INTV_START;
import static io.smalldata.beehiveapp.utils.Store.INTV_TREATMENT_IMAGE;
import static io.smalldata.beehiveapp.utils.Store.INTV_TREATMENT_TEXT;
import static io.smalldata.beehiveapp.utils.Store.INTV_WHEN;


/**
 * All daily interventions assigned through Beehive Platform will be handled here.
 * Created by fnokeke on 2/15/17.
 */

public class Intervention extends BaseConfig {
    private Context mContext;
    private static final String TAG = "Intervention";

    public Intervention(Context context) {
        mContext = context;
    }

    @Override
    public void saveSettings(JSONArray interventions) {
        if (interventions == null) {
            interventions = new JSONArray();
        }
        storeAllInterventions(interventions);
        prepareTodayIntervention(mContext);
    }

    private void storeAllInterventions(JSONArray interventions) {
        Store.setString(mContext, "interventions", interventions.toString());
    }

    public static void prepareTodayIntervention(Context context) {
        JSONArray interventions = getAllInterventions(context);
        JSONObject intv;
        int i;
        for (i = 0; i < interventions.length(); i++) {
            intv = JsonHelper.strToJsonObject(interventions.optString(i));
            if (isForToday(intv) && isTodayInterventionType(context, intv)) {
                Log.i(TAG, "TodayIntv: " + intv.toString());

                Store.setString(context, Store.INTV_START, intv.optString("start"));
                Store.setString(context, Store.INTV_END, intv.optString("end"));
                Store.setString(context, Store.INTV_EVERY, intv.optString("every"));
                Store.setString(context, Store.INTV_REPEAT, intv.optString("repeat"));
                Store.setString(context, Store.INTV_TREATMENT_TEXT, intv.optJSONArray("treatment_text").toString());
                Store.setString(context, Store.INTV_TREATMENT_IMAGE, intv.optJSONArray("treatment_image").toString());
                Store.setString(context, Store.INTV_WHEN, intv.optString("when"));
                Store.setString(context, Store.INTV_TYPE, intv.optString("intv_type"));
                Store.setString(context, Store.INTV_NOTIF, intv.optString("notif"));
                Store.setInt(context, Store.INTV_USER_WINDOW_HOURS, intv.optInt("user_window_hours"));
                Store.setInt(context, Store.INTV_FREE_HOURS_BEFORE_SLEEP, intv.optInt("free_hours_before_sleep"));
                Store.setBoolean(context, Store.INTV_DAILY_NOTIF_DISABLED, intv.optBoolean("intv_daily_notif_disabled"));

                if (!intv.optBoolean("intv_daily_notif_disabled")) {
                    JSONObject currentReminders = SettingsFragment.getCurrentReminders(context);
                    DailyReminder dr = new DailyReminder(context);
                    dr.setReminderBeforeBedTime(currentReminders.optLong(context.getString(R.string.bedtime_reminder)), true);
                    dr.setTodayReminder(currentReminders.optLong(context.getString(R.string.daily_reminder)), true);
//                    AlarmHelper.showInstantNotif(context, DateHelper.getTimestamp() + " intv set",
//                            currentReminders.toString(), "", 4003); // FIXME: 5/31/17 remove debug code
                }

                break;
            }

        }

        if (i == interventions.length()) {
            AlarmHelper.showInstantNotif(context, "No intervention matched.", "Checked: " + DateHelper.getTimestamp(), "", 3993);
        }

        if (Store.getString(context, "iTreatmentImage").equals("")) {
            Log.d(TAG, "prepareTodayIntervention: BeehiveTreatment: No treatment image for today");
        }

        if (Store.getString(context, "iTreatmentText").equals("")) {
            Log.d(TAG, "prepareTodayIntervention: BeehiveTreatment: No treatment text for today");
        }
    }

    private static boolean isTodayInterventionType(Context context, JSONObject intv) {
        return getTodayIntvType(context).equals(intv.optString("intv_type"));
    }

    private static String getTodayIntvType(Context context) {
        String intvType = "";
        if (Store.getBoolean(context, Store.IS_DASHBOARD_TEXT_ENABLED) || Store.getBoolean(context, Store.IS_DASHBOARD_IMAGE_ENABLED)) {
            intvType = "text_image";
        } else if (Store.getBoolean(context, Store.IS_RESCUETIME_ENABLED)) {
            intvType = "rescuetime";
        } else if (Store.getBoolean(context, Store.IS_CALENDAR_ENABLED)) {
            intvType = "calendar";
        }
        return intvType;
    }

    private static JSONArray getAllInterventions(Context context) {
        return JsonHelper.strToJsonArray(Store.getString(context, "interventions"));
    }

    private static Boolean isForToday(JSONObject jo) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        Date startDate = DateHelper.getDatetimeGMT(jo.optString("start"), dateFormat);
        Date endDate = DateHelper.getDatetimeGMT(jo.optString("end"), dateFormat);
        long rightNow = java.util.Calendar.getInstance().getTimeInMillis();
        return rightNow >= startDate.getTime() && rightNow <= endDate.getTime();
    }

    static JSONObject getNotifDetails(Context context) {
        return JsonHelper.strToJsonObject(Store.getString(context, INTV_NOTIF));
    }

    public static JSONObject getTodayIntervention(Context context) {
        String treatmentText;
        String treatmentImage;

        JSONArray treatmentImageArr = JsonHelper.strToJsonArray(Store.getString(context, INTV_TREATMENT_IMAGE));
        JSONArray treatmentTextArr = JsonHelper.strToJsonArray(Store.getString(context, INTV_TREATMENT_TEXT));
        if (treatmentTextArr.length() == 1 && treatmentImageArr.length() == 1) {
            treatmentText = treatmentTextArr.optString(0);
            treatmentImage = treatmentImageArr.optString(0);
        } else {
            int userCondition = Experiment.getUserCondition(context);
            treatmentText = treatmentTextArr.optString(userCondition);
            treatmentImage = treatmentImageArr.optString(userCondition);
        }

        JSONObject todayIntervention = new JSONObject();
        JsonHelper.setJSONValue(todayIntervention, "start", Store.getString(context, INTV_START));
        JsonHelper.setJSONValue(todayIntervention, "end", Store.getString(context, INTV_END));
        JsonHelper.setJSONValue(todayIntervention, "every", Store.getString(context, INTV_EVERY));
        JsonHelper.setJSONValue(todayIntervention, "when", Store.getString(context, INTV_WHEN));
        JsonHelper.setJSONValue(todayIntervention, "repeat", Store.getString(context, INTV_REPEAT));
        JsonHelper.setJSONValue(todayIntervention, "treatment_text", treatmentText);
        JsonHelper.setJSONValue(todayIntervention, "treatment_image", treatmentImage);
        return todayIntervention;
    }

}
