package io.smalldata.beehiveapp.config;

import android.content.Context;

import org.json.JSONObject;

import java.util.Calendar;

import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.Store;

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
        String content = String.format("(%s) / (%s)", DateHelper.millisToDateFormat(lastSetAlarm), DateHelper.millisToDateFormat(alarmMillis));
        title = String.format("*%s* - %s", notifType, title);
        final int INSTANT_NOTIF_ID_DAILY = 7777;
        final int INSTANT_NOTIF_ID_SLEEP = 5555;
        final int notifId = notifType.equals("daily") ? INSTANT_NOTIF_ID_DAILY : INSTANT_NOTIF_ID_SLEEP;
        AlarmHelper.showInstantNotif(mContext, title, content, "", notifId);
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
        AlarmHelper.scheduleSingleAlarm(mContext, BED_TIME_ALARM_ID, title, content, appId, bedTimeInMillis);
        Store.setLong(mContext, Store.LAST_SCHEDULED_BEDTIME_REMINDER, bedTimeInMillis);
    }

    public void setTodayReminder(long alarmMillis, boolean shouldShowTip) {
        if (alarmMillis <= 0) return;
        long lastSetAlarm = Store.getLong(mContext, Store.LAST_SCHEDULED_REMINDER_TIME);
        if (shouldShowTip) showAlarmTip(alarmMillis, lastSetAlarm, "daily");
        if (alreadySeenAlarm(lastSetAlarm)) return;

        JSONObject notif = Intervention.getNotifDetails(mContext);
        final int DAILY_INTV_ALARM_ID = 7700;
        AlarmHelper.scheduleSingleAlarm(mContext, DAILY_INTV_ALARM_ID, notif.optString("title"), notif.optString("content"), notif.optString("app_id"), alarmMillis);
        Store.setLong(mContext, Store.LAST_SCHEDULED_REMINDER_TIME, alarmMillis);
        Store.setString(mContext, Store.LAST_CHECKED_INTV_DATE, DateHelper.getTodayDateStr());
        Store.setString(mContext, Store.LAST_REMINDER_DATE, DateHelper.getTodayDateStr());
    }

    private boolean alreadySeenAlarm(long alarmMillis) {
        long rightNow = Calendar.getInstance().getTimeInMillis();
        return intvAlreadySetForToday() && (rightNow > alarmMillis) && (alarmMillis > 0);
    }

    private boolean intvAlreadySetForToday() {
        String lastCheckedDate = Store.getString(mContext, Store.LAST_CHECKED_INTV_DATE);
        String today = DateHelper.getTodayDateStr();
        return today.equals(lastCheckedDate);
    }

    public static boolean isNewDayForReminder(Context context) {
        String lastReminderDate = Store.getString(context, Store.LAST_REMINDER_DATE);
        String today = DateHelper.getTodayDateStr();
        return !today.equals(lastReminderDate);
    }
}
