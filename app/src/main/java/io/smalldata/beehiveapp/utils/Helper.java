package io.smalldata.beehiveapp.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.smalldata.beehiveapp.main.MainActivity;
import io.smalldata.beehiveapp.main.NotificationPublisher;
import io.smalldata.beehiveapp.main.RefreshService;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Helper.java
 * Created: 1/24/17
 * author: Fabian Okeke
 */

public class Helper {

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

    public static String getTodaysDateStr() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }


    public static Date getDatetimeGMT(String datetimeStr) {
        String format = "yyyy-MM-dd'T'HH:mm:ss-05:00";
        return getDatetimeGMT(datetimeStr, format);
    }

    public static Date getDatetimeGMT(String datetimeStr, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Constants.LOCALE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date result = new Date();
        try {
            result = dateFormat.parse(datetimeStr);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return result;
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Constants.LOCALE).format(Calendar.getInstance().getTime());
    }

    public static long getTimestampInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getTimestamp(Calendar cal) {
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Constants.LOCALE).format(cal.getTime());
    }

    public static String getTimestamp(long timeInMillis) {
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Constants.LOCALE).format(timeInMillis);
    }

    public static void scheduleSingleAlarm(Context context, String title, String content, String appIdToLaunch, long alarmTime) {

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        Notification notification = createNotification(context, title, content, appIdToLaunch);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    private static Notification createNotification(Context context, String title, String content, String appIdToLaunch) {
        Intent appLauncherIntent = new Intent(context, MainActivity.class);
        appLauncherIntent.putExtra("appId", appIdToLaunch);
        appLauncherIntent.putExtra("was_dismissed", false);

        Intent deleteIntent = new Intent(context, MainActivity.class);
        deleteIntent.putExtra("was_dismissed", true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, appLauncherIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deletePendingIntent = PendingIntent.getActivity(context, 99, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(title)
                .setContentText(content)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setSound(getDefaultSound())
                .setDeleteIntent(deletePendingIntent)
//                .addAction(android.R.drawable.ic_input_add, "Ok, do now.", contentIntent) // #0
//                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Remove!", dismissIntent) // #2
                .setSmallIcon(android.R.drawable.ic_popup_reminder);

        return builder.build();
    }

    private static Uri getDefaultSound() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public static void showInstantNotif(Context context, String title, String message, String appIdToLaunch, Integer NOTIF_ID) {
        Intent launchAppIntent = IntentLauncher.getLaunchIntent(context, appIdToLaunch);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSound(getDefaultSound())
                .setContentText(message);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }

//    public static void scheduleRepeatingAlarm(Context context, String title, String content, String appIdToLaunch, long alarmTime) {
//        Notification notification = createNotification(context, title, content, appIdToLaunch);
//        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
//        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
//        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC, alarmTime, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
//    }

//    public static void downloadImage(Context context, String image_url) {
//        Log.i("BeehiveDownloadFile: ", image_url);
////        File direct = new File(Environment.getExternalStorageDirectory()
////                + "/Beehive");
////
////        if (!direct.exists()) {
////            direct.mkdirs();
////        }
//
//        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//
//        Uri downloadUri = Uri.parse(image_url);
//        DownloadManager.Request request = new DownloadManager.Request(
//                downloadUri);
//
//        request.setAllowedNetworkTypes(
//                DownloadManager.Request.NETWORK_WIFI
//                        | DownloadManager.Request.NETWORK_MOBILE)
//                .setAllowedOverRoaming(false).setTitle("Demo")
//                .setDescription("Something useful. No, really.")
//                .setDestinationInExternalPublicDir("/BeehiveFiles", "beehiveImage.jpg");
//
//        mgr.enqueue(request);
//
//    }

//    public static void downImage(Context context, String image_url) {
//        try {
//            URL url = new URL(image_url);
//            URLConnection connection = url.openConnection();
//            InputStream input = connection.getInputStream();
//            FileOutputStream output = context.openFileOutput("beehive.jpg", Context.MODE_PRIVATE);
//            byte[] data = new byte[1024];
//
//            output.write(data);
//            output.flush();
//            output.close();
//            input.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void downloadFile(Context context, String image_url) {
//        Log.i("BeehiveDownloadFile: ", image_url);
//        File direct = new File(Environment.getExternalStorageDirectory()
//                + "/Beehive");
//
//        if (!direct.exists()) {
//            direct.mkdirs();
//        }
//
//        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//
//        Uri downloadUri = Uri.parse(image_url);
//        DownloadManager.Request request = new DownloadManager.Request(
//                downloadUri);
//
//        request.setAllowedNetworkTypes(
//                DownloadManager.Request.NETWORK_WIFI
//                        | DownloadManager.Request.NETWORK_MOBILE)
//                .setAllowedOverRoaming(false).setTitle("Demo")
//                .setDescription("Something useful. No, really.")
//                .setDestinationInExternalPublicDir("/BeehiveFiles", "beehiveImage.jpg");
//
//        mgr.enqueue(request);
//
//    }

//    public static void downloadImage(Context context, String image_url) {
//        Log.i("BeehiveDownloadFile: ", image_url);
////        File direct = new File(Environment.getExternalStorageDirectory()
////                + "/Beehive");
////
////        if (!direct.exists()) {
////            direct.mkdirs();
////        }
//
//        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//
//        Uri downloadUri = Uri.parse(image_url);
//        DownloadManager.Request request = new DownloadManager.Request(
//                downloadUri);
//
//        request.setAllowedNetworkTypes(
//                DownloadManager.Request.NETWORK_WIFI
//                        | DownloadManager.Request.NETWORK_MOBILE)
//                .setAllowedOverRoaming(false).setTitle("Demo")
//                .setDescription("Something useful. No, really.")
//                .setDestinationInExternalPublicDir("/BeehiveFiles", "beehiveImage.jpg");
//
//        mgr.enqueue(request);
//
//    }

//    public static void downImage(Context context, String image_url) {
//        try {
//            URL url = new URL(image_url);
//            URLConnection connection = url.openConnection();
//            InputStream input = connection.getInputStream();
//            FileOutputStream output = context.openFileOutput("beehive.jpg", Context.MODE_PRIVATE);
//            byte[] data = new byte[1024];
//
//            output.write(data);
//            output.flush();
//            output.close();
//            input.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void downloadFile(Context context, String image_url) {
//        Log.i("BeehiveDownloadFile: ", image_url);
//        File direct = new File(Environment.getExternalStorageDirectory()
//                + "/Beehive");
//
//        if (!direct.exists()) {
//            direct.mkdirs();
//        }
//
//        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//
//        Uri downloadUri = Uri.parse(image_url);
//        DownloadManager.Request request = new DownloadManager.Request(
//                downloadUri);
//
//        request.setAllowedNetworkTypes(
//                DownloadManager.Request.NETWORK_WIFI
//                        | DownloadManager.Request.NETWORK_MOBILE)
//                .setAllowedOverRoaming(false).setTitle("Demo")
//                .setDescription("Something useful. No, really.")
//                .setDestinationInExternalPublicDir("/BeehiveFiles", "beehiveImage.jpg");
//
//        mgr.enqueue(request);
//
//    }

}


