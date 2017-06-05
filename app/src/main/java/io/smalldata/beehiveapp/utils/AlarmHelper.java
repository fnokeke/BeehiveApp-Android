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
import io.smalldata.beehiveapp.main.MainActivity;
import io.smalldata.beehiveapp.main.SingleAlarmReceiver;

/**
 * Helper.java
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
                .setContentText(message);

        if (!appIdToLaunch.equals("")) {
            Intent launchAppIntent = IntentLauncher.getLaunchIntent(context, appIdToLaunch);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(contentIntent);
        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }

    public static void scheduleSingleAlarm(Context context, int alarmId, String title, String content, String appIdToLaunch, long alarmMillis) {
        Intent singleIntent = new Intent(context, SingleAlarmReceiver.class);
        Notification notification = createNotification(context, alarmId, title, content, appIdToLaunch, alarmMillis);
        singleIntent.putExtra(SingleAlarmReceiver.NOTIFICATION, notification);
        singleIntent.putExtra(SingleAlarmReceiver.NOTIFICATION_ID, alarmId);

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

    private static Intent appendExtras(Intent intent, String title, String content, String appIdToLaunch, long alarmMillis, boolean wasDismissed) {
        intent.putExtra(ALARM_NOTIF_TITLE, title);
        intent.putExtra(ALARM_NOTIF_CONTENT, content);
        intent.putExtra(ALARM_APP_ID, appIdToLaunch);
        intent.putExtra(ALARM_MILLIS_SET, alarmMillis);
        intent.putExtra(ALARM_NOTIF_WAS_DISMISSED, wasDismissed);
        return intent;
    }

    private static Notification createNotification(Context context, int alarmId, String title, String content, String appIdToLaunch, long alarmMillis) {
        Intent appLauncherIntent = new Intent(context, MainActivity.class);
        appLauncherIntent = appendExtras(appLauncherIntent, title, content, appIdToLaunch, alarmMillis, false);

        Intent deleteIntent = new Intent();
        deleteIntent = appendExtras(deleteIntent, title, content, appIdToLaunch, alarmMillis, true);

        final int CONTENT_INTENT_PREFIX = 16;// make requestCode unique
        final int DELETE_INTENT_PREFIX = 32; // make requestCode unique

        PendingIntent contentIntent = PendingIntent.getActivity(context, CONTENT_INTENT_PREFIX + alarmId, appLauncherIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deletePendingIntent = PendingIntent.getActivity(context, DELETE_INTENT_PREFIX + alarmId, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(getDefaultSound())
                .setDeleteIntent(deletePendingIntent)
                .setShowWhen(true)
                .setSmallIcon(android.R.drawable.ic_popup_reminder);

        return builder.build();
    }

    private static Uri getDefaultSound() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

}


