package io.smalldata.beehiveapp.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

import io.smalldata.beehiveapp.config.GoogleCalendar;
import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.config.Rescuetime;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

public class RefreshService extends Service {

    private Rescuetime rescueTime;
    private GoogleCalendar googleCalendar;
    Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rescueTime = new Rescuetime(this);
        googleCalendar = new GoogleCalendar(this);
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateContents();
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateContents() {
        if (Store.getBoolean(mContext, Store.IS_RESCUETIME_ENABLED)) {
            rescueTime.refreshAndStoreStats();
        }

        if (Store.getBoolean(mContext, Store.IS_CALENDAR_ENABLED)) {
            googleCalendar.refreshAndStoreStats();
        }

        Helper.showInstantNotif(mContext, "Background Refresh Service.", "Done at: " + Helper.getTimestamp(), "", 3434);
        Intervention.prepareTodayIntervention(mContext);
    }

    public static void startRefreshInIntervals(Context context) {
        Intent refreshIntent = new Intent(context, RefreshService.class);
        PendingIntent pendingRefreshIntent = PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, pendingRefreshIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getMillisUntilTriggerTime(0), 12 * AlarmManager.INTERVAL_HOUR, pendingRefreshIntent);
    }

    public static long getMillisUntilTriggerTime(int hourOf24HourDay) {
        Calendar triggerAt = Calendar.getInstance();
        triggerAt.setTimeInMillis(System.currentTimeMillis());
        triggerAt.set(Calendar.HOUR_OF_DAY, hourOf24HourDay);
        triggerAt.set(Calendar.MINUTE, 0);
        triggerAt.set(Calendar.SECOND, 0);
        Calendar now = Calendar.getInstance();
        if (triggerAt.before(now)) triggerAt.add(Calendar.DAY_OF_MONTH, 1);
        return triggerAt.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

}
