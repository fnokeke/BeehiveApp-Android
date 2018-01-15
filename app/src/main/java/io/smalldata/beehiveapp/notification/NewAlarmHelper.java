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

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.NotifClickORDismissReceiver;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;

/**
 * Created by fnokeke on 1/3/18.
 * Support functions for scheduling alarm
 */

public class NewAlarmHelper {

    public static void startDaily4amTask(Context context) {
        AlarmHelper.showInstantNotif(context, "Start 4am Task",
                DateHelper.getFormattedTimestamp(), "", 7711); // FIXME: 1/15/18 remove debug

        final int alarmId = Constants.DAILY_TASK_ALARM_ID;
        Intent intent = new Intent(context, DailyTaskReceiver.class);
        intent.putExtra("alarmId", alarmId);
        intent.putExtra(Constants.NOTIFICATION_ID, alarmId);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal4am = Calendar.getInstance();
        cal4am.set(Calendar.HOUR_OF_DAY, 4);
        cal4am.set(Calendar.MINUTE, 0);
        cal4am.set(Calendar.SECOND, 0);
        cal4am.set(Calendar.MILLISECOND, 0);

        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal4am.getTimeInMillis(),  AlarmManager.INTERVAL_DAY, pi);
        }
    }

    public static void scheduleIntvReminder(Context context, JSONObject notif) {
        Notification notification = createNewNotif(context, notif);
        Intent singleIntent = new Intent(context, SingleAlarmReceiver.class);
        singleIntent.putExtra(Constants.NOTIFICATION, notification);

        int alarmId = notif.optInt("alarmId");
        singleIntent.putExtra(Constants.NOTIFICATION_ID, alarmId);
        singleIntent.putExtra("alarmId", alarmId);

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
                .setShowWhen(true);

        if (notif.optString("alarmType").equals("sleep_wake")) {
            builder.setSmallIcon(R.drawable.end_of_day_reminder);
        }

        return builder.build();
    }

    private static Intent createNewOnClickIntent(Context context, JSONObject notif, boolean wasDismissed) {
        Intent intent = new Intent(context, NotifClickORDismissReceiver.class);
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
}
