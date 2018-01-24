package io.smalldata.beehiveapp.onboarding;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import io.smalldata.beehiveapp.notification.DailyTaskReceiver;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Created by fnokeke on 1/2/18.
 * Handle intervention
 */

public class TriggerIntervention {
    private Context mContext;
    private Profile mProfile;

    public TriggerIntervention(Context context) {
        mContext = context;
        mProfile = new Profile(mContext);
    }

    public static void startDaily3amTask(Context context, boolean isUserTriggered) {
        AlarmHelper.showInstantNotif(context, "Start 3am Task",
                DateHelper.getFormattedTimestamp(),
                "", 7711); // FIXME: 1/15/18 remove debug

        if (isUserTriggered) {
            resetLastSavedIntvDate(context);
        }

        final int alarmId = Constants.DAILY_TASK_ALARM_ID;
        Intent intent = new Intent(context, DailyTaskReceiver.class);
        intent.putExtra(Constants.NOTIFICATION_ID, alarmId);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal4am = Calendar.getInstance();
        cal4am.set(Calendar.HOUR_OF_DAY, 3);
        cal4am.set(Calendar.MINUTE, 0);
        cal4am.set(Calendar.SECOND, 0);
        cal4am.set(Calendar.MILLISECOND, 0);

        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal4am.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }
    }

    private static void resetLastSavedIntvDate(Context context) {
        Store.setString(context, Constants.KEY_LAST_SAVED_DATE, "");
    }


    public void startIntvForToday() {
        if (isNewDay() && todayIntvExists() && !todayIsFirstDayOfStudy()) {
            mProfile.applyIntvForToday();
        }
    }

    private boolean todayIsFirstDayOfStudy() {
        return mProfile.getFirstDayOfStudy().equals(DateHelper.getTodayDateStr());
    }

    private boolean isNewDay() {
        String lastSavedDate = Profile.getLastAppliedIntvDate(mContext);
        String todayDate = DateHelper.getTodayDateStr();
        return !lastSavedDate.equals(todayDate);
    }

    private boolean todayIntvExists() {
        return mProfile.hasIntvForToday();
    }

}
