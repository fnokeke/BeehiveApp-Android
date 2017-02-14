package io.smalldata.beehiveapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.IntentLauncher;
import io.smalldata.beehiveapp.utils.Store;

public class RefreshService extends Service {

    private Handler serverHandler = new Handler();
    private static Locale locale = Locale.getDefault();

    private boolean firstTime = true;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;

    private final static int RESCUTIME_NOTIFICATION_ID = 8899;
    private final static long UPDATE_SERVER_INTERVAL_MS = 3000 * 1000;

    private Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        mBuilder = new NotificationCompat.Builder(mContext);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", locale).format(Calendar.getInstance().getTime());
        String rtStatusTitle = timeStamp + " " + Store.getString(mContext, "statusTitleRT");
        String rtStatusContent = Store.getString(mContext, "statusContentRT");
        if (rtStatusContent.equals("")) {
           rtStatusContent = "Expect updates throughout the day.";
        }
        updateNotification(rtStatusTitle, rtStatusContent);

        serverHandler.postDelayed(serverUpdateTask, 0);
        return super.onStartCommand(intent, flags, startId);
    }

    public static void start(Context context) {
        context.startService(new Intent(context, RefreshService.class));
    }

    private Runnable serverUpdateTask = new Runnable() {
        public void run() {
            JSONObject phoneDetails = DeviceInfo.getPhoneDetails(mContext);
            Log.i("BeehivePhoneDetails: ", phoneDetails.toString());

            String email = Store.getString(mContext, "email");
            if (email.equals("")) { return; }

            JSONObject params = new JSONObject();
            Helper.setJSONValue(params, "email", email);
            Helper.setJSONValue(params, "date", Helper.getTodayDateStr());

            CallAPI.getRTRealtimeActivity(mContext, params, getRTResponseHandler);
            CallAPI.getAllCalEvents(mContext, params, showCalResponseHandler);

            serverHandler.postDelayed(this, 30*60*1000);
        }
    };


    VolleyJsonCallback getRTResponseHandler = new VolleyJsonCallback() {
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

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", locale).format(Calendar.getInstance().getTime());

            String statsRT = String.format(locale, "UPDATED RESCUETIME STATS (" + timeStamp + "): " +
                    "\n\nFocused: %s hrs (%s%%)\n%s", fT, fP, fA) +
                    String.format(locale, "\n\nDistracted: %s hrs (%s%%)\n%s", dT, dP, dA) +
                    String.format(locale, "\n\nNeutral: %s hrs (%s%%)\n%s", nT, nP, nA);
            Store.setString(mContext, "statsRT", statsRT);

            String statusTitleRT = String.format("Updated stats (%s)", timeStamp);
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

        Double totalTime = focusedTotal + distrTotal + neutralTotal;
        String focusedPercent = String.format(locale, "%.1f", 100.0 * focusedTotal / totalTime);
        String distrPercent = String.format(locale, "%.1f", 100.0 * distrTotal / totalTime);
        String neutralPercent = String.format(locale, "%.1f", 100.0 * neutralTotal / totalTime);

        String focusedActivity = activityArr[0].toString();
        String distrActivity = activityArr[1].toString();
        String neutralActivity = activityArr[2].toString();

        JSONObject results = new JSONObject();
        Helper.setJSONValue(results, "focusedActivity", focusedActivity);
        Helper.setJSONValue(results, "focusedTotal", String.format(locale, "%.1f", focusedTotal));
        Helper.setJSONValue(results, "focusedPercent", focusedPercent);

        Helper.setJSONValue(results, "distrActivity", distrActivity);
        Helper.setJSONValue(results, "distrTotal", String.format(locale, "%.1f", distrTotal));
        Helper.setJSONValue(results, "distrPercent", distrPercent);

        Helper.setJSONValue(results, "neutralActivity", neutralActivity);
        Helper.setJSONValue(results, "neutralTotal", String.format(locale, "%.1f", neutralTotal));
        Helper.setJSONValue(results, "neutralPercent", neutralPercent);

        return results;
    }

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

    VolleyJsonCallback showCalResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            String resultStr = result.toString();

            Log.d("onConnectSuccess: ", resultStr);

            if (result.optInt("events") == -1) {
                Store.setString(mContext, "errorCal", "There are no available calendar events.");
                return;
            }

            JSONArray mJsonArray = result.optJSONArray("events");

            if (mJsonArray != null) {
                String mJsonStr = getPrettyEvents(mJsonArray);
                String mFreeStr = getFreeTime(mJsonArray);
                long busyTimeMs = computeBusyTimeMs(mJsonArray);
                Float busyHours = (float) busyTimeMs / 3600000;
                Integer noOfTodayEvents = countTodayEvents(mJsonArray);
                String statsCal = "No of today events: " + noOfTodayEvents.toString() + "\n\n" +
                                "Total Busy Hours: " + String.format(locale, "%.02f", busyHours) + "\n\n" +
                                "Today Events:\n" + mJsonStr +
                                "Potential Notification Time:\n" + mFreeStr;
                Store.setString(mContext, "statsCal", statsCal);
            }
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            handleVolleyError(error);
        }
    };

    private Integer countTodayEvents(JSONArray ja) {
        JSONObject jo;
        Integer totalEventCount = 0;

        for (Integer i = 0; i < ja.length(); i++) {
            jo = ja.optJSONObject(i);

            // use only events with specific begin/end time (not just begin/end date)
            if (!(jo.optJSONObject("start").optString("dateTime").equals(""))) {
                totalEventCount += 1;
            }
        }
        return totalEventCount;
    }

    private long computeBusyTimeMs(JSONArray ja) {
        JSONObject jo;
        String start, end;
        Date startDT, endDT;
        long totalMs = 0;

        for (Integer i = 0; i < ja.length(); i++) {
            jo = ja.optJSONObject(i);

            // use only events with specific begin/end time (not just begin/end date)
            if (!(jo.optJSONObject("start").optString("dateTime").equals(""))) {
                start = jo.optJSONObject("start").optString("dateTime");
                startDT = Helper.getDatetime(start);

                end = jo.optJSONObject("end").optString("dateTime");
                endDT = Helper.getDatetime(end);

                totalMs += endDT.getTime() - startDT.getTime();
            }
        }

        return totalMs;
    }

    public void handleVolleyError(VolleyError error) {
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

    private String getPrettyEvents(JSONArray ja) {

        String results = "";
        String summary, start, end;
        JSONObject jo, joTmp;

        for (int i = 0; i < ja.length(); i++) {
            jo = ja.optJSONObject(i);

            summary = jo.optString("summary");

            joTmp = jo.optJSONObject("start");
            start = joTmp.optString("dateTime").equals("") ? joTmp.optString("date") : joTmp.optString("dateTime");

            joTmp = jo.optJSONObject("end");
            end = joTmp.optString("dateTime").equals("") ? joTmp.optString("date") : joTmp.optString("dateTime");

            results += summary + "\n" + start + " to " + end + "\n\n";
        }

        return results;
    }

    private String getFreeTime(JSONArray ja) {

        HashMap freeHoursOfDay = new HashMap();
        for (Integer i = 0; i < 24; i++) {
            freeHoursOfDay.put(i, String.format(locale, "%s:00 hrs", i.toString()));
        }

        JSONObject jo;
        String start, end;
        Date startDT, endDT;

        for (Integer i = 0; i < ja.length(); i++) {
            jo = ja.optJSONObject(i);

            // use only events with specific begin/end time (not just begin/end date)
            if (!(jo.optJSONObject("start").optString("dateTime").equals(""))) {
                start = jo.optJSONObject("start").optString("dateTime");
                startDT = Helper.getDatetime(start);

                end = jo.optJSONObject("end").optString("dateTime");
                endDT = Helper.getDatetime(end);

                Helper.removeBusyTime(freeHoursOfDay, startDT, endDT);
            }
        }

        return Helper.prettyHours(freeHoursOfDay);
    }

}
