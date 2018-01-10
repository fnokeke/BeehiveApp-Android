package io.smalldata.beehiveapp.onboarding;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.utils.DateHelper;

public class Step1SleepWakeTime extends AppCompatActivity {
    private Context mContext;
    private Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step1_sleep_wake_time);

        mContext = this;
        mProfile = new Profile(mContext);

        if (!Profile.usernameExists(mContext)) {
            DummyData.applyFakeConfig(mContext); // FIXME: 1/2/18 remove
        }

        setToolbarTitle();
        activateNextButton();
        activateTimeDialog();
    }

    private void setToolbarTitle() {
        Integer numOfSteps = mProfile.getNumOfSteps();
        String title = String.format(Locale.getDefault(), "Step 1 of %d", numOfSteps);
        setTitle(title);
    }

    private void activateNextButton() {
        Button btnNext = (Button) findViewById(R.id.next_btn_to_set_user_window);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidTimeEntry()) {
                    Toast.makeText(mContext, "Enter valid wakeup time and sleep time.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!hasMinimumTimeDiff()) {
                    Toast.makeText(mContext, "Sleep time should be at least 3 hours from wake-up time.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Class<?> nextActivity = Step3OnboardingCompleted.class;
                if (mProfile.hasUserWindowEnabled()) {
                    nextActivity = Step2TimeWindow.class;
                }
                startActivity(new Intent(mContext, nextActivity));
            }
        });
    }

    private boolean isValidTimeEntry() {
        long weekdayWakeMillis = toMillis(mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_WAKE));
        long weekdaySleepMillis = toMillis(mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_SLEEP));

        long weekendWakeMillis = toMillis(mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_WAKE));
        long weekendSleepMillis = toMillis(mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_SLEEP));

        return (weekdayWakeMillis != -1 && weekdaySleepMillis != -1 &&
                weekendWakeMillis != -1 && weekendSleepMillis != -1);
    }

    private boolean hasMinimumTimeDiff() {
        long weekdayWakeMillis = toMillis(mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_WAKE));
        long weekdaySleepMillis = toMillis(mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_SLEEP));

        long weekendWakeMillis = toMillis(mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_WAKE));
        long weekendSleepMillis = toMillis(mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_SLEEP));

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

    private void activateTimeDialog() {
        Profile mProfile = new Profile(mContext);

        EditText weekdayWake = (EditText) findViewById(R.id.et_weekday_wake);
        new SetTimeDialog(mContext, Constants.KEY_WEEKDAY_WAKE, weekdayWake);
        this.populateSavedEntry(weekdayWake, mProfile.getSavedTimeIn12HourClock(Constants.KEY_WEEKDAY_WAKE));

        EditText weekdaySleep = (EditText) findViewById(R.id.et_weekday_sleep);
        new SetTimeDialog(mContext, Constants.KEY_WEEKDAY_SLEEP, weekdaySleep);
        this.populateSavedEntry(weekdaySleep, mProfile.getSavedTimeIn12HourClock(Constants.KEY_WEEKDAY_SLEEP));

        EditText weekendWake = (EditText) findViewById(R.id.et_weekend_wake);
        new SetTimeDialog(mContext, Constants.KEY_WEEKEND_WAKE, weekendWake);
        this.populateSavedEntry(weekendWake, mProfile.getSavedTimeIn12HourClock(Constants.KEY_WEEKEND_WAKE));

        EditText weekendSleep = (EditText) findViewById(R.id.et_weekend_sleep);
        new SetTimeDialog(mContext, Constants.KEY_WEEKEND_SLEEP, weekendSleep);
        this.populateSavedEntry(weekendSleep, mProfile.getSavedTimeIn12HourClock(Constants.KEY_WEEKEND_SLEEP));
    }

    public void populateSavedEntry(EditText editText, String timeEntryValue) {
        editText.setText(timeEntryValue);
    }
}
