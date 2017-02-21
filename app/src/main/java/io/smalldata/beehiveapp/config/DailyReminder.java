package io.smalldata.beehiveapp.config;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Daily Reminders configured on Beehive Platform will be handled here
 * Created by fnokeke on 2/21/17.
 */

public class DailyReminder extends BaseConfig {
    private Context mContext;
    private final static String REMINDER_TIME = "reminder_time";

    public DailyReminder(Context context) {
        mContext = context;
    }

    public void saveSettings(JSONArray reminderConfig) {
        if (reminderConfig == null || reminderConfig.length() == 0) return;
        JSONObject lastItem = reminderConfig.optJSONObject(reminderConfig.length() - 1);
        Store.setString(mContext, REMINDER_TIME, lastItem.optString(REMINDER_TIME));
        setReminder(getReminder());
    }

    private void setReminder(String reminder) {
        if (reminder.equals("")) return;
        GeneralNotification generalNotification = new GeneralNotification(mContext);
        String title = generalNotification.getTitle().equals("") ? "1 new message." : generalNotification.getTitle();
        String content = generalNotification.getContent().equals("") ? "Tap to view immediately." : generalNotification.getContent();

        String [] hrMin = reminder.split(":");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hrMin[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(hrMin[1]));

        String alarmTimeStr = Helper.getTimestamp(cal);
        Helper.showInstantNotif(mContext, "Quick Reminder Tip for Hourly Alarm", "Expect first reminder at: " + alarmTimeStr);
        Helper.scheduleNotification(mContext, title, content, generalNotification.getAppId(), cal.getTimeInMillis());

    }

    private String getReminder() {
        return Store.getString(mContext, REMINDER_TIME);
    }

}
