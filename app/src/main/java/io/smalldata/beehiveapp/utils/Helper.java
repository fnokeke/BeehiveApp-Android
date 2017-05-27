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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.MainActivity;
import io.smalldata.beehiveapp.main.NotificationPublisher;

import static android.R.attr.max;

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
//        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Constants.LOCALE).format(Calendar.getInstance().getTime());
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Constants.LOCALE).format(System.currentTimeMillis());
    }

    public static long getTimestampInMillis() {
//        return Calendar.getInstance().getTimeInMillis();
        return System.currentTimeMillis();
    }

    public static String millisToDateFormat(Calendar cal) {
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Constants.LOCALE).format(cal.getTime());
    }

    public static String millisToDateFormat(long timeInMillis) {
        if (timeInMillis <= 0) return "Zero.am";
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Constants.LOCALE).format(timeInMillis);
    }

    public static void scheduleSingleAlarm(Context context, int alarmId, String title, String content, String appIdToLaunch, long alarmTime) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        Notification notification = createNotification(context, title, content, appIdToLaunch);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    private static Notification createNotification(Context context, String title, String content, String appIdToLaunch) {
        Intent appLauncherIntent = new Intent(context, MainActivity.class);
        appLauncherIntent.putExtra("appId", appIdToLaunch);
        appLauncherIntent.putExtra("was_dismissed", false);

        Intent deleteIntent = new Intent();
        deleteIntent.putExtra("was_dismissed", true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, appLauncherIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deletePendingIntent = PendingIntent.getActivity(context, 99, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(getDefaultSound())
                .setDeleteIntent(deletePendingIntent)
                .setShowWhen(true)
//                .addAction(android.R.drawable.ic_input_add, "Ok, do now.", contentIntent) // #0
//                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Remove!", dismissIntent) // #2
                .setSmallIcon(android.R.drawable.ic_popup_reminder);

        return builder.build();
    }

    private static Uri getDefaultSound() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public static void showInstantNotif(Context context, String title, String message, String appIdToLaunch, Integer NOTIF_ID) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.info_tip)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSound(getDefaultSound())
                .setContentText(message);

        if (!appIdToLaunch.equals("")) {
            Intent launchAppIntent = IntentLauncher.getLaunchIntent(context, appIdToLaunch);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(contentIntent);
        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }

    public static String dateToStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Constants.LOCALE);
        return sdf.format(date);
    }

    public static Date strToDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Constants.LOCALE);
        Date formattedDate = new Date();
        try {
            formattedDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public boolean isEqualOrGreater(String mainDateStr, String compareDateStr) {
        Date mainDate = strToDate(mainDateStr);
        Date compareDate = strToDate(compareDateStr);
        return mainDate.getTime() >= compareDate.getTime();
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

    public static int getRandomInt(int min, int max) {
        Random random = new Random();
        int range = max - min + 1;
        return random.nextInt(range) + min;
    }

    public static JSONObject strToJsonObject(String jsonStr) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONArray strToJsonArray(String jsonStr) {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}


