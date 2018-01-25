package io.smalldata.beehiveapp.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.smalldata.beehiveapp.onboarding.TriggerIntervention;
import io.smalldata.beehiveapp.server.ServerPeriodicUpdateReceiver;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            TriggerIntervention.startDaily3amTask(context, false);
            ServerPeriodicUpdateReceiver.setAlarmForPeriodicUpdate(context);
        }

    }

}
