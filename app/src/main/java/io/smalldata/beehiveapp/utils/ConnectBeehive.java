package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.onboarding.Step0BLoginUser;
import io.smalldata.beehiveapp.main.AutoUpdateAlarm;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.onboarding.Profile;

/**
 * Help connect user to Beehive Researcher Study
 * Created by fnokeke on 5/18/17.
 */

public class ConnectBeehive {
    private final static String TAG = "ConnectBeehive";
    private Context mContext;
    private TextView tvFeedback;
//    private Experiment experiment;
    private Profile mProfile;

    public ConnectBeehive(Context context, TextView feedback) {
        mContext = context;
        mProfile = new Profile(context);
        tvFeedback = feedback;
//        experiment = new Experiment(context);
    }

    public ConnectBeehive(Context context) {
        mContext = context;
    }

    public void fetchStudyUsingCode(String code) {
        JSONObject data = new JSONObject();
        JsonHelper.setJSONValue(data, "code", code);
        Display.showBusy(mContext, "Verifying code...");
        CallAPI.fetchStudy(mContext, data, fetchStudyResponseHandler);
    }

    private VolleyJsonCallback fetchStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject jsonResult) {
            Display.dismissBusy();
            Display.showSuccess(tvFeedback, "Successfully connected!");
            mProfile.saveStudyConfig(jsonResult);
            FirebaseMessaging.getInstance().subscribeToTopic(mProfile.getStudyCode());
            beginLoginProcess();
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.i(TAG, "fetchStudyFailure: " + error.toString());
            Display.dismissBusy();
            if (error.toString().contains("NoConnectionError")) {
                Display.showError(tvFeedback, "You don't have network connection.");
            } else {
                Display.showError(tvFeedback, "Uh oh...invalid code.");
                // FIXME: 1/10/18 wrong study code should not land in onConnectFailure
            }
        }
    };

    public void updateStudyThenApplyAnyInstantSurvey(Context context, String code) {
        JSONObject data = new JSONObject();
        JsonHelper.setJSONValue(data, "code", code);
        mProfile = new Profile(context);
        CallAPI.updateStudy(context, data, updateStudyResponseHandler);
    }

    private VolleyJsonCallback updateStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject jsonResult) {
            mProfile.saveStudyConfig(jsonResult);
            mProfile.applyThenRemoveOneTimeProtocols();
            Toast.makeText(mContext, "Successfully updated Beehive study.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.i(TAG, "updateStudyFailure: " + error.toString());
            AlarmHelper.showInstantNotif(mContext, "Error updating study: " + error.toString(), DateHelper.getFormattedTimestamp(), "", 2023);
        }
    };

    private void beginLoginProcess() {
        mContext.startActivity(new Intent(mContext, Step0BLoginUser.class));
    }

}
