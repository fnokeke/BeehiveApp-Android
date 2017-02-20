package io.smalldata.beehiveapp.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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

    public static Date getDatetime(String datetimeStr, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Constants.locale);
        Date result = new Date();
        try {
            result = dateFormat.parse(datetimeStr);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return result;
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

    public static void downloadImage(Context context, String image_url) {
        Log.i("BeehiveDownloadFile: ", image_url);
//        File direct = new File(Environment.getExternalStorageDirectory()
//                + "/Beehive");
//
//        if (!direct.exists()) {
//            direct.mkdirs();
//        }

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(image_url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/BeehiveFiles", "beehiveImage.jpg");

        mgr.enqueue(request);

    }

    public static void downImage(Context context, String image_url) {
        try {
            URL url = new URL(image_url);
            URLConnection connection = url.openConnection();
            InputStream input = connection.getInputStream();
            FileOutputStream output = context.openFileOutput("beehive.jpg", Context.MODE_PRIVATE);
            byte[] data = new byte[1024];

            output.write(data);
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(Context context, String image_url) {
        Log.i("BeehiveDownloadFile: ", image_url);
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Beehive");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(image_url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/BeehiveFiles", "beehiveImage.jpg");

        mgr.enqueue(request);

    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Constants.locale).format(Calendar.getInstance().getTime());
    }



}


