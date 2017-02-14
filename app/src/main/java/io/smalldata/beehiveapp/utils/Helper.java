package io.smalldata.beehiveapp.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.smalldata.beehiveapp.R;

/**
 * Helper.java
 * Created: 1/24/17
 * author: Fabian Okeke
 */

public class Helper {

    private static final String PREF_NAME = "beehivePrefs";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getStoreString(Context context, String key) {
        return getPrefs(context).getString(key, "");
    }

    public static void copy(JSONObject from, JSONObject to) {

        for (int i = 0; i < from.names().length(); i++) {
            String key = from.names().optString(i);
            Object value = from.opt(key);
            setJSONValue(to, key, value);
        }

    }

    public static void setJSONValue(JSONObject jsonObject, String key, Object value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public static String getTodayDateStr() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static void promptIfNetworkError(Context context, Boolean isTimeoutError) {
        if (isTimeoutError) {
            ((Activity) context).findViewById(R.id.tv_timeout_prompt).setVisibility(View.VISIBLE);
        } else {
            ((Activity) context).findViewById(R.id.tv_timeout_prompt).setVisibility(View.INVISIBLE);
        }
    }


    public static void promptIfDisconnected(Context context) {
        if (!Network.isDeviceOnline(context)) {
            ((Activity) context).findViewById(R.id.tv_offline_prompt).setVisibility(View.VISIBLE);
        } else {
            ((Activity) context).findViewById(R.id.tv_offline_prompt).setVisibility(View.INVISIBLE);
        }
    }


    public static Date getDatetime(String datetimeStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss-05:00", Locale.US);
        Date result = new Date();
        try {
            result = dateFormat.parse(datetimeStr);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return result;
    }


    public static String prettyHours(HashMap freeHoursOfDay) {
        String results = "";
        for (Object value : freeHoursOfDay.values()) {
            results += value.toString() + "\n";
        }
        return results;
    }

    public static void removeBusyTime(HashMap freeHrs, Date startDT, Date endDT) {
        int startHr = getHours(startDT);
        int endHr = getHours(endDT);
        if (getMinutes(endDT) > 0) {
            endHr += 1;
        }

        for (int i = startHr; i < endHr; i++) {
            freeHrs.remove(i);
        }
    }

    public static int getHours(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinutes(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }


    public static void showInstantNotif(Context context, String title, String message) {

        Intent launchAppIntent = IntentLauncher.getLaunchIntent(context, "org.md2k.moodsurfing");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.GENERAL_NOTIF_ID, mBuilder.build());
    }

}


