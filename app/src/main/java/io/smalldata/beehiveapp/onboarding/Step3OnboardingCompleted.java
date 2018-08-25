package io.smalldata.beehiveapp.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.fcm.InAppAnalytics;
import io.smalldata.beehiveapp.main.AppInfo;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.Store;

public class Step3OnboardingCompleted extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step3_onboarding_completed);

        mContext = this;
        final Profile mProfile = new Profile(mContext);
        Integer numOfSteps = mProfile.getNumOfSteps();
        String title = String.format(Locale.getDefault(), "Step %d of %d", numOfSteps, numOfSteps);
        setTitle(title);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button btnNext = (Button) findViewById(R.id.img_btn_close_congrats);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InAppAnalytics.add(mContext, Constants.CLICKED_CONGRATS_FINISH_BUTTON);
                mProfile.indicateUserCompletedSteps();

                if (!alreadyScheduledTodayIntv()) {
                    resetLastSavedIntvDate();
                }

                startActivity(new Intent(mContext, AppInfo.class));
            }
        });

    }

    private void resetLastSavedIntvDate() {
        Store.setString(mContext, Constants.KEY_LAST_SAVED_DATE, "");
    }


    @Override
    protected void onResume() {
        super.onResume();
        InAppAnalytics.add(mContext, Constants.VIEWED_SCREEN_CONGRATS);
    }

    public boolean alreadyScheduledTodayIntv() {
        return DateHelper.getTodayDateStr().equals(Profile.getLastScheduledIntvDate(mContext));
    }
}
