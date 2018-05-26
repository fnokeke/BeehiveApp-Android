package io.smalldata.beehiveapp.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.fcm.InAppAnalytics;
import io.smalldata.beehiveapp.onboarding.AboutApp;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.onboarding.Step0AWelcomeStudyCode;
import io.smalldata.beehiveapp.onboarding.Step1SleepWakeTime;

public class AppInfo extends AppCompatActivity {

    private Profile mProfile;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        mProfile = new Profile(mContext);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        setTitle("Ongoing Study");
    }

    @Override
    protected void onResume() {
        setAppInfo();
        super.onResume();
        if (mProfile.userCompletedAllSteps()) {
            InAppAnalytics.add(mContext, Constants.VIEWED_SCREEN_APPINFO);
            mProfile.applyIntvForToday(); // FIXME: 5/26/18 remove debug!
        }
    }

    private void setAppInfo() {
        TextView tvStudyTitle = (TextView) findViewById(R.id.tv_study_title);
        tvStudyTitle.setText(mProfile.getStudyTitle());

        TextView tvStudyDates = (TextView) findViewById(R.id.tv_study_dates);
        tvStudyDates.setText(mProfile.getStudyDates());

        TextView tvIntv = (TextView) findViewById(R.id.tv_interventions);
        tvIntv.setText(mProfile.getAllAppliedNotifForToday());

        TextView tvParticipantSince = (TextView) findViewById(R.id.tv_participant_since);
        tvParticipantSince.setText(mProfile.getFirstDayOfStudy());

        String weekdayTime = mProfile.getUserTimeWindow("weekday");
        String weekendTime = mProfile.getUserTimeWindow("weekend");
        String times = String.format("Weekday: %s. \nWeekend: %s.", weekdayTime, weekendTime);
        TextView tvSelectedTimes = (TextView) findViewById(R.id.tv_selected_times);
        tvSelectedTimes.setText(times);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.appinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_time:
                InAppAnalytics.add(mContext, Constants.CLICKED_TIMER_BUTTON);
                startActivity(new Intent(mContext, Step1SleepWakeTime.class));
                break;
            case R.id.action_about:
                InAppAnalytics.add(mContext, Constants.CLICKED_ABOUT_BUTTON);
                startActivity(new Intent(mContext, AboutApp.class));
                break;
            case R.id.action_reset_app:
                InAppAnalytics.add(mContext, Constants.CLICKED_RESET_APP_BUTTON);
                mProfile.wipeAllData();
                startActivity(new Intent(mContext, Step0AWelcomeStudyCode.class));
                break;
        }

        return true;
    }
}
