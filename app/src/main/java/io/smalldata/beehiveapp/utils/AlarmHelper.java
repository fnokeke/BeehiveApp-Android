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

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.notification.NotifEventReceiver;
import io.smalldata.beehiveapp.notification.ProtocolAlarmReceiver;
import io.smalldata.beehiveapp.onboarding.Constants;

/**
 * AlarmHelper.java
 * Created: 1/24/17
 * author: Fabian Okeke
 */

public class AlarmHelper {
    public final static String ALARM_NOTIF_TITLE = "title";
    public final static String ALARM_NOTIF_CONTENT = "content";
    public final static String ALARM_APP_ID = "app_id";
    public final static String ALARM_MILLIS_SET = "alarm_millis";
    public final static String ALARM_NOTIF_WAS_DISMISSED = "was_dismissed";

    public static void showInstantNotif(Context context, String title, String message, String appIdToLaunch, Integer NOTIF_ID) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.info_tip)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSound(getDefaultSound())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        if (!appIdToLaunch.equals("")) {
            Intent launchAppIntent = IntentLauncher.getLaunchIntent(context, appIdToLaunch);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(contentIntent);
        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }

    public static void scheduleSingleAlarm(Context context, int alarmId, String title, String content, String appIdToLaunch, long alarmMillis, String alarmType) {
        Intent singleIntent = new Intent(context, ProtocolAlarmReceiver.class);
        Notification notification = createNotification(context, alarmId, title, content, appIdToLaunch, alarmMillis, alarmType);
        singleIntent.putExtra(Constants.NOTIFICATION, notification);
        singleIntent.putExtra(Constants.NOTIFICATION_ID, alarmId);

        singleIntent.putExtra("alarmId", alarmId);
        singleIntent.putExtra(ALARM_NOTIF_TITLE, title);
        singleIntent.putExtra(ALARM_NOTIF_CONTENT, content);
        singleIntent.putExtra(ALARM_APP_ID, appIdToLaunch);
        singleIntent.putExtra(ALARM_MILLIS_SET, alarmMillis); // FIXME: 6/5/17 debug code

        final int PENDING_INTENT_PREFIX = 64; // make requestCode unique
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, PENDING_INTENT_PREFIX + alarmId, singleIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmMillis, pendingIntent);
    }

    private static Intent createOnClickIntent(Context context, String title, String content, String appIdToLaunch, long alarmMillis, boolean wasDismissed) {
        Intent intent = new Intent(context, NotifEventReceiver.class);
        intent.putExtra(ALARM_NOTIF_TITLE, title);
        intent.putExtra(ALARM_NOTIF_CONTENT, content);
        intent.putExtra(ALARM_APP_ID, appIdToLaunch);
        intent.putExtra(ALARM_MILLIS_SET, alarmMillis);
        intent.putExtra(ALARM_NOTIF_WAS_DISMISSED, wasDismissed);
        return intent;
    }

    private static Notification createNotification(Context context, int alarmId, String title, String content, String appIdToLaunch, long alarmMillis, String alarmType) {
        Intent appLauncherIntent = createOnClickIntent(context, title, content, appIdToLaunch, alarmMillis, false);
        Intent deleteIntent = createOnClickIntent(context, title, content, appIdToLaunch, alarmMillis, true);

        final int CONTENT_INTENT_PREFIX = 16;// make requestCode unique
        final int DELETE_INTENT_PREFIX = 32; // make requestCode unique

        PendingIntent contentIntent = PendingIntent.getBroadcast(context, CONTENT_INTENT_PREFIX + alarmId, appLauncherIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, DELETE_INTENT_PREFIX + alarmId, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(getDefaultSound())
                .setDeleteIntent(deletePendingIntent)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setShowWhen(true);

        if (alarmType.equals("sleep")) {
            builder.setSmallIcon(R.drawable.end_of_day_reminder);
        }

        return builder.build();
    }

    private static Uri getDefaultSound() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

}


