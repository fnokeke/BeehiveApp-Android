package io.smalldata.beehiveapp.fcm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.VolleyError;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONObject;

import java.io.File;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.DeviceInfo;
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

    public void updateServerRecords(Context context) {
        sendAllLocalData(context);
    }

    private void sendAllLocalData(Context context) {
        sendNotifLogs(context);
        sendAllSurveyLogs(context);
        sendInAppAnalytics(context);
    }

    public static void registerMobileUserOnLoginComplete(Context context) {
        JSONObject data = new JSONObject();
        JsonHelper.setJSONValue(data, "email", Profile.getCurrentUsername(context));
        JsonHelper.setJSONValue(data, "code", Profile.getCurrentCode(context));
        JSONObject fromPhoneDetails = DeviceInfo.getPhoneDetails(context);
        JsonHelper.copy(fromPhoneDetails, data);
        CallAPI.registerMobileUser(context, data, getLogResponseHandler(context, null));
    }

    public static void sendNotifLogs(Context context) {
        String filename = Constants.NOTIF_EVENT_LOGS_CSV;
        JSONObject data = getLocalData(context, filename);
        CallAPI.submitNotifLogs(context, data, getLogResponseHandler(context, filename));
    }

    public static void sendAllSurveyLogs(Context context) {
        File directory = new File(Constants.FULL_RSUITE_SURVEY_DIR);
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            String filename = file.getPath();
            JSONObject data = getLocalData(context, filename);
            if (!data.optString("logs").equals("")) {
                if (filename.equals(Constants.PAM_LOGS_CSV)) {
                    CallAPI.submitPAMLog(context, data, getLogResponseHandler(context, filename));
                } else {
                    CallAPI.submitSurveyLog(context, data, getLogResponseHandler(context, filename));
                }
            }
        }
    }

    public static void sendInAppAnalytics(Context context) {
//        String filename = Constants.ANALYTICS_LOG_CSV;
//        JSONObject data = getLocalData(context, filename);
//        CallAPI.submitAnalytics(context, data, getLogResponseHandler(context, filename));
    }

    private static JSONObject getLocalData(Context context, String filename) {
        JSONObject params = new JSONObject();
        JsonHelper.setJSONValue(params, "email", Profile.getCurrentUsername(context));
        JsonHelper.setJSONValue(params, "code", Profile.getCurrentCode(context));
        JsonHelper.setJSONValue(params, "logs", LocalStorage.readFromFile(context, filename));
        return params;
    }

    private static VolleyJsonCallback getLogResponseHandler(final Context context, final String filenameToReset) {
        return new VolleyJsonCallback() {
            @Override
            public void onConnectSuccess(JSONObject result) {
                Log.i(TAG, filenameToReset + " Submit Response: " + result.toString());
                if (filenameToReset != null) {
                    LocalStorage.resetFile(context, filenameToReset);
                }
            }

            @Override
            public void onConnectFailure(VolleyError error) {
                String msg = "Contact researcher: " + error.toString();
                Log.e(TAG, filenameToReset + " StatsError: " + msg);
                // FIXME: 1/24/18 remove debug code
                AlarmHelper.showInstantNotif(context,
                        "At " + DateHelper.getFormattedTimestamp() + " sent failed!",
                        "Error: " + msg,
                        "",
                        8961);
            }
        };

    }

}
