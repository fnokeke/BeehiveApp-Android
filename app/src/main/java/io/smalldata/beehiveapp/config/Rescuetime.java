package io.smalldata.beehiveapp.config;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.IntentLauncher;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Store;

import static io.smalldata.beehiveapp.utils.Store.getString;

/**
 * Rescuetime configurations set on Beehive Platform will be handled here.
 * Created by fnokeke on 2/20/17.
 */

public class Rescuetime extends BaseConfig {
    private Context mContext;
    private boolean firstTime = true;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private final static int RESCUTIME_NOTIFICATION_ID = 8899;
    private final static String STATS_RT = "statsRT";

    private final static String PRODUCTIVE_DURATION = "productive_duration";
    private final static String DISTRACTED_DURATION = "distracted_duration";
    private final static String PRODUCTIVE_MSG = "productive_msg";
    private final static String DISTRACTED_MSG = "distracted_msg";
    private final static String SHOW_STATS = "show_stats";

    private static Locale locale = Constants.LOCALE;


    public Rescuetime(Context context) {
        mContext = context;
        mBuilder = new NotificationCompat.Builder(mContext);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void saveSettings(JSONArray rescuetimeConfig) {
        if (rescuetimeConfig == null || rescuetimeConfig.length() == 0) return;

        JSONObject lastItem = rescuetimeConfig.optJSONObject(rescuetimeConfig.length() - 1);
        Store.setString(mContext, PRODUCTIVE_DURATION, lastItem.optString(PRODUCTIVE_DURATION));
        Store.setString(mContext, DISTRACTED_DURATION, lastItem.optString(DISTRACTED_DURATION));
        Store.setString(mContext, PRODUCTIVE_MSG, lastItem.optString(PRODUCTIVE_MSG));
        Store.setString(mContext, DISTRACTED_MSG, lastItem.optString(DISTRACTED_MSG));
        Store.setString(mContext, SHOW_STATS, lastItem.optString(SHOW_STATS));
    }

    public void refreshAndStoreStats() {
        String email = Experiment.getUserInfo(mContext).optString("email");
        if (email.equals("")) {
            return;
        }

        JSONObject params = new JSONObject();
        JsonHelper.setJSONValue(params, "email", email);
        JsonHelper.setJSONValue(params, "date", Helper.getTodayDateStr());
        CallAPI.getRTRealtimeActivity(mContext, params, getRTResponseHandler);
    }

    private VolleyJsonCallback getRTResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.w("RT onSuccess ", result.toString());
            JSONArray activities = result.optJSONArray("rows");
            JSONObject currStats = computeProductivity(activities);

            String fT = currStats.optString("focusedTotal");
            String fP = currStats.optString("focusedPercent");
            String fA = currStats.optString("focusedActivity");

            String dT = currStats.optString("distrTotal");
            String dP = currStats.optString("distrPercent");
            String dA = currStats.optString("distrActivity");

            String nT = currStats.optString("neutralTotal");
            String nP = currStats.optString("neutralPercent");
            String nA = currStats.optString("neutralActivity");

            String timeStamp = Helper.getTimestamp();

            String statsRT = String.format(locale, "Last Updated Rescuetime \n(" + timeStamp + "): " +
                    "\n\nFocused: %s hrs (%s%%)\n%s", fT, fP, fA) +
                    String.format(locale, "\n\nDistracted: %s hrs (%s%%)\n%s", dT, dP, dA) +
                    String.format(locale, "\n\nNeutral: %s hrs (%s%%)\n%s", nT, nP, nA);
            Store.setString(mContext, STATS_RT, statsRT);

            String statusTitleRT = String.format("Last updated at %s", timeStamp);
            Store.setString(mContext, "statusTitleRT", statusTitleRT);

            String statusContentRT = String.format(locale,
                    "Focused: %shrs (%s%%)", fT, fP) +
                    String.format(locale, "; Distracted: %shrs (%s%%)", dT, dP);
            Store.setString(mContext, "statusContentRT", statusContentRT);

            updateNotification(statusTitleRT, statusContentRT);
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.w("RT error:", error.toString());
            handleVolleyError(error);
        }
    };

    private void updateNotification(String title, String message) {
        Intent launchAppIntent = IntentLauncher.getLaunchIntent(mContext, mContext.getPackageName());
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, launchAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (firstTime) {
            mBuilder.setSmallIcon(android.R.drawable.ic_menu_recent_history)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setOngoing(true);
            firstTime = false;
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(RESCUTIME_NOTIFICATION_ID, mBuilder.build());
    }

    private void handleVolleyError(VolleyError error) {
        error.printStackTrace();
        if (error instanceof TimeoutError) {
            Log.d("**TimeoutError**", "Check that your server is up and running");
            Store.setString(mContext, "errorRT", "Error fetching Rescuetime data. No network connection detected.");
        } else if (error instanceof ServerError) {
            Log.d("**ServerError**", "Typically happens when your API url endpoint is incorrect.");
            Store.setString(mContext, "errorRT", "Experiment is unavailable. Please contact researcher.");
        } else {
            String msg = "Something went wrong while fetching your info. Please contact your researcher. \n\nError details: " + error.toString();
            Store.setString(mContext, "errorRT", msg);
        }
    }

    private JSONObject computeProductivity(JSONArray activities) {
        JSONArray row;
        String activity;
        Double productivity, timeSpent, focusedTotal, distrTotal, neutralTotal;

        focusedTotal = 0.0;
        distrTotal = 0.0;
        neutralTotal = 0.0;

        JSONObject focusedApps = new JSONObject();
        JSONObject distrApps = new JSONObject();
        JSONObject neutralApps = new JSONObject();

        for (int i = 0; i < activities.length(); i++) {
            row = activities.optJSONArray(i);

            timeSpent = row.optDouble(1);
            activity = row.optString(3);
            productivity = row.optDouble(5);

            try {
                if (productivity < 0) {
                    distrTotal += timeSpent;
                    distrApps.put(activity, timeSpent + distrApps.optDouble(activity, 0));
                } else if (productivity == 0) {
                    neutralTotal += timeSpent;
                    neutralApps.put(activity, timeSpent + neutralApps.optDouble(activity, 0));
                } else if (productivity > 0) {
                    focusedTotal += timeSpent;
                    focusedApps.put(activity, timeSpent + focusedApps.optDouble(activity, 0));
                }

            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        Double[] timeArr = new Double[]{focusedTotal, distrTotal, neutralTotal};
        JSONObject[] activityArr = new JSONObject[]{focusedApps, distrApps, neutralApps};
        return toJSONResults(timeArr, activityArr);
    }

    private JSONObject toJSONResults(Double[] timeArr, JSONObject[] activityArr) {
        Double focusedTotal = timeArr[0];
        Double distrTotal = timeArr[1];
        Double neutralTotal = timeArr[2];

        focusedTotal /= 3600;
        distrTotal /= 3600;
        neutralTotal /= 3600;

        Double totalTime = focusedTotal + distrTotal + neutralTotal + 0.001; // add 0.001 to prevent 0/0 when total == 0
        String focusedPercent = String.format(locale, "%.1f", 100.0 * focusedTotal / totalTime);
        String distrPercent = String.format(locale, "%.1f", 100.0 * distrTotal / totalTime);
        String neutralPercent = String.format(locale, "%.1f", 100.0 * neutralTotal / totalTime);

        String focusedActivity = activityArr[0].toString();
        String distrActivity = activityArr[1].toString();
        String neutralActivity = activityArr[2].toString();

        JSONObject results = new JSONObject();
        JsonHelper.setJSONValue(results, "focusedActivity", focusedActivity);
        JsonHelper.setJSONValue(results, "focusedTotal", String.format(locale, "%.1f", focusedTotal));
        JsonHelper.setJSONValue(results, "focusedPercent", focusedPercent);

        JsonHelper.setJSONValue(results, "distrActivity", distrActivity);
        JsonHelper.setJSONValue(results, "distrTotal", String.format(locale, "%.1f", distrTotal));
        JsonHelper.setJSONValue(results, "distrPercent", distrPercent);

        JsonHelper.setJSONValue(results, "neutralActivity", neutralActivity);
        JsonHelper.setJSONValue(results, "neutralTotal", String.format(locale, "%.1f", neutralTotal));
        JsonHelper.setJSONValue(results, "neutralPercent", neutralPercent);

        return results;
    }

    public String getStoredStats() {
        return getString(mContext, STATS_RT);
    }
}
