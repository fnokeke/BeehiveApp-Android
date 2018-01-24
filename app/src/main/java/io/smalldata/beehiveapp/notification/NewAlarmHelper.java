package io.smalldata.beehiveapp.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;

/**
 * Created by fnokeke on 1/3/18.
 * Support functions for scheduling alarm
 */

public class NewAlarmHelper {

    public static void scheduleIntvReminder(Context context, JSONObject notif) {

        Notification notification = createNewNotif(context, notif);
        Intent singleIntent = new Intent(context, SingleAlarmReceiver.class);
        singleIntent.putExtra(Constants.NOTIFICATION, notification);

        int alarmId = notif.optInt("alarmId");
        singleIntent.putExtra(Constants.NOTIFICATION_ID, alarmId);
        singleIntent.putExtra("alarmId", alarmId);


        AlarmHelper.showInstantNotif(context,
                "Today Intv Applied",
                "at: " + DateHelper.getFormattedTimestamp(),
                "",
                9063 + alarmId);


        singleIntent.putExtra(Constants.ALARM_NOTIF_TITLE, notif.optString("title"));
        singleIntent.putExtra(Constants.ALARM_NOTIF_CONTENT, notif.optString("content"));
        singleIntent.putExtra(Constants.ALARM_APP_ID, notif.optString("appIdToLaunch"));
        singleIntent.putExtra(Constants.ALARM_MILLIS_SET, notif.optLong("alarmMillis")); // FIXME: 6/5/17 debug code

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, singleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        int alarmId = notif.optInt("alarmId");
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, CONTENT_PREFIX + alarmId, appLauncherIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, DELETE_PREFIX + alarmId, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(notif.optString("title"))
                .setContentText(notif.optString("content"))
                .setAutoCancel(true)
                .setSound(getDefaultSound())
                .setDeleteIntent(deletePendingIntent)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setWhen(notif.optLong("alarmMillis"))
                .setShowWhen(true);

        if (notif.optString(Constants.NOTIF_TYPE).equals("sleep_wake")) {
            builder.setSmallIcon(R.drawable.end_of_day_reminder);
        }

        return builder.build();
    }

    private static Intent createNewOnClickIntent(Context context, JSONObject notif, boolean wasDismissed) {
        Intent intent = new Intent(context, NotifClickORDismissReceiver.class);
        intent.putExtra(Constants.NOTIF_TYPE, notif.optString(Constants.NOTIF_TYPE));
        intent.putExtra(Constants.ALARM_NOTIF_TITLE, notif.optString("title"));
        intent.putExtra(Constants.NOTIF_TYPE, notif.optString("alarmType"));
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
}
