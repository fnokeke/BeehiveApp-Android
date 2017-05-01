package io.smalldata.beehiveapp.config;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import io.smalldata.beehiveapp.fragment.SettingsFragment;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

import static android.content.ContentValues.TAG;

/**
 * Daily Reminders configured on Beehive Platform will be handled here
 * Created by fnokeke on 2/21/17.
 */

public class DailyReminder extends BaseConfig {
    private Context mContext;
    private final static String REMINDER_TIME = "reminder_time";
    private Experiment experiment;

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

    public void triggerSetReminder() {
        if (cannotTriggerAlarm()) {
            long alarmMillis = Long.parseLong(Store.getString(mContext, Store.LAST_SCHEDULED_REMINDER_TIME));
            String alarmTimeStr = Helper.getTimestamp(alarmMillis);
            String content = "Last reminder time: " + alarmTimeStr;
            String title = "Cannot trigger Alarm. At: " + Helper.getTimestamp();
            Log.i(TAG, title + content);
            Helper.showInstantNotif(mContext, title, content, "", 5556);
            return;
        }

        if (experiment.notif_window_enabled()) {
            extractWindowTimeThenSetReminder();
        } else {
            extractThenSetReminder(experiment.getInterventionReminderTime());
        }

    }

    private void extractWindowTimeThenSetReminder() {
        String selectedWindowTime = SettingsFragment.getSelectedWindowTime(mContext);
        if (selectedWindowTime.equals("")) {
            String title = "Select your time preferences";
            String content = "Go to Beehive App >> Settings";
            Helper.showInstantNotif(mContext, title, content, "", 5555);
            return;
        }
        long millisFromStart = getRandomTimeInMillis(selectedWindowTime);
        long futureAlarmMillis = Calendar.getInstance().getTimeInMillis() + millisFromStart;
        setReminder(futureAlarmMillis, true);
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

    private void setReminder(long alarmMillis, boolean shouldShowTip) {
        if (shouldShowTip) {
            String alarmTimeStr = Helper.getTimestamp(alarmMillis);
            String title = cannotTriggerAlarm() ? "Reminder won't show" : "Upcoming Reminder Tip";
            String content = "Reminder time: " + alarmTimeStr;
            Log.i(TAG, title + content);
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

    private int getRandomTimeInMillis(String userWindowTime) {
        String[] window = userWindowTime.split("-");
        int millis = 60 * 60 * 1000;
        int start = Integer.parseInt(window[0]) * millis;
        int end = Integer.parseInt(window[1]) * millis;
        return getRandomInt(start, end); 
    }

    private int getRandomInt(int max, int min) {
        Random random = new Random();
        int range = max - min + 1;
        return random.nextInt(range) + min;
    }

    private void saveLastDayAndTimeAlarmScheduled(Context context, long alarmMillis) {
        Store.setString(context, Store.LAST_CHECKED_INTV_DATE, Helper.getTodaysDateStr());
        Store.setString(mContext, Store.LAST_SCHEDULED_REMINDER_TIME, String.valueOf(alarmMillis));
    }


}
