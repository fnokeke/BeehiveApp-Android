package io.smalldata.beehiveapp.onboarding;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import io.smalldata.beehiveapp.notification.ExtractAlarmMillis;
import io.smalldata.beehiveapp.notification.NewAlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Created by fnokeke on 12/17/17.
 * Profile to manage study data
 */

public class Profile {
    private Context mContext;

    public Profile(Context context) {
        mContext = context;
    }

    void saveFirstname(String firstname) {
        Store.setString(mContext, Constants.FIRSTNAME, firstname);
    }

    public void saveStudyConfig(JSONObject jsonResult) {
        Store.setString(mContext, Constants.STUDY_CONFIG_KEY, jsonResult.toString());
    }

    private JSONObject getStudyConfig() {
        return JsonHelper.strToJsonObject(Store.getString(mContext, Constants.STUDY_CONFIG_KEY));
    }

    public static String getCurrentUsername(Context context) {
        return Store.getString(context, Constants.USERNAME);
    }

    public static String getCurrentCode(Context context) {
        JSONObject jo = JsonHelper.strToJsonObject(Store.getString(context, Constants.STUDY_CONFIG_KEY));
        String code = "";
        JSONObject experiment = jo.optJSONObject("experiment");
        if (experiment != null) {
            code = jo.optJSONObject("experiment").optString("code");
        }
        return code;
    }

    void saveUsername(String username) {
        Store.setString(mContext, Constants.USERNAME, username.split("#")[0]);
    }

    public String getUsername() {
        return Store.getString(mContext, Constants.USERNAME);
    }

    public String getStudyCode() {
        return getStudyConfig().optJSONObject("experiment").optString("code");
    }

    static boolean usernameExists(Context context) {
        return !fetchUsername(context).equals("");
    }

    private static String fetchUsername(Context context) {
        return Store.getString(context, Constants.USERNAME);
    }

    int getNumOfSteps() {
        int steps = 2;
        if (this.hasUserWindowEnabled()) {
            steps = 3;
        }
        return steps;
    }

