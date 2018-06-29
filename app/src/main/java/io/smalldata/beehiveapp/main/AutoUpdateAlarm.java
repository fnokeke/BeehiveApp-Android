package io.smalldata.beehiveapp.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import java.util.Locale;
import java.util.Calendar;

import io.smalldata.beehiveapp.config.DailyReminder;
import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.fragment.SettingsFragment;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.Store;

public class AutoUpdateAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        performUpdate(context);
        wl.release();
    }

    private void performUpdate(Context context) {
        String title;
        String lastReminderStr = Store.getString(context, Store.LAST_REMINDER_DATE);
        String todayStr = DateHelper.getTodayDateStr();
        if (DailyReminder.isNewDayForReminder(context)) {
            SettingsFragment.generateAndStoreReminders(context);
            title = "New: ";
        } else {
            title = "Same: ";
        }
        title = String.format(Locale.getDefault(), "%s: %s/%s", title, lastReminderStr, todayStr);
        Intervention.prepareTodayIntervention(context);
    }

    public void setAlarmForPeriodicUpdate(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AutoUpdateAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, getMillisUntilTriggerTime(10), AlarmManager.INTERVAL_DAY, pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 4 * AlarmManager.INTERVAL_HOUR, pi);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AutoUpdateAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
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