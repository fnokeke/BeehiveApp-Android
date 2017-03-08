package io.smalldata.beehiveapp.config;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.fragment.HomeFragment;
import io.smalldata.beehiveapp.fragment.SettingsFragment;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.main.MainActivity;
import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

import static android.R.attr.delay;
import static android.R.attr.max;
import static android.provider.UserDictionary.Words.LOCALE;

/**
 * Daily Reminders configured on Beehive Platform will be handled here
 * Created by fnokeke on 2/21/17.
 */

public class DailyReminder extends BaseConfig {
    private Context mContext;
    private final static String REMINDER_TIME = "reminder_time";
    private Experiment experiment;
    Random rand = new Random();

    public DailyReminder(Context context) {
        mContext = context;
        experiment = new Experiment(mContext);
    }

    public void saveSettings(JSONArray reminderConfig) {
        if (reminderConfig == null || reminderConfig.length() == 0) return;
        JSONObject lastItem = reminderConfig.optJSONObject(reminderConfig.length() - 1);
        Store.setString(mContext, REMINDER_TIME, lastItem.optString(REMINDER_TIME));
        setReminder(getReminderTime());
    }

    private String getReminderTime() {
        return Store.getString(mContext, REMINDER_TIME);
    }

    public void setReminder(String reminder) {
        if (reminder.equals("")) return;

        String[] hrMin = reminder.split(":");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hrMin[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(hrMin[1]));
        cal.set(Calendar.SECOND, 0);

        Calendar rightNow = Calendar.getInstance();
        if (cal.getTimeInMillis() <= rightNow.getTimeInMillis()) return;

        String alarmTimeStr = Helper.getTimestamp(cal);
        Helper.showInstantNotif(mContext, "Quick Reminder Tip", "Expect reminder at " + alarmTimeStr, "", 5555);

        GeneralNotification generalNotification = new GeneralNotification(mContext);
        String title = generalNotification.getTitle().equals("") ? "1 new message." : generalNotification.getTitle();
        String content = generalNotification.getContent().equals("") ? "Tap to view immediately." : generalNotification.getContent();
        Helper.scheduleSingleAlarm(mContext, title, content, generalNotification.getAppId(), cal.getTimeInMillis());
    }

    void triggerSetReminder() {
        if (experiment.notif_window_enabled()) {
            setReminderFromWindowTime(experiment.getWindowMintues());
        } else {
            setReminder(experiment.getInterventionReminderTime());
        }
    }

    private void setReminderFromWindowTime(Integer windowMinutes) {
        long startTimeFromSettings = SettingsFragment.getStartTimeFromSettings(mContext);
        if (startTimeFromSettings == 0) {
            String title = "Select your reminder window";
            String content = "Go to Beehive App >> Settings >> Start Time";
            Helper.showInstantNotif(mContext, title, content, mContext.getPackageName(), 7777);
            return;
        }

        if (windowMinutes == null) throw new AssertionError("Window Minutes shouldn't be null");

        int minutesFromStart = getRandomInt(windowMinutes);
        long millisFromStart = minutesFromStart * 60 * 1000;

        long futureAlarmMillis = startTimeFromSettings + 60000;
        String alarmTimeStr = Helper.getTimestamp(futureAlarmMillis);
        Helper.showInstantNotif(mContext, "Reminder Tip", "Upcoming reminder at " + alarmTimeStr, "", 5555);

        JSONObject notif = Intervention.getNotifDetails(mContext);
        scheduleNotification(getNotification(notif.optString("content")), futureAlarmMillis, 15000, 6666);

//        timeMillis = System.currentTimeMillis();
//            Calendar rightNow = Calendar.getInstance();
//            timeMillis = rightNow.getTimeInMillis();
//        Calendar futureAlarmTime = Calendar.getInstance();

//        futureAlarmTime.setTimeInMillis(timeMillis);
//        futureAlarmTime.setTimeInMillis(futureAlarmTime.getTimeInMillis() + 60000);
//        Helper.scheduleSingleAlarm(mContext, notif.optString("title"), notif.optString("content"),
//                notif.optString("app_id"), futureAlarmTime.getTimeInMillis());

    }


    private void scheduleNotification(Notification notification, long actualTime, int delay, int id) {

        Intent notificationIntent = new Intent(mContext, HomeFragment.class);
        notificationIntent.putExtra("NOTIFICATION_ID", id);
        notificationIntent.putExtra("NOTIFICATION", notification);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
//                actualTime,
                System.currentTimeMillis() + delay,
                PendingIntent.getBroadcast(mContext, id, notificationIntent, PendingIntent.FLAG_ONE_SHOT));
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_link);
        return builder.getNotification();
    }

    private int getRandomInt(int max) {
        return rand.nextInt(max);
    }
}
