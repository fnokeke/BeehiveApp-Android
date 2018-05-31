package io.smalldata.beehiveapp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.studyManagement.RSActivityManager;

/**
 * Handle broadcast notification
 * Created by fnokeke on 1/25/17.
 */

public class ProtocolAlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            throw new UnsupportedOperationException("Your notification manager should not be null");
        }

        int id = intent.getIntExtra(Constants.NOTIFICATION_ID, 1);
        Notification notification = intent.getParcelableExtra(Constants.NOTIFICATION);
        notificationManager.notify(id, notification);
    }
}

