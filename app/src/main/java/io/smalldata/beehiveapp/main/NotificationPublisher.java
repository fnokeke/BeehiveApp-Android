package io.smalldata.beehiveapp.main;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Handle broadcast notification
 * Created by fnokeke on 1/25/17.
 */

import android.app.Notification;

import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        int counter = Store.getInt(context, "alarmCounter");
        counter += 1;
        Store.setInt(context, "alarmCounter", counter);

        String title = String.format(Constants.LOCALE, "No%d: Alarm time", counter);
        String content =String.format(Constants.LOCALE, "Fired @ %s", Helper.getTimestamp());
        Helper.showInstantNotif(context, title, content);

        notificationManager.notify(id, notification);
    }

}
