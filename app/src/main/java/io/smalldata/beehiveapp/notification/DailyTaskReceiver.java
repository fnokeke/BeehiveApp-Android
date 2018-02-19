package io.smalldata.beehiveapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.smalldata.beehiveapp.fcm.ServerPeriodicUpdateReceiver;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.onboarding.TriggerIntervention;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;

public class DailyTaskReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmHelper.showInstantNotif(context, "Started 3am Task",
                DateHelper.getFormattedTimestamp(),
                "", 7711); // FIXME: 1/15/18 remove debug

        new TriggerIntervention(context).startIntvForToday();
    }
}
