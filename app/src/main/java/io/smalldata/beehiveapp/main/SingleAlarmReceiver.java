package io.smalldata.beehiveapp.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.smalldata.beehiveapp.utils.AlarmHelper;

/**
 * Handle broadcast notification
 * Created by fnokeke on 1/25/17.
 */

public class SingleAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "SingleAlarmReceiver";

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 1);
        notificationManager.notify(id, notification);
//        Log.d(TAG, "onReceive: notif id=" + id + " " + "/ notif-info = " + notification.toString());
//
//        int alarmId = intent.getIntExtra("alarmId", 10);
//        String title = intent.getStringExtra(AlarmHelper.ALARM_NOTIF_TITLE);
//        String content = intent.getStringExtra(AlarmHelper.ALARM_NOTIF_CONTENT);
//        String appId = intent.getStringExtra(AlarmHelper.ALARM_APP_ID);
//        AlarmHelper.showInstantNotif(context, "id:" + id + "; aid:" + alarmId, content, appId, alarmId + 1);
    }
}

