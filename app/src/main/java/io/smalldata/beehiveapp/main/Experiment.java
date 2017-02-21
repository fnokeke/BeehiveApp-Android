package io.smalldata.beehiveapp.main;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import io.smalldata.beehiveapp.config.DailyReminder;
import io.smalldata.beehiveapp.config.GeneralNotification;
import io.smalldata.beehiveapp.config.GoogleCalendar;
import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.config.Rescuetime;
import io.smalldata.beehiveapp.config.ScreenUnlock;
import io.smalldata.beehiveapp.config.Vibration;
import io.smalldata.beehiveapp.utils.Store;

/**
 *
 * Created by fnokeke on 2/21/17.
 */

public class Experiment {

    private Context mContext;

    public Experiment(Context context) {
        mContext = context;
    }

    public void save(JSONObject experiment) {
        if (experiment.optString("start").equals("")) return;
        Store.setString(mContext, "expStart", experiment.optString("start"));
        Store.setString(mContext, "expEnd", experiment.optString("end"));
        Store.setString(mContext, "expTitle", experiment.optString("title"));

        JSONArray generalNotificationConfig = experiment.optJSONArray("general_notification_config");
        new GeneralNotification(mContext).saveSettings(generalNotificationConfig);

        JSONArray interventions = experiment.optJSONArray("interventions");
        new Intervention(mContext).saveSettings(interventions);

        JSONArray calendarConfig = experiment.optJSONArray("calendar_config");
        new GoogleCalendar(mContext).saveSettings(calendarConfig);

        JSONArray dailyReminderConfig = experiment.optJSONArray("daily_reminder_config");
        new DailyReminder(mContext).saveSettings(dailyReminderConfig);

        JSONArray rescuetimeConfig = experiment.optJSONArray("rescuetime_config");
        new Rescuetime(mContext).saveSettings(rescuetimeConfig);

        JSONArray screenUnlockConfig = experiment.optJSONArray("screen_unlock_config");
        new ScreenUnlock(mContext).saveSettings(screenUnlockConfig);

        JSONArray vibrationConfig = experiment.optJSONArray("vibration_config");
        new Vibration(mContext).saveSettings(vibrationConfig);

        Store.printAll(mContext);
    }

}
