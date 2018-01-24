package io.smalldata.beehiveapp.onboarding;

import android.content.Context;

import io.smalldata.beehiveapp.utils.JsonHelper;

/**
 * Created by fnokeke on 1/3/18.
 * Centralized place for shared constants
 */

public class Constants {

    public final static String ALARM_NOTIF_TITLE = "title";
    public final static String ALARM_NOTIF_CONTENT = "content";
    public final static String ALARM_APP_ID = "app_id";
    public final static String ALARM_MILLIS_SET = "alarm_millis";
    public final static String ALARM_NOTIF_WAS_DISMISSED = "was_dismissed";

    public final static String NOTIFICATION_ID = "notification-id";
    public final static String NOTIFICATION = "notification";

    final static String KEY_WEEKDAY_USER_WINDOW = "keyWeekdayUserWindow";
    final static String KEY_WEEKEND_USER_WINDOW = "keyWeekendUserWindow";
    final static String FIRSTNAME = "firstname";
    final static String STUDY_CONFIG_KEY = "studyConfig";
    final static String USERNAME = "username";

    final static String KEY_WEEKDAY_WAKE = "keyWeekdayWake";
    final static String KEY_WEEKDAY_SLEEP = "keyWeekdaySleep";
    final static String KEY_WEEKEND_WAKE = "keyWeekendWake";
    final static String KEY_WEEKEND_SLEEP = "keyWeekendSleep";

    final static String KEY_LAST_SAVED_DATE = "keyLastSavedDate";

    final static String KEY_TODAY_NOTIF_APPLIED = "keyTodayNotifApplied";

    public static final int DAILY_TASK_ALARM_ID = 9991;
    public static final String NOTIF_TYPE = "keyNotifType";
    public static final String ALARM_ID = "alarmId";
    public static final String TYPE_EMA = "typeEMA";
    public static final String EMA_RESPONSE = "emaResponse";
    public static final String KEY_FIRST_DAY_OF_STUDY = "isFirstDayOfStudy";

//    final static String NOTIF_TYPE_USER_WINDOW = "user_window";
//    final static String NOTIF_TYPE_SLEEP_WAKE = "sleep_wake";
}
