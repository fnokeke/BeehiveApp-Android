package io.smalldata.beehiveapp.fcm;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONObject;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.JsonHelper;

public class AppJobService extends JobService {

    private static final String TAG = "BeehiveAppJobService";

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
        sendAllLocalData(context);
    }

    private void sendAllLocalData(Context context) {
        sendNotifLogs(context);
    }

    private void sendNotifLogs(Context context) {
        JSONObject params = getLogParams(context, Constants.NOTIF_LOGS_CSV);
        CallAPI.submitNotifLogs(context, params, getLogResponseHandler(context, Constants.NOTIF_LOGS_CSV));
    }

    private static JSONObject getLogParams(Context context, String filename) {
        JSONObject params = new JSONObject();
        JsonHelper.setJSONValue(params, "username", Profile.getCurrentUsername(context));
        JsonHelper.setJSONValue(params, "code", Profile.getCurrentCode(context));
        JsonHelper.setJSONValue(params, "logs", LocalStorage.readFromFile(context, filename));
        return params;
    }

    private static VolleyJsonCallback getLogResponseHandler(final Context context, final String filenameToReset) {
        return new VolleyJsonCallback() {
            @Override
            public void onConnectSuccess(JSONObject result) {
                Log.i(TAG, filenameToReset + " Submit Response: " + result.toString());
                LocalStorage.resetFile(context, filenameToReset);

                // FIXME: 1/24/18 remove debug code
                AlarmHelper.showInstantNotif(context,
                        "Successfully sent all local data!",
                        "at: " + DateHelper.getFormattedTimestamp(),
                        "",
                        8960);

            }

            @Override
            public void onConnectFailure(VolleyError error) {
                String msg = "Stats submit error: " + error.toString();
                Log.e(TAG, filenameToReset + " StatsError: " + msg);
                // FIXME: 1/24/18 remove debug code
                AlarmHelper.showInstantNotif(context,
                        "At "+ DateHelper.getFormattedTimestamp() + " sent failed!",
                        "Error: " + msg,
                        "",
                        8961);
            }
        };

    }
}
