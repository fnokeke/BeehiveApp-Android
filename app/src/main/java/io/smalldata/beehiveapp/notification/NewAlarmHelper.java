package io.smalldata.beehiveapp.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import org.json.JSONObject;

import java.util.Calendar;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.onboarding.Constants;

/**
 * Created by fnokeke on 1/3/18.
 * Support functions for scheduling alarm
 */

public class NewAlarmHelper {

    public static void scheduleIntvReminder(Context context, JSONObject notif) {
        Notification notification = createNewNotif(context, notif);
        Intent singleIntent = new Intent(context, ProtocolAlarmReceiver.class);
        singleIntent.putExtra(Constants.NOTIFICATION, notification);

        int notifId = Integer.parseInt(notif.optString("notifId"));
        singleIntent.putExtra(Constants.NOTIFICATION_ID, notifId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifId, singleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            throw new UnsupportedOperationException("alarmManager should not be null");
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, notif.optLong("alarmMillis"), pendingIntent);
    }

    private static Notification createNewNotif(Context context, JSONObject notif) {
        Intent appLauncherIntent = createNewOnClickIntent(context, notif, false);
        Intent deleteIntent = createNewOnClickIntent(context, notif, true);

        final int CONTENT_PREFIX = 16;// make requestCode unique
        final int DELETE_PREFIX = 32; // make requestCode unique
        int notifId = Integer.parseInt(notif.optString("notifId"));
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, CONTENT_PREFIX + notifId, appLauncherIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, DELETE_PREFIX + notifId, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(notif.optString("title"))
                .setContentText(notif.optString("content"))
                .setAutoCancel(true)
                .setSound(getDefaultSound())
                .setDeleteIntent(deletePendingIntent)
                .setWhen(notif.optLong("alarmMillis"))
                .setShowWhen(true);

        switch (notif.optString("method")) {
            case Constants.TYPE_PAM:
                builder.setSmallIcon(R.drawable.end_of_day_reminder);
                break;

            case Constants.TYPE_PUSH_SURVEY:
                builder.setSmallIcon(android.R.drawable.ic_dialog_email);
                break;

            default:
                builder.setSmallIcon(android.R.drawable.ic_popup_reminder);
        }

        return builder.build();
    }

    private static Intent createNewOnClickIntent(Context context, JSONObject notif, boolean wasDismissed) {
        Intent intent = new Intent(context, NotifEventReceiver.class);
        intent.putExtra(Constants.NOTIF_TYPE, notif.optString(Constants.NOTIF_TYPE));
        intent.putExtra(Constants.ALARM_PROTOCOL_METHOD, notif.optString("method"));
        intent.putExtra(Constants.ALARM_NOTIF_TITLE, notif.optString("title"));
        intent.putExtra(Constants.ALARM_NOTIF_CONTENT, notif.optString("content"));
        intent.putExtra(Constants.ALARM_APP_ID, notif.optString("appIdToLaunch"));
        intent.putExtra(Constants.ALARM_MILLIS_SET, notif.optLong("alarmMillis"));
        intent.putExtra(Constants.ALARM_NOTIF_WAS_DISMISSED, wasDismissed);
        return intent;
    }

    private static Uri getDefaultSound() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public static boolean todayIsWeekend() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    public static void notifyUpdateApp(Context context, String title, String message, String url) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.beehive)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSound(getDefaultSound())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent urlPendingIntent = PendingIntent.getActivity(context, 33, urlIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(urlPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(9876, mBuilder.build());
    }
}
