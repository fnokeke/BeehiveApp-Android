package io.smalldata.beehiveapp.onboarding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DateChangeOrRebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        TriggerIntervention triggerIntervention = new TriggerIntervention(context);

        boolean deviceWasRestarted = Intent.ACTION_BOOT_COMPLETED.equals(action);
        if (deviceWasRestarted) {
            triggerIntervention.startIntvForToday();
        }

        boolean dateChanged = Intent.ACTION_DATE_CHANGED.equals(action);
        if (dateChanged) {
            new Profile(context).resetSavedNotifForToday();
            triggerIntervention.startIntvForToday();
        }

    }

}
