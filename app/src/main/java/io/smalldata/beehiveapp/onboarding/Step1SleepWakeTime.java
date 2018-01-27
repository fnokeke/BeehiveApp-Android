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
import io.smalldata.beehiveapp.fcm.InAppAnalytics;

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

    @Override
    protected void onResume() {
        super.onResume();
        InAppAnalytics.add(mContext, Constants.VIEWED_SCREEN_USERTIMERS);
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
                InAppAnalytics.add(mContext, Constants.CLICKED_SLEEP_WAKE_NEXT_BUTTON);

                if (!mProfile.hasValidTimeEntry()) {
                    Toast.makeText(mContext, "Enter valid wakeup time and sleep time.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mProfile.hasMinimumTimeDiff()) {
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
