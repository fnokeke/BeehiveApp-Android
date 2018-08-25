package io.smalldata.beehiveapp.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.fcm.InAppAnalytics;
import io.smalldata.beehiveapp.notification.DailyTaskReceiver;
import io.smalldata.beehiveapp.onboarding.AboutApp;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.onboarding.Step0AWelcomeStudyCode;
import io.smalldata.beehiveapp.onboarding.Step1SleepWakeTime;
import io.smalldata.beehiveapp.studyManagement.RSActivity;
import io.smalldata.beehiveapp.studyManagement.RSActivityManager;
import io.smalldata.beehiveapp.utils.Store;

public class AppInfo extends RSActivity {

    private Profile mProfile;
    private Context mContext;
    CheckActiveStream mCheckActiveStream;
    TextView tvIntv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        mProfile = new Profile(mContext);
        mCheckActiveStream = new CheckActiveStream(mContext);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        setTitle("Ongoing Study");
    }

    @Override
    public void onResume() {
        super.onResume();
        setAppInfo();
        confirmSetUp();
        handleRSTask();
    }

    private void confirmSetUp() {
        AppInfo.requestStoragePermission(this);
        if (mProfile.userCompletedAllSteps()) {
            InAppAnalytics.add(mContext, Constants.VIEWED_SCREEN_APPINFO);
        }
        mCheckActiveStream.confirmMonitorAppSetUp();
        mCheckActiveStream.confirmMeditationAppSetUp();
        DailyTaskReceiver.startDaily3amTask(mContext);
    }

    public static Boolean isDebugMode(Context context) {
        return Store.getBoolean(context, Constants.KEY_IS_DEBUG_MODE);
    }

    private void handleRSTask() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String rsType = bundle.getString(Constants.RS_TYPE);
            if (Constants.TYPE_PAM.equals(rsType)) {
                RSActivityManager.get().queueActivity(mContext, mProfile.generatePAMSurveyString());
            } else if (Constants.TYPE_PUSH_SURVEY.equals(rsType) || Constants.TYPE_PUSH_ONE_TIME_SURVEY.equals(rsType)) {
                String surveyJsonStr = bundle.getString(Constants.ALARM_PROTOCOL_NOTIF_DETAILS);
                RSActivityManager.get().queueActivity(mContext, mProfile.generateJSONSurveyString(surveyJsonStr));
            }
            getIntent().removeExtra(Constants.RS_TYPE);
        }
    }

    private void setAppInfo() {
        TextView tvStudyTitle = (TextView) findViewById(R.id.tv_study_title);
        tvStudyTitle.setText(mProfile.getStudyTitle());

        TextView tvStudyDates = (TextView) findViewById(R.id.tv_study_dates);
        tvStudyDates.setText(mProfile.getStudyDates());

        tvIntv = (TextView) findViewById(R.id.tv_interventions);
        tvIntv.setText(mProfile.getAllAppliedNotifForToday());
        tvIntv.setMovementMethod(new ScrollingMovementMethod());

        TextView tvParticipantSince = (TextView) findViewById(R.id.tv_participant_since);
        tvParticipantSince.setText(mProfile.getFirstDayOfStudy());

        TextView tvAppVersion = (TextView) findViewById(R.id.tv_app_version);
        tvAppVersion.setText(mProfile.getAppVersion());

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
        updateDebugMenuMode(menu);
        return true;
    }

    private void updateDebugMenuMode(Menu menu) {
        TextView tvTitle = (TextView) findViewById(R.id.tv_intv_section_title);
        if (AppInfo.isDebugMode(mContext)) {
            menu.findItem(R.id.clear_intv_list).setVisible(true);
            menu.findItem(R.id.apply_intv_now).setVisible(true);
            tvIntv.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            menu.findItem(R.id.clear_intv_list).setVisible(false);
            menu.findItem(R.id.apply_intv_now).setVisible(false);
            tvIntv.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_time:
                InAppAnalytics.add(mContext, Constants.CLICKED_TIMER_BUTTON);
                startActivity(new Intent(mContext, Step1SleepWakeTime.class));
                break;

            case R.id.link_applogger:
                InAppAnalytics.add(mContext, Constants.LINK_APPLOGGER);
                mCheckActiveStream.connectToAppLogger();
                break;

            case R.id.clear_intv_list:
                mProfile.clearAppliedNotifList();
                TextView tvIntv = (TextView) findViewById(R.id.tv_interventions);
                tvIntv.setText(mProfile.getAllAppliedNotifForToday());
                Toast.makeText(mContext, "List cleared.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.apply_intv_now:
                mProfile.applyIntvForToday(true);
                Toast.makeText(mContext, "Applying intervention...", Toast.LENGTH_SHORT).show();
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

    public static void requestStoragePermission(Activity activity) {
        requestReadStoragePermission(activity);
        requestWriteStoragePermission(activity);
    }

    public static void requestReadStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) { //permission is automatically granted on sdk<23 upon installation
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            }
        }
    }

    public static void requestWriteStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) { //permission is automatically granted on sdk<23 upon installation
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
    }

}
