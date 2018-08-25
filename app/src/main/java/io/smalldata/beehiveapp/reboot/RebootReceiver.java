package io.smalldata.beehiveapp.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.smalldata.beehiveapp.fcm.ServerPeriodicUpdateReceiver;
import io.smalldata.beehiveapp.notification.DailyTaskReceiver;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            DailyTaskReceiver.startDaily3amTask(context);
            ServerPeriodicUpdateReceiver.startRepeatingServerTask(context);
        }

    }

}
