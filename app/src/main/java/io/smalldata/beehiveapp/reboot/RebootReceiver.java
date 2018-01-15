package io.smalldata.beehiveapp.reboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.smalldata.beehiveapp.onboarding.TriggerIntervention;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            TriggerIntervention.startDaily4amTask(context, false);
        }

    }

}