    boolean hasUserWindowEnabled() {
        boolean exists = false;
        JSONArray protocols = JsonHelper.strToJsonArray(this.getStudyConfig().optString("protocols"));
        for (int i = 0; i < protocols.length(); i++) {
            JSONObject p = protocols.optJSONObject(i);
            if (p.optString("notif_type").equals("user_window")) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    void saveEditTextTimeEntry(String timeEntryKey, String timeEntryValue) {
        Store.setString(mContext, timeEntryKey, timeEntryValue);
    }

    String getSavedTimeIn24HourClock(String timeEntryKey) {
        return Store.getString(mContext, timeEntryKey);
    }

    String getSavedTimeIn12HourClock(String timeEntryKey) {
        String time24Hour = Store.getString(mContext, timeEntryKey);
        if (time24Hour.equals("")) return time24Hour;
        return SetTimeDialog.to12HourFormat(time24Hour);
    }

    boolean hasSleepWakeEnabled() {
        boolean exists = false;
        JSONArray protocols = JsonHelper.strToJsonArray(this.getStudyConfig().optString("protocols"));
        for (int i = 0; i < protocols.length(); i++) {
            JSONObject p = protocols.optJSONObject(i);
            if (p.optString("notif_type").equals("sleep_wake")) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    int getSleepWakeHourDuration() {
        int duration = 0;
        JSONArray protocols = JsonHelper.strToJsonArray(this.getStudyConfig().optString("protocols"));
        for (int i = 0; i < protocols.length(); i++) {
            JSONObject p = protocols.optJSONObject(i);
            if (p.optString("notif_type").equals("sleep_wake")) {
                String notif_time = p.optString("notif_time"); // e.g. notif_time: "1_hour;before_sleep"
                duration = Integer.parseInt(notif_time.split("_")[0]);
                break;
            }
        }
        return duration;
    }

    int getUserWindowHourDuration() {
        int duration = 0;
        JSONArray protocols = JsonHelper.strToJsonArray(this.getStudyConfig().optString("protocols"));
        for (int i = 0; i < protocols.length(); i++) {
            JSONObject p = protocols.optJSONObject(i);
            if (p.optString("notif_type").equals("user_window")) {
                String notif_time = p.optString("notif_time"); // e.g. notif_time: "3_hour"
                duration = Integer.parseInt(notif_time.split("_")[0]);
                break;
            }
        }
        return duration;
    }

    boolean hasIntvForToday() {
        boolean exists = false;
        JSONArray protocols = JsonHelper.strToJsonArray(this.getStudyConfig().optString("protocols"));
        for (int i = 0; i < protocols.length(); i++) {
            JSONObject p = protocols.optJSONObject(i);
            if (coversToday(p)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    private boolean coversToday(JSONObject protocol) {
        return isWithinDateRange(protocol) && isWithinFrequency(protocol);
    }

    private static boolean isWithinFrequency(JSONObject protocol) {
        String freqStr = protocol.optString("frequency");
        if (freqStr.equals("daily")) {
            return true;
        }

        Date startDate = DateHelper.strToDate(protocol.optString("start_date"));
        Date todayDate = DateHelper.strToDate(DateHelper.getTodayDateStr());
        long diffInDays = (todayDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24);
        return diffInDays % freqStrToInt(freqStr) == 0;
    }

    private static int freqStrToInt(String freqStr) {
        int freqNum;
        switch (freqStr) {
            case "weekly":
                freqNum = 7;
                break;
            case "biweekly":
                freqNum = 14;
                break;
            default:
                throw new UnsupportedOperationException("Protocol freq should have valid input.");
        }
        return freqNum;
    }

    private static boolean isWithinDateRange(JSONObject protocol) {
        Date startDate = DateHelper.strToDate(protocol.optString("start_date"));
        Date endDate = DateHelper.strToDate(protocol.optString("end_date"));
        long rightNow = java.util.Calendar.getInstance().getTimeInMillis();
        return rightNow >= startDate.getTime() && rightNow <= endDate.getTime();
    }

    public void applyIntvForToday() {
        Toast.makeText(mContext, "Apply Intv for today", Toast.LENGTH_SHORT).show();
        JSONArray protocols = JsonHelper.strToJsonArray(this.getStudyConfig().optString("protocols"));
        for (int i = 0; i < protocols.length(); i++) {
            JSONObject p = protocols.optJSONObject(i);
            if (coversToday(p) ) {
//                if (canContinueAfterCoinToss(p)) {
                    if (true) { // fixme: remove this
                    extractThenScheduleNotif(p);
                } else {
                    Toast.makeText(mContext, "Coin fail: " + p.optString("label"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void extractThenScheduleNotif(JSONObject protocol) {
        // There could be multiple notifications available (one per line) so randomly select one to show
        // There could be multiple app ids so randomly select one to launch
        String[] chosen = chooseTitleContentAppId(protocol);
        JSONObject notif = new JSONObject();
        JsonHelper.setJSONValue(notif, Constants.ALARM_ID, getNotifId(protocol));
        JsonHelper.setJSONValue(notif, "method", protocol.optString("method"));
        JsonHelper.setJSONValue(notif, "title", chosen[0]);
        JsonHelper.setJSONValue(notif, "content", chosen[1]);
        JsonHelper.setJSONValue(notif, "appIdToLaunch", chosen[2]);
        JsonHelper.setJSONValue(notif, "notifId", chosen[3]);
        JsonHelper.setJSONValue(notif, "alarmMillis", getAlarmMillis(protocol));
        JsonHelper.setJSONValue(notif, Constants.NOTIF_TYPE, protocol.optString("notif_type"));


        if (protocol.optString("label").contains("headspace")) { // FIXME: 5/30/18 debug remove
            Toast.makeText(mContext, "Coin success:" + protocol.optString("label"), Toast.LENGTH_SHORT).show();
        }
        JsonHelper.setJSONValue(notif, "alarmMillis", System.currentTimeMillis()); // fixme: remove debug


        NewAlarmHelper.scheduleIntvReminder(mContext, notif);
//        markTodayAsIntvApplied(); // fixme: undo comment remove debug
//        saveToNotifAppliedToday(notif); // fixme: undo comment remove debug
    }

    private String[] chooseTitleContentAppId(JSONObject protocol) {
        switch (protocol.optString("method")) {
            case Constants.TYPE_PAM:
                return new String[]{"New PAM survey.", "Tap here to see.", "pam", "4004"};

            case Constants.TYPE_PUSH_SURVEY:
                return new String[]{"You have a new survey.", "Tap here to view.", "push_survey", "5005"};

            case Constants.TYPE_PUSH_NOTIFICATION:
                // get title - content
                String notifDetailsPairs = protocol.optString("notif_details");
                String[] pairs = notifDetailsPairs.split("\n");
                String tmpChosen = pairs[getRandom(pairs.length)];
                String titleContentArr[] = tmpChosen.split(",");
                // get app id
                String notifAppIdsList = protocol.optString("notif_appid");
                String[] pairsAppId = notifAppIdsList.split("\n");
                String chosenAppId = pairsAppId[getRandom(pairsAppId.length)];
                return new String[]{titleContentArr[0], titleContentArr[1], chosenAppId, "6006"};

            default:
                throw new UnsupportedOperationException("Protocol type does not exist");

        }
    }

    private int getRandom(int limit) {
        return new Random().nextInt(limit);
    }

    private void markTodayAsIntvApplied() {
        Store.setString(mContext, Constants.KEY_LAST_SAVED_DATE, DateHelper.getTodayDateStr());
    }

    public static String getLastAppliedIntvDate(Context context) {
        return Store.getString(context, Constants.KEY_LAST_SAVED_DATE);
    }

    private void saveToNotifAppliedToday(JSONObject notif) {
        String allNotif = getAllAppliedNotifForToday();
        String infoFromNewNotif = String.format("%s (%s)", notif.optString("title"), DateHelper.millisToDateFormat(notif.optLong("alarmMillis")));
        allNotif = String.format("%s; %s", allNotif, infoFromNewNotif);
        Store.setString(mContext, Constants.KEY_TODAY_NOTIF_APPLIED, allNotif);
    }

    public String getAllAppliedNotifForToday() {
        return Store.getString(mContext, Constants.KEY_TODAY_NOTIF_APPLIED);
    }

    void clearExistingAppInfoNotifText() {
        Store.setString(mContext, Constants.KEY_TODAY_NOTIF_APPLIED, "");
    }

    /**
     * if probable_half_notify is false then it means that user should always see notification
     * but if probable_half_notify is true then fair coin must be tossed and if the result is
     * above 0.5 then user can see notification for that day otherwise notification isn't shown
     *
     * @param protocol containing field 'probable_half_notify'
     * @return boolean indicating if notification should happen (true) or not (false)
     */
    private boolean canContinueAfterCoinToss(JSONObject protocol) {
        boolean probableNotify = protocol.optBoolean("probable_half_notify");
        if (!probableNotify) return true;
        return tossFairCoin() > 0.5;
    }

    /**
     * @return generate double between 0 and 1
     */
    private static double tossFairCoin() {
        int min = 1;
        int max = 10;
        int range = max - min + 1;
        return (new Random().nextInt(range) + min) / max;
    }

    // FIXME: 1/7/18 push 2am alarm next day
    private long getAlarmMillis(JSONObject protocol) {
        long alarmMillis = -1;
        switch (protocol.optString("notif_type")) {
            case "sleep_wake":
                alarmMillis = ExtractAlarmMillis.getSleepWakeMillis(mContext, protocol);
                break;
            case "user_window":
                alarmMillis = ExtractAlarmMillis.getUserWindowMillis(mContext, protocol);
                break;
            case "fixed":
                alarmMillis = ExtractAlarmMillis.getFixedMillis(protocol);
                break;

        }
        return alarmMillis;
    }

    private int getNotifId(JSONObject protocol) {
        int notifId;
        String alarmType = protocol.optString("notif_type");
        switch (alarmType) {
            case "sleep_wake":
                notifId = 3337;
                break;
            case "user_window":
                notifId = 4447;
                break;
            case "fixed":
                notifId = 5557;
                break;
            default:
                throw new UnsupportedOperationException("Protocol NotifId should have its own value");
        }
        return notifId;

    }

    void saveUserSelectedTimeWindow(String dayType, String item) {
        if (dayType.equals("weekend")) {
            Store.setString(mContext, Constants.KEY_WEEKEND_USER_WINDOW, item);
        } else {
            Store.setString(mContext, Constants.KEY_WEEKDAY_USER_WINDOW, item);
        }
    }

    public String getUserTimeWindow(String dayType) {
        if (dayType.equals("weekend")) {
            return Store.getString(mContext, Constants.KEY_WEEKEND_USER_WINDOW);
        }
        return Store.getString(mContext, Constants.KEY_WEEKDAY_USER_WINDOW);
    }

    public static String getSleepTimeForToday(Context context) {
        Profile mProfile = new Profile(context);
        if (NewAlarmHelper.todayIsWeekend()) {
            return mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_SLEEP);
        }
        return mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_SLEEP);
    }

    public static String getWakeTimeForToday(Context context) {
        Profile mProfile = new Profile(context);
        if (NewAlarmHelper.todayIsWeekend()) {
            return mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_WAKE);
        }
        return mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_WAKE);
    }

    public String getStudyTitle() {
        String title = getStudyConfig().optJSONObject("experiment").optString("title");
        String description = getStudyConfig().optJSONObject("experiment").optString("description");
        return String.format("%s: %s", title, description);
    }

    public String getStudyDates() {
        String startDate = getStudyConfig().optJSONObject("experiment").optString("start_date");
        String endDate = getStudyConfig().optJSONObject("experiment").optString("end_date");
        return String.format("%s to %s", startDate, endDate);
    }

    public void wipeAllData() {
        Store.wipeAll(mContext);
    }

    public void setTodayAsFirstDay() {
        Store.setString(mContext, Constants.KEY_FIRST_DAY_OF_STUDY, DateHelper.getTodayDateStr());
    }

    public String getFirstDayOfStudy() {
        return Store.getString(mContext, Constants.KEY_FIRST_DAY_OF_STUDY);
    }

    public boolean hasValidTimeEntry() {
        long weekdayWakeMillis = toMillis(getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_WAKE));
        long weekdaySleepMillis = toMillis(getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_SLEEP));

        long weekendWakeMillis = toMillis(getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_WAKE));
        long weekendSleepMillis = toMillis(getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_SLEEP));

        return (weekdayWakeMillis != -1 && weekdaySleepMillis != -1 &&
                weekendWakeMillis != -1 && weekendSleepMillis != -1);
    }

    public boolean hasMinimumTimeDiff() {
        long weekdayWakeMillis = toMillis(getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_WAKE));
        long weekdaySleepMillis = toMillis(getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_SLEEP));

        long weekendWakeMillis = toMillis(getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_WAKE));
        long weekendSleepMillis = toMillis(getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_SLEEP));

        long minimumDiff = 3 * 60 * 60 * 1000; // 3 hours
        return Math.abs(weekdaySleepMillis - weekdayWakeMillis) >= minimumDiff &&
                Math.abs(weekendSleepMillis - weekendWakeMillis) >= minimumDiff;
    }

    long toMillis(String hrMinStr) {
        if (hrMinStr.equals("")) return -1;

        String[] timeArr = hrMinStr.split(":");
        Calendar cal = Calendar.getInstance(); // TODO: 1/3/18 refactor code here
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArr[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(timeArr[1]));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public void indicateUserCompletedSteps() {
        Store.setBoolean(mContext, Constants.KEY_USER_COMPLETED_STEPS, true);
    }

    public boolean userCompletedAllSteps() {
        return Store.getBoolean(mContext, Constants.KEY_USER_COMPLETED_STEPS);
    }

    void saveSelectedPosition(String dayType, int position) {
        String key = Constants.KEY_ITEM_POSITION_WEEKDAY;
        if (dayType.equals("weekend")) {
            key = Constants.KEY_ITEM_POSITION_WEEKEND;
        }
        Store.setInt(mContext, key, position);
    }

    int getLastSavedPosition(String dayType) {
        String key = Constants.KEY_ITEM_POSITION_WEEKDAY;
        if (dayType.equals("weekend")) {
            key = Constants.KEY_ITEM_POSITION_WEEKEND;
        }
        return Store.getInt(mContext, key);
    }
}

// TODO: 1/3/18 implement halfNotify
// TODO: 1/3/18 what happens when someone schedules alarm at 12:05am from previous day but your intv resets at midnight