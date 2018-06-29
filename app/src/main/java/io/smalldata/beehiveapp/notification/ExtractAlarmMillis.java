package io.smalldata.beehiveapp.notification;

import android.content.Context;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import io.smalldata.beehiveapp.onboarding.Profile;

/**
 * Created by fnokeke on 1/3/18.
 * Handle operations to transform notification time to actual alarm time
 */

public class ExtractAlarmMillis {

    /**
     * @param protocol e.g. notif_time: 15:30
     * @return time in milliseconds
     */
    public static long getFixedMillis(JSONObject protocol) {
        String[] timeArr = protocol.optString("notif_time").split(":");
        Calendar cal = getTodayCalendarTime(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]));
        return cal.getTimeInMillis();
    }

    /**
     * @param protocol e.g. notif_time: 3_hour
     * @return time in milliseconds
     */
    public static long getUserWindowMillis(Context context, JSONObject protocol) {
        String[] windowArr = protocol.optString("notif_time").split("_");
        int windowHourDuration = Integer.parseInt(windowArr[0]);
        String userSelectedWindow = getUserWindowForToday(context);
        return fromUserWindowToMillis(userSelectedWindow, windowHourDuration);
    }

    /**
     * @param protocol e.g. 1_hour;before_sleep, 2_hour;after_wake
     * @return time in milliseconds
     */
    public static long getSleepWakeMillis(Context context, JSONObject protocol) {
        String[] sleepWakeArr = protocol.optString("notif_time").split(";");
        String[] hourSleepWakeArr = sleepWakeArr[0].split("_");
        String sleepWakeMode = sleepWakeArr[1]; // e.g. before_sleep, after_wake
        int sleepWakeHourDuration = Integer.parseInt(hourSleepWakeArr[0]); // e.g. 1, 2
        return fromSleepWakeToMillis(context, sleepWakeMode, sleepWakeHourDuration);
    }

    /**
     * @param context               application context
     * @param sleepWakeMode:        two options: before_sleep or after_wakeup
     * @param sleepWakeHourDuration e.g. 2 from 2_hour;before_sleep
     * @return time in milliseconds
     */
    private static long fromSleepWakeToMillis(Context context, String sleepWakeMode, int sleepWakeHourDuration) {
        String hrMin24Clock = Profile.getWakeTimeForToday(context); //24hr format e.g. 23:30
        if (sleepWakeMode.equals("before_sleep")) {
            hrMin24Clock = Profile.getSleepTimeForToday(context);
        }

        String[] timeArr = hrMin24Clock.split(":");
        int alarmHour = Integer.parseInt(timeArr[0]);
        int alarmMinutes = Integer.parseInt(timeArr[1]);
        Calendar cal = getTodayCalendarTime(alarmHour, alarmMinutes);
        long alarmMillis = cal.getTimeInMillis();
        int randomMins = getRandomInt(sleepWakeHourDuration * 60);

        final long ONE_MINUTE = 60 * 1000;
        if (sleepWakeMode.equals("before_sleep")) {
            alarmMillis -= randomMins * ONE_MINUTE;
        } else { // after_wakeup
            alarmMillis += randomMins * ONE_MINUTE;
        }
        return extendToNextDayIfBehindNow(alarmMillis);
    }

    // move to next day is necessary when user selects sleep time past midnight
    private static long extendToNextDayIfBehindNow(long timeMillis) {
        if (timeMillis <= Calendar.getInstance().getTimeInMillis()) {
            final long ONE_DAY = 24 * 60 * 60 * 1000;
            timeMillis += ONE_DAY;
        }
        return timeMillis;
    }

    private static String getUserWindowForToday(Context context) {
        Profile mProfile = new Profile(context);
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return mProfile.getUserTimeWindow("weekend");
        }
        return mProfile.getUserTimeWindow("weekday");
    }


    /**
     * @param userSelectedWindow e.g. 3pm to 6pm
     * @return time in milliseconds
     */
    private static long fromUserWindowToMillis(String userSelectedWindow, int userWindowDurationInHour) {
        String[] userWindowArr = userSelectedWindow.split(" to ");
        int alarmHour = getClockHour(userWindowArr[0]);
        int alarmMinutes = getRandomInt(userWindowDurationInHour * 60);
        Calendar cal = getTodayCalendarTime(alarmHour, alarmMinutes);
        return cal.getTimeInMillis();
//        return extendToNextDayIfBehindNow(cal.getTimeInMillis());
    }

    private static int getRandomInt(int limit) {
        return new Random().nextInt(limit + 1);
    }

    /**
     * @param timeAmPm e.g. 3pm, 6pm
     * @return hour of time e.g. 15, 18 respectively
     */
    private static int getClockHour(String timeAmPm) {
        if (timeAmPm.equals("Midnight")) {
            return 0;
        } else if (timeAmPm.equals("Noon")) {
            return 12;
        } else if (timeAmPm.contains("am")) {
            return Integer.parseInt(timeAmPm.split("am")[0]);
        } else {
            return 12 + Integer.parseInt(timeAmPm.split("pm")[0]);
        }
    }

    private static Calendar getTodayCalendarTime(int hour, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }


}
