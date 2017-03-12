package io.smalldata.beehiveapp.config;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import io.smalldata.beehiveapp.fragment.SettingsFragment;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Daily Reminders configured on Beehive Platform will be handled here
 * Created by fnokeke on 2/21/17.
 */

public class DailyReminder extends BaseConfig {
    private Context mContext;
    private final static String REMINDER_TIME = "reminder_time";
    private Experiment experiment;
    private Random rand = new Random();

    public DailyReminder(Context context) {
        mContext = context;
        experiment = new Experiment(mContext);
    }

    public void saveSettings(JSONArray reminderConfig) {
        if (reminderConfig == null || reminderConfig.length() == 0) return;
        JSONObject lastItem = reminderConfig.optJSONObject(reminderConfig.length() - 1);
        Store.setString(mContext, REMINDER_TIME, lastItem.optString(REMINDER_TIME));
        extractThenSetReminder(getReminderTime());
    }

    private String getReminderTime() {
        return Store.getString(mContext, REMINDER_TIME);
    }

     private void extractThenSetReminder(String reminder) {
        if (reminder.equals("")) return;

        String[] hrMin = reminder.split(":");
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hrMin[0]));
        today.set(Calendar.MINUTE, Integer.parseInt(hrMin[1]));
        today.set(Calendar.SECOND, 0);
        setReminder(today.getTimeInMillis(), true);
    }

    public void triggerSetReminder() {
        if (experiment.notif_window_enabled()) {
            extractWindowTimeThenSetReminder(experiment.getWindowMintues());
        } else {
            extractThenSetReminder(experiment.getInterventionReminderTime());
        }
    }

    private void extractWindowTimeThenSetReminder(Integer windowMinutes) {
        long userTimeMillisFromSettings = SettingsFragment.getStartTimeFromSettings(mContext);
        if (userTimeMillisFromSettings == 0) {
            String title = "Select your reminder window";
            String content = "Go to Beehive App >> Settings >> Start Time";
            Helper.showInstantNotif(mContext, title, content, "", 5555);
            return;
        }
        userTimeMillisFromSettings = adjustMillisDateToToday(userTimeMillisFromSettings);
        long millisFromStart = getRandomInt(windowMinutes) * 60 * 1000;
        long futureAlarmMillis = userTimeMillisFromSettings + millisFromStart;
        setReminder(futureAlarmMillis, true);
    }

    private long adjustMillisDateToToday(long userTimeMillisFromSettings) {
        Calendar newTime = Calendar.getInstance();
        newTime.setTimeInMillis(userTimeMillisFromSettings);

        Calendar today = Calendar.getInstance();
        newTime.set(Calendar.YEAR, today.get(Calendar.YEAR));
        newTime.set(Calendar.MONTH, today.get(Calendar.MONTH));
        newTime.set(Calendar.DATE, today.get(Calendar.DATE));

        return newTime.getTimeInMillis();
    }

    private void setReminder(long alarmMillis, boolean shouldShowTip) {
        if (shouldShowTip) {
            String alarmTimeStr = Helper.getTimestamp(alarmMillis);
            String title = cannotTriggerAlarm() ? "Reminder won't show" : "Upcoming Reminder Tip";
            String content = "Reminder time: " + alarmTimeStr;
            Helper.showInstantNotif(mContext, title, content, "", 5555);
        }

        if (cannotTriggerAlarm()) return;
        JSONObject notif = Intervention.getNotifDetails(mContext);
        Helper.scheduleSingleAlarm(mContext, notif.optString("title"), notif.optString("content"), notif.optString("app_id"), alarmMillis);
        saveLastDayAndTimeAlarmScheduled(mContext, alarmMillis);
    }

    private boolean cannotTriggerAlarm() {
        return Intervention.alarmAlreadyScheduledToday(mContext);
    }

    private int getRandomInt(int max) {
        return rand.nextInt(max);
    }

    private void saveLastDayAndTimeAlarmScheduled(Context context, long alarmMillis) {
        Store.setString(context, Store.LAST_CHECKED_INTV_DATE, Helper.getTodaysDateStr());
        Store.setString(mContext, Store.LAST_SCHEDULED_REMINDER_TIME, String.valueOf(alarmMillis));
    }


}
