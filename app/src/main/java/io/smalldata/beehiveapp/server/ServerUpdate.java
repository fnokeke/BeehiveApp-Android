package io.smalldata.beehiveapp.server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import io.smalldata.beehiveapp.fcm.AppJobService;

public class ServerUpdate extends Service {

    private static final String TAG = "BeehiveAppJobService";

    public ServerUpdate() {
        dispatchOneTimeUpdate(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void dispatchOneTimeUpdate(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = dispatcher.newJobBuilder()
                .setService(AppJobService.class)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0, 60))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setTag(TAG)
                .build();
        dispatcher.mustSchedule(job);
    }

    public static void setAlarmForPeriodicUpdate(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ServerUpdate.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (am == null) {
            throw new UnsupportedOperationException("Repeating alarmManager should not be null");
        }
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, pi);
    }

}
