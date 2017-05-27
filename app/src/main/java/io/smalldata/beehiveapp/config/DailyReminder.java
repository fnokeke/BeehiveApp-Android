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

public class DailyReminder {

    private Context mContext;

    public DailyReminder(Context context) {
        mContext = context;
    }

    private void showAlarmTip(long alarmMillis, long lastSetAlarm, String notifType) {
        String title = alreadySeenAlarm(lastSetAlarm) ? "Tip: Ignored, already seen. " : "Upcoming Reminder";
        String content = String.format("(%s) / (%s)", Helper.millisToDateFormat(lastSetAlarm), Helper.millisToDateFormat(alarmMillis));
        title = String.format("*%s* - %s", notifType, title);
        final int INSTANT_NOTIF_ID_DAILY = 7777;
        final int INSTANT_NOTIF_ID_SLEEP = 5555;
        final int notifId = notifType.equals("daily") ? INSTANT_NOTIF_ID_DAILY : INSTANT_NOTIF_ID_SLEEP;
        Helper.showInstantNotif(mContext, title, content, "", notifId);
    }

    public void setReminderBeforeBedTime(long bedTimeInMillis, boolean shouldShowTip) {
        if (bedTimeInMillis <= 0) return;
        long lastSetAlarm = Store.getLong(mContext, Store.LAST_SCHEDULED_BEDTIME_REMINDER);
        if (shouldShowTip) showAlarmTip(bedTimeInMillis, lastSetAlarm, "bedtime");
        if (alreadySeenAlarm(lastSetAlarm)) return;

        final String title = "BedTime Survey: How was your day?";
        final String content = "Tap here to respond.";
        final String appId = "io.smalldatalab.android.pam";
        final int BED_TIME_ALARM_ID = 5500;
        Helper.scheduleSingleAlarm(mContext, BED_TIME_ALARM_ID, title, content, appId, bedTimeInMillis);
        Store.setLong(mContext, Store.LAST_SCHEDULED_BEDTIME_REMINDER, bedTimeInMillis);
    }

    public void setTodayReminder(long alarmMillis, boolean shouldShowTip) {
        if (alarmMillis <= 0) return;
        long lastSetAlarm = Store.getLong(mContext, Store.LAST_SCHEDULED_REMINDER_TIME);
        if (shouldShowTip) showAlarmTip(alarmMillis, lastSetAlarm, "daily");
        if (alreadySeenAlarm(lastSetAlarm)) return;

        JSONObject notif = Intervention.getNotifDetails(mContext);
        final int DAILY_INTV_ALARM_ID = 7700;
        Helper.scheduleSingleAlarm(mContext, DAILY_INTV_ALARM_ID, notif.optString("title"), notif.optString("content"), notif.optString("app_id"), alarmMillis);
        Store.setLong(mContext, Store.LAST_SCHEDULED_REMINDER_TIME, alarmMillis);
        Store.setString(mContext, Store.LAST_CHECKED_INTV_DATE, Helper.getTodaysDateStr());
    }

    private boolean alreadySeenAlarm(long alarmMillis) {
        long rightNow = Calendar.getInstance().getTimeInMillis();
        return intvAlreadySetForToday() && (rightNow > alarmMillis) && (alarmMillis > 0);
    }

    private boolean intvAlreadySetForToday() {
        String lastCheckedDate = Store.getString(mContext, Store.LAST_CHECKED_INTV_DATE);
        String today = Helper.getTodaysDateStr();
        return today.equals(lastCheckedDate);
    }

}
