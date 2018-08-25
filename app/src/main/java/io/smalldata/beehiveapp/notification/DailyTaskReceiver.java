package io.smalldata.beehiveapp.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.utils.DateHelper;

public class DailyTaskReceiver extends BroadcastReceiver {
    private Context mContext;
    private Profile mProfile;

    @Override
    public void onReceive(Context context, Intent intent) {
        startIntvForToday(context);
    }

    public static void startDaily3amTask(Context context) {
        final int alarmId = Constants.DAILY_TASK_ALARM_ID;
        Intent intent = new Intent(context, DailyTaskReceiver.class);
        intent.putExtra(Constants.NOTIFICATION_ID, alarmId);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calTime = Calendar.getInstance();
        calTime.set(Calendar.HOUR_OF_DAY, 3);
        calTime.set(Calendar.MINUTE, 0);
        calTime.set(Calendar.SECOND, 0);
        calTime.set(Calendar.MILLISECOND, 0);

        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, calTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }
    }


    public void startIntvForToday(Context context) {
        mContext = context;
        mProfile = new Profile(mContext);
        if (isNewDay() && mProfile.hasIntvForToday()) {
            if (todayIsFirstDayOfStudy()) {
                Toast.makeText(mContext, "Expect your first reminder tomorrow.", Toast.LENGTH_SHORT).show();
            } else {
                mProfile.applyIntvForToday();
            }
        }

    }

    private boolean todayIsFirstDayOfStudy() {
        return mProfile.getFirstDayOfStudy().equals(DateHelper.getTodayDateStr());
    }

    private boolean isNewDay() {
        String lastSavedDate = Profile.getLastScheduledIntvDate(mContext);
        String todayDate = DateHelper.getTodayDateStr();
        return !lastSavedDate.equals(todayDate);
    }

}
