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
    private final int DAILY_INTV_ALARM_ID = 7700;
    private final int INSTANT_NOTIF_ID_DAILY = 7777;
    private final int BED_TIME_ALARM_ID = 5500;
    private final int INSTANT_NOTIF_ID_SLEEP = 5555;

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

    void oldsetTodayReminder() {
        long alarmMillis = Long.parseLong(Store.getString(mContext, Store.LAST_SCHEDULED_REMINDER_TIME));
        if (alreadySeenAlarm(alarmMillis)) {
            String alarmTimeStr = Helper.millisToDateFormat(alarmMillis);
            String content = "Set daily alarm requested: " + alarmTimeStr;
            String title = "Already saw alarm at: " + Helper.getTimestamp();
            Helper.showInstantNotif(mContext, title, content, "", INSTANT_NOTIF_ID_DAILY);
            return;
        }

        if (Experiment.isNotifWindowEnabled(mContext)) {
            extractWindowTimeThenSetReminder();
        } else {
            extractThenSetReminder(Experiment.getInterventionReminderTime(mContext));
        }

    }

    public void extractWindowTimeThenSetReminder() { //FIXME
        String selectedWindowTime = SettingsFragment.getSelectedWindowTime(mContext);
        if (selectedWindowTime.equals("")) {
            return;
        }
        long alarmTimeMillis = getAlarmTimeInMillis(selectedWindowTime);
        oldSetDailyReminder(alarmTimeMillis, true, DAILY_INTV_ALARM_ID, true);
    }

//    public void setReminderBeforeBedTime(long bedTimeInMillis, boolean ignorePastAlarm) {
//        long rightNow = Calendar.getInstance().getTimeInMillis();
//        String instantTitle = ignorePastAlarm ? "Upcoming BedTime Reminder (Ignore if alarm past)" : "Upcoming BedTime Reminder";
//        Helper.showInstantNotif(mContext, instantTitle, "Alarm at: " + Helper.millisToDateFormat(bedTimeInMillis), "", INSTANT_NOTIF_ID_SLEEP);
//        if (ignorePastAlarm && rightNow > bedTimeInMillis) return;
//
//        String title = "BedTime Survey: How was your day?";
//        String content = "Tap here to respond.";
//        String appId = "io.smalldatalab.android.pam";
//        Helper.scheduleSingleAlarm(mContext, BED_TIME_ALARM_ID, title, content, appId, bedTimeInMillis);
//        Store.setString(mContext, Store.LAST_SCHEDULED_BEDTIME_REMINDER, String.valueOf(bedTimeInMillis));
//    }


    private void extractThenSetReminder(String reminder) {
        if (reminder.equals("")) return;

        String[] hrMin = reminder.split(":");
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hrMin[0]));
        today.set(Calendar.MINUTE, Integer.parseInt(hrMin[1]));
        today.set(Calendar.SECOND, 0);
        oldSetDailyReminder(today.getTimeInMillis(), true, DAILY_INTV_ALARM_ID, true);
    }

    private void showAlarmTip(long alarmMillis, long lastSetAlarm, String notifType) {
        String title = alreadySeenAlarm(lastSetAlarm) ? "Tip: Ignored, already seen. " : "Upcoming Reminder";
        String content = String.format("(%s) / (%s)", Helper.millisToDateFormat(lastSetAlarm), Helper.millisToDateFormat(alarmMillis));
        title = String.format("*%s* - %s", notifType, title);
        int notifId = notifType.equals("daily") ? INSTANT_NOTIF_ID_DAILY : INSTANT_NOTIF_ID_SLEEP;
        Helper.showInstantNotif(mContext, title, content, "", notifId);
    }

    public void setReminderBeforeBedTime(long bedTimeInMillis, boolean shouldShowTip) {
        if (bedTimeInMillis <= 0) return;
        long lastSetAlarm = Store.getLong(mContext, Store.LAST_SCHEDULED_BEDTIME_REMINDER);
        if (shouldShowTip) showAlarmTip(bedTimeInMillis, lastSetAlarm, "bedtime");
        if (alreadySeenAlarm(lastSetAlarm)) return;

        String title = "BedTime Survey: How was your day?";
        String content = "Tap here to respond.";
        String appId = "io.smalldatalab.android.pam";
        Helper.scheduleSingleAlarm(mContext, BED_TIME_ALARM_ID, title, content, appId, bedTimeInMillis);
        Store.setLong(mContext, Store.LAST_SCHEDULED_BEDTIME_REMINDER, bedTimeInMillis);
    }

    public void setTodayReminder(long alarmMillis, boolean shouldShowTip) {
        if (alarmMillis <= 0) return;
        long lastSetAlarm = Store.getLong(mContext, Store.LAST_SCHEDULED_REMINDER_TIME);
        if (shouldShowTip) showAlarmTip(alarmMillis, lastSetAlarm, "daily");
        if (alreadySeenAlarm(lastSetAlarm)) return;

        JSONObject notif = Intervention.getNotifDetails(mContext);
        Helper.scheduleSingleAlarm(mContext, DAILY_INTV_ALARM_ID, notif.optString("title"), notif.optString("content"), notif.optString("app_id"), alarmMillis);
        Store.setLong(mContext, Store.LAST_SCHEDULED_REMINDER_TIME, alarmMillis);
        Store.setString(mContext, Store.LAST_CHECKED_INTV_DATE, Helper.getTodaysDateStr());
    }

    private boolean alreadySeenAlarm(long alarmMillis) {
        long rightNow = Calendar.getInstance().getTimeInMillis();
        return intvAlreadySetForToday() && (rightNow > alarmMillis) && (alarmMillis > 0);
//        return (rightNow > alarmMillis) && (alarmMillis > 0);
    }

    private boolean intvAlreadySetForToday() {
        String lastCheckedDate = Store.getString(mContext, Store.LAST_CHECKED_INTV_DATE);
        String today = Helper.getTodaysDateStr();
        return today.equals(lastCheckedDate);
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

    private void oldSetDailyReminder(long alarmMillis, boolean shouldShowTip, int alarmId, boolean ignorePastAlarm) {
        long lastSetAlarm = Long.parseLong(Store.getString(mContext, Store.LAST_SCHEDULED_REMINDER_TIME));
        if (shouldShowTip) {
            String alarmTimeStr = Helper.millisToDateFormat(alarmMillis);
            String title = alreadySeenAlarm(lastSetAlarm) ? "Remove!!: Already seen reminder" : "Upcoming Daily Reminder";
            String content = "Alarm time set at: " + alarmTimeStr;
            Log.i(TAG, title + content);
            Helper.showInstantNotif(mContext, title, content, "", INSTANT_NOTIF_ID_DAILY);
        }

        long rightNow = Calendar.getInstance().getTimeInMillis();
        if (ignorePastAlarm && rightNow > alarmMillis) return;
        if (alreadySeenAlarm(lastSetAlarm)) return;

        JSONObject notif = Intervention.getNotifDetails(mContext);
        Helper.scheduleSingleAlarm(mContext, alarmId, notif.optString("title"), notif.optString("content"), notif.optString("app_id"), alarmMillis);
        saveLastDayAndTimeAlarmScheduled(mContext, alarmMillis);
        Store.setString(mContext, Store.LAST_SCHEDULED_REMINDER_TIME, String.valueOf(alarmMillis));
    }


}
