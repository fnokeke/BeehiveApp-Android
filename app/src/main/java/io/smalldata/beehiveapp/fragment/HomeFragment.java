package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.smalldata.beehiveapp.MainActivity;
import io.smalldata.beehiveapp.NotificationPublisher;
import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Created by fnokeke on 1/20/17.
 */

public class HomeFragment extends Fragment {

    AlarmManager alarmManager;
    PendingIntent mPendingIntent;
    BroadcastReceiver mReceiver;

    TextView todayTV;
    Activity gContext;
    Locale locale = Locale.getDefault();

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private boolean firstTime = true;
    private final static int RESCUTIME_NOTIFICATION_ID = 8899;
    private Context mContext;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        todayTV = (TextView) getActivity().findViewById(R.id.tv_today_msg);
        mBuilder = new NotificationCompat.Builder (mContext);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

//        unRegisterRTAlarmBroadcast();
        registerRTAlarmBroadcast();

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(Calendar.getInstance().getTime());

        updateNotification("Stats will auto-updtate throughout the day.", "Current time: " + timeStamp);

        fetchRTActivity();

//        showCalEvents();
//        scheduleNotification(mContext,
//                getNotification(mContext, "Beehive Mobile message for you.", "Spending 15 seconds each for 3 deep breaths."),
//                5000);
    }

    private void fetchRTActivity() {
        JSONObject params = new JSONObject();
        Helper.setJSONValue(params, "email", Store.getInstance(mContext).getString("email"));
        Helper.setJSONValue(params, "date", Helper.getTodayDateFmt());
        CallAPI.getRTRealtimeActivity(mContext, params, getRTResponseHandler);
    }

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

            String rtStr = String.format(locale, "UPDATED RESCUETIME STATS: " +
                    "\n\nFocused: %s hrs (%s%%)\n%s", fT, fP, fA) +
                    String.format(locale, "\n\nDistracted: %s hrs (%s%%)\n%s", dT, dP, dA) +
                    String.format(locale, "\n\nNeutral: %s hrs (%s%%)\n%s", nT, nP, nA);
            Display.showSuccess(todayTV, rtStr);

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(Calendar.getInstance().getTime());
            String rtTitle = String.format("Rescuetime ongoing stats (%s)", timeStamp);

            String rtContent = String.format(locale,
                    "Focused: %s hrs (%s%%)", fT, fP) +
                    String.format(locale, " / Distracted: %s hrs (%s%%)", dT, dP);
            updateNotification(rtTitle, rtContent);
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.w("RT onSuccess error:", error.toString());
            handleVolleyError(error);
        }
    };

    private void updateNotification(String title, String message) {

        if (firstTime) {
            mBuilder.setSmallIcon(android.R.drawable.ic_menu_recent_history)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setOngoing(true);
            firstTime = false;
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mNotificationManager.notify(RESCUTIME_NOTIFICATION_ID, mBuilder.build());
    }

    private JSONObject computeProductivity(JSONArray activities) {
        JSONArray row;
        String activity, focusedActivity, distrActivity, neutralActivity;
        Double productivity, timeSpent, focusedTotal, distrTotal, neutralTotal;

        focusedActivity = "";
        distrActivity = "";
        neutralActivity = "";

        focusedTotal = 0.0;
        distrTotal = 0.0;
        neutralTotal = 0.0;

        for (int i = 0; i < activities.length(); i++) {
            row = activities.optJSONArray(i);

            timeSpent = row.optDouble(1);
            activity = row.optString(3);
            productivity = row.optDouble(5);

            if (productivity < 0) {
                distrActivity += activity + "\n ";
                distrTotal += timeSpent;
            } else if (productivity == 0) {
                neutralActivity += activity + "\n ";
                neutralTotal += timeSpent;
            } else if (productivity > 0) {
                focusedActivity += activity + "\n ";
                focusedTotal += timeSpent;
            }

        }

        focusedTotal /= 3600;
        distrTotal /= 3600;
        neutralTotal /= 3600;

        Double totalTime = focusedTotal + distrTotal + neutralTotal;
        String focusedPercent = String.format(locale, "%.1f", 100.0 * focusedTotal / totalTime);
        String distrPercent = String.format(locale, "%.1f", 100.0 * distrTotal / totalTime);
        String neutralPercent = String.format(locale, "%.1f", 100.0 * neutralTotal / totalTime);

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

    private void showCalEvents() {
        JSONObject params = new JSONObject();
        Helper.setJSONValue(params, "email", Store.getInstance(getActivity()).getString("email"));
        Helper.setJSONValue(params, "date", Helper.getTodayDateFmt());
        CallAPI.getAllCalEvents(getActivity(), params, showCalResponseHandler);
    }


    VolleyJsonCallback showCalResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            String resultStr = result.toString();

            Log.d("onConnectSuccess: ", resultStr);

            if (result.optInt("events") == -1) {
                Display.showError(todayTV, "Could not fetch calendar info.");
                return;
            }

            JSONArray mJsonArray = result.optJSONArray("events");

            if (mJsonArray != null) {
                String mJsonStr = getPrettyEvents(mJsonArray);
                String mFreeStr = getFreeTime(mJsonArray);
                long busyTimeMs = computeBusyTimeMs(mJsonArray);
                Float busyHours = (float) busyTimeMs / 3600000;
                Integer noOfTodayEvents = countTodayEvents(mJsonArray);

                Display.showSuccess(todayTV,
                        "No of today events: " + noOfTodayEvents.toString() + "\n\n" +
                                "Total Busy Hours: " + String.format(locale, "%.02f", busyHours) + "\n\n" +
                                "Today Events:\n" + mJsonStr +
                                "Potential Notification Time:\n" + mFreeStr
                );
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
            Display.showError(todayTV, R.string.no_network_prompt);
        } else if (error instanceof ServerError) {
            Log.d("**ServerError**", "Typically happens when you have HttpAccessTokenRefreshError or your API url is incorrect.");
            Display.showError(todayTV, R.string.timeout_error_prompt);
        } else {
            String msg = "Something went wrong while fetching your info. Please contact your researcher. \n\nError details: " + error.toString();
            Display.showError(todayTV, msg);
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


    private void scheduleNotification(Context cxt, Notification notification, int delay) {

        Intent notificationIntent = new Intent(cxt, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(cxt, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(Context cxt, String title, String content) {
        Notification.Builder builder = new Notification.Builder(cxt);


        Intent resultIntent = new Intent(cxt, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(cxt);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        builder.setContentTitle(title)
                .setContentText(content)
                .setShowWhen(true)
                .addAction(android.R.drawable.ic_input_add, "Ok, do now.", resultPendingIntent) // #0
                .addAction(android.R.drawable.ic_input_delete, "Do later.", resultPendingIntent)  // #1
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Remove!", resultPendingIntent) // #2
                .setSmallIcon(android.R.drawable.ic_menu_recent_history);

        return builder.build();
    }

    private void registerRTAlarmBroadcast() {
        Log.w("******AlarmRegistere***", "Alarm registerd.");
        mReceiver = new BroadcastReceiver() {
            // private static final String TAG = "Alarm Example Receiver";
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.w("******Alarm Time*******", "Alarm time has reached indeed.");
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(Calendar.getInstance().getTime());
//                Toast.makeText(context, "Alarm look at me now.", Toast.LENGTH_LONG).show();
//                updateNotification("look at me now: ", timeStamp);
                fetchRTActivity();

            }
        };

        mContext.registerReceiver(mReceiver, new IntentFilter("sample"));
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("sample"), 0);
        alarmManager = (AlarmManager)(mContext.getSystemService(Context.ALARM_SERVICE));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 15 * 60000, mPendingIntent);
    }

    private void unRegisterRTAlarmBroadcast() {
        alarmManager.cancel(mPendingIntent);
        mContext.unregisterReceiver(mReceiver);
    }

//    public static void registerNewAlarm(Context context) {
//        Intent i = new Intent(context, YOURBROADCASTRECIEVER.class);
//
//        PendingIntent sender = PendingIntent.getBroadcast(context, 999, i, 0);
//
//        // We want the alarm to go off 3 seconds from now.
//        long firstTime = SystemClock.elapsedRealtime();
//        firstTime += 3 * 1000;//start 3 seconds after first register.
//
//        // Schedule the alarm!
//        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
//                600000, sender);//10min interval
//
//    }
}

// TODO: 1/25/17 move functions to Helper
// TODO: 2/1/17 make sure home fragment shows "Awaiting info" for first time use until connection done
