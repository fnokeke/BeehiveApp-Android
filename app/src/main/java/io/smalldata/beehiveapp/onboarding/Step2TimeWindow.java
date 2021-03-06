package io.smalldata.beehiveapp.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.fcm.InAppAnalytics;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;

public class Step2TimeWindow extends AppCompatActivity {

    private Context mContext;
    Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step2_time_window);
        mContext = this;
        mProfile = new Profile(mContext);

        setTitle("Step 2 of 3");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btnNext = (Button) findViewById(R.id.img_btn_next_weekend_screen);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InAppAnalytics.add(mContext, Constants.CLICKED_USER_WINDOW_NEXT_BUTTON);
                startActivity(new Intent(getApplicationContext(), Step3OnboardingCompleted.class));
            }
        });

        activateDropDown("weekday");
        activateDropDown("weekend");
    }

    @Override
    protected void onResume() {
        super.onResume();
        InAppAnalytics.add(mContext, Constants.VIEWED_SCREEN_USERWINDOWS);
    }

    private void activateDropDown(final String dayType) {
        int spinnerId = R.id.spinner_weekday;
        if (dayType.equals("weekend")) {
            spinnerId = R.id.spinner_weekend;
        }

        Spinner spinner = (Spinner) findViewById(spinnerId);
        final String[] dropDownItems = getDropdownTimeWindow(dayType);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, dropDownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(mProfile.getLastSavedPosition(dayType));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                mProfile.saveUserSelectedTimeWindow(dayType, item);
                mProfile.saveSelectedPosition(dayType, position);

                if (dayType.equals("weekend")) {
                    InAppAnalytics.add(mContext, Constants.SELECTED_USER_WINDOW_WEEKEND);
                    InAppAnalytics.add(mContext, Constants.VALUE_OF_CHANGED_USER_WINDOW_WEEKEND, item);
                } else {
                    InAppAnalytics.add(mContext, Constants.SELECTED_USER_WINDOW_WEEKDAY);
                    InAppAnalytics.add(mContext, Constants.VALUE_OF_CHANGED_USER_WINDOW_WEEKDAY, item);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    private String[] getDropdownTimeWindow(String typeOfDay) {
        String wakeTime = mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_WAKE);
        String sleepTime = mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKDAY_SLEEP);

        if (typeOfDay.equals("weekend")) {
            wakeTime = mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_WAKE);
            sleepTime = mProfile.getSavedTimeIn24HourClock(Constants.KEY_WEEKEND_SLEEP);
        }

        if (mProfile.hasSleepWakeEnabled()) {
            String[] endHrMins = sleepTime.split(":");
            int newEndHour = Integer.parseInt(endHrMins[0]);
            newEndHour -= mProfile.getSleepWakeHourDuration(); // e.g. 10:35pm minus 1 hour (from 1_hour;before_sleep)
            sleepTime = newEndHour + ":" + endHrMins[1];
        }

        return GenerateUserWindow.generateWindowList(wakeTime, sleepTime, mProfile.getUserWindowHourDuration());
    }


}


