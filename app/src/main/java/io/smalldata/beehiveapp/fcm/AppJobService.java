package io.smalldata.beehiveapp.fcm;

import android.content.Context;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;

public class AppJobService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        updateServerRecords(getApplicationContext());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void updateServerRecords(Context context) {
        // FIXME: 1/24/18 remove debug code
        AlarmHelper.showInstantNotif(context,
                "Dummy server update done!",
                "at: " + DateHelper.getFormattedTimestamp(),
                "",
                8960);

    }

}
