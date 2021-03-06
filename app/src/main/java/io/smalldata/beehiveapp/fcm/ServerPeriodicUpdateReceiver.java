package io.smalldata.beehiveapp.fcm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class ServerPeriodicUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "BeehiveAppJobService";

    @Override
    public void onReceive(Context context, Intent intent) {
        dispatchOneTimeUpdate(context);
    }

    private void dispatchOneTimeUpdate(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = dispatcher.newJobBuilder()
                .setService(AppJobService.class)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(60, 180))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setTag(TAG)
                .build();
        dispatcher.mustSchedule(job);
    }

    public static void startRepeatingServerTask(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ServerPeriodicUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (am == null) {
            throw new UnsupportedOperationException("Repeating alarmManager should not be null");
        }
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    public static void registerUserOnLoginComplete(Context context) {
        AppJobService.registerMobileUserOnLoginComplete(context);
    }
}
