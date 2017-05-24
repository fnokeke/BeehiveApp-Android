package io.smalldata.beehiveapp.config;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

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
    private final int BED_TIME_ALARM_ID = 3434;
    private final int DAILY_INTV_ALARM_ID = 7878;
    private final int INSTANT_NOTIF_ID = 5555;
    private final int INSTANT_NOTIF_ID2 = 5556;

    private Context mContext;
    private final static String REMINDER_TIME = "reminder_time";

    public DailyReminder(Context context) {
        mContext = context;
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

    void triggerSetReminder() {
        if (cannotTriggerAlarm()) {
            long alarmMillis = Long.parseLong(Store.getString(mContext, Store.LAST_SCHEDULED_REMINDER_TIME));
            String alarmTimeStr = Helper.millisToDateFormat(alarmMillis);
            String content = "Last reminder time: " + alarmTimeStr;
            String title = "Cannot trigger Alarm. At: " + Helper.getTimestamp();
            Log.i(TAG, title + content);
            Helper.showInstantNotif(mContext, title, content, "", INSTANT_NOTIF_ID2);
            return;
        }

        if (Experiment.isNotifWindowEnabled(mContext)) {
            extractWindowTimeThenSetReminder();
        } else {
            extractThenSetReminder(Experiment.getInterventionReminderTime(mContext));
        }

    }

    public void extractWindowTimeThenSetReminder() {
        SettingsFragment settingsFragment = new SettingsFragment();
        String selectedWindowTime = settingsFragment.getSelectedWindowTime();
        if (selectedWindowTime.equals("")) {
            String title = "Select your time preferences";
            String content = "Go to Beehive App >> Settings";
            Helper.showInstantNotif(mContext, title, content, "", 5555);
            return;
        }
        long alarmTimeMillis = getAlarmTimeInMillis(selectedWindowTime);
        setReminder(alarmTimeMillis, true, DAILY_INTV_ALARM_ID);
    }

    public void setReminderBeforeBedTime(long bedTimeInMillis) {
        String title = "How was your day?";
        String content = "Tap here to respond.";
        String appId = "io.smalldatalab.android.pam";
        Helper.showInstantNotif(mContext, "Last Reminder of Day", Helper.millisToDateFormat(bedTimeInMillis), "", INSTANT_NOTIF_ID);
        Helper.scheduleSingleAlarm(mContext, BED_TIME_ALARM_ID, title, content, appId, bedTimeInMillis);
    }

    private void extractThenSetReminder(String reminder) {
        if (reminder.equals("")) return;

        String[] hrMin = reminder.split(":");
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hrMin[0]));
        today.set(Calendar.MINUTE, Integer.parseInt(hrMin[1]));
        today.set(Calendar.SECOND, 0);
        setReminder(today.getTimeInMillis(), true, DAILY_INTV_ALARM_ID);
    }

    private void setReminder(long alarmMillis, boolean shouldShowTip, int alarmId) {
        if (shouldShowTip) {
            String alarmTimeStr = Helper.millisToDateFormat(alarmMillis);
            String title = cannotTriggerAlarm() ? "Reminder won't show" : "Upcoming Reminder Tip";
            String content = "Reminder time: " + alarmTimeStr;
            Log.i(TAG, title + content);
            Helper.showInstantNotif(mContext, title, content, "", INSTANT_NOTIF_ID);
        }

        if (cannotTriggerAlarm()) return;
        JSONObject notif = Intervention.getNotifDetails(mContext);
        Helper.scheduleSingleAlarm(mContext, alarmId, notif.optString("title"), notif.optString("content"), notif.optString("app_id"), alarmMillis);
        saveLastDayAndTimeAlarmScheduled(mContext, alarmMillis);
    }

    private boolean cannotTriggerAlarm() {
        return Intervention.alarmAlreadyScheduledToday(mContext);
    }

    private long getAlarmTimeInMillis(String userWindowTime) {
        String[] window = userWindowTime.split("-");
        int startHour = Integer.parseInt(window[0]);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, startHour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        int endHour = Integer.parseInt(window[1]);
        int diffInMinutes = (endHour - startHour) * 60;
        long millisFromStart = Helper.getRandomInt(0, diffInMinutes) * 60 * 1000;
        return cal.getTimeInMillis() + millisFromStart;
    }

    private void saveLastDayAndTimeAlarmScheduled(Context context, long alarmMillis) {
        Store.setString(context, Store.LAST_CHECKED_INTV_DATE, Helper.getTodaysDateStr());
        Store.setString(mContext, Store.LAST_SCHEDULED_REMINDER_TIME, String.valueOf(alarmMillis));
    }


}
