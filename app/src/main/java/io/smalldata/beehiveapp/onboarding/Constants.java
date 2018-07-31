package io.smalldata.beehiveapp.onboarding;

import android.os.Environment;

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
    public static final String ALARM_PROTOCOL_METHOD = "method";
    public static final String ALARM_PROTOCOL_NOTIF_DETAILS = "notif_details";

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
    static final String KEY_ITEM_POSITION_WEEKDAY = "keyItemPositionWeekday";
    static final String KEY_ITEM_POSITION_WEEKEND = "keyItemPositionWeekend";

    final static String KEY_LAST_SAVED_DATE = "keyLastSavedDate";
    final static String KEY_TODAY_NOTIF_APPLIED = "keyTodayNotifApplied";

    static final int DAILY_TASK_ALARM_ID = 9991;
    static final String EMA_RESPONSE = "emaResponse";
    static final String KEY_FIRST_DAY_OF_STUDY = "isFirstDayOfStudy";
    static final String KEY_USER_COMPLETED_STEPS = "keyUserCompletedSteps";

    public static final String NOTIF_EVENT_LOGS_CSV = "notifLogs.csv";
    public static final String ANALYTICS_LOG_CSV = "analytics.csv";
    public static final String BEEHIVE_DIR = "/beehive/";
    public static String FULL_BEEHIVE_DIR = Environment.getExternalStorageDirectory().getPath() + BEEHIVE_DIR;
    public static final String PAM_LOGS_CSV = FULL_BEEHIVE_DIR + "pam.csv";

    static final String VIEWED_SCREEN_USERTIMERS = "viewed-screen-usertimers";
    static final String VIEWED_SCREEN_USERWINDOWS = "viewed-screen-userwindows";
    static final String VIEWED_SCREEN_CONGRATS = "viewed-screen-congrats";
    public static final String VIEWED_SCREEN_APPINFO = "viewed-screen-appinfo";

    public static final String CLICKED_TIMER_BUTTON = "clicked-timer-button";
    public static final String CLICKED_RESET_APP_BUTTON = "clicked-reset-button";
    public static final String CLICKED_ABOUT_BUTTON = "clicked-about-button";
    static final String CLICKED_CONGRATS_FINISH_BUTTON = "clicked-congrats-finish-button";
    static final String CLICKED_USER_WINDOW_NEXT_BUTTON = "clicked-userwindow-next-button";
    static final String CLICKED_SLEEP_WAKE_NEXT_BUTTON = "clicked-sleepwake-next-button";
    static final String SELECTED_USER_WINDOW_WEEKEND = "selected-user-window-weekend";
    static final String SELECTED_USER_WINDOW_WEEKDAY = "selected-user-window-weekday";
    static final String CHANGED_WEEKDAY_WAKE = "changed-weekday-waketime";
    static final String CHANGED_WEEKEND_WAKE = "changed-weekend-waketime";
    static final String CHANGED_WEEKDAY_SLEEP = "changed-weekday-sleeptime";
    static final String CHANGED_WEEKEND_SLEEP = "changed-weekend-sleeptime";

    static final java.lang.String VALUE_OF_CHANGED_WEEKDAY_WAKE = "value-changed-weekday-wake";
    static final java.lang.String VALUE_OF_CHANGED_WEEKEND_WAKE = "value-changed-weekend-wake" ;
    static final java.lang.String VALUE_OF_CHANGED_WEEKDAY_SLEEP = "value-changed-weekday-sleep";
    static final java.lang.String VALUE_OF_CHANGED_WEEKEND_SLEEP = "value-changed-weekend-sleep";
    static final java.lang.String VALUE_OF_CHANGED_USER_WINDOW_WEEKEND = "value-changed-user-window-weekend";
    static final java.lang.String VALUE_OF_CHANGED_USER_WINDOW_WEEKDAY = "value-changed-user-window-weekday";
    public static final String TYPE_PAM = "pam";
    public static final String TYPE_PUSH_SURVEY = "push_survey";
    public static final String TYPE_PUSH_ONE_TIME_SURVEY = "push_one_time_survey";
    public static final String TYPE_PUSH_NOTIFICATION = "push_notification";

    public static final String NOTIF_TYPE = "notif_type";
    static final String ALARM_ID = "alarmId";
    public static final String RS_TYPE = "rs_type";
    public static final String NOTIF_CONTENTS_SHOWED = "notifContentsShowed";
    public static final String LAST_DATE_PAM_SEEN = "lastDatePamSeen";
    public static final String LAST_DATE_SURVEY_SEEN = "lastDateSurveySeen";


//    final static String NOTIF_TYPE_USER_WINDOW = "user_window";
//    final static String NOTIF_TYPE_SLEEP_WAKE = "sleep_wake";
}
